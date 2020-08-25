package com.google.netpcapanalysis.dao;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.CacheBuilder.Policy;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import com.google.netpcapanalysis.models.DNSRecord;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReverseDNSLookupDaoImpl implements ReverseDNSLookupDao {

  public static class GoogleDNS {

    public static class GoogleDNSQuestion {

      public String name;
      public Integer type;
    }

    private static class GoogleDNSAnswer {

      public String name;
      public Integer type;
      public Integer TTL;
      public String data;
    }

    public int Status;
    public boolean TC;
    public boolean RD;
    public boolean RA;
    public boolean AD;
    public boolean CD;
    public GoogleDNSQuestion[] Question;
    public GoogleDNSAnswer[] Answer;
    public GoogleDNSAnswer[] Authority;
  }

  private static final ForkJoinPool POOL = new ForkJoinPool();
  private static final RateLimiter rateLimiter = RateLimiter.create(500.0);
  private static boolean dnsSwitch;

  private static final String DNS_REGEX =
      "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)(?=.\\s[0-9\\s]+)?";
  private Pattern dnsPattern;
  public Cache<String, DNSRecord> cache;

  public ReverseDNSLookupDaoImpl() {
    this(false);
  }

  public ReverseDNSLookupDaoImpl(boolean memory) {
    dnsPattern = Pattern.compile(DNS_REGEX);
    cache =
        new CacheBuilder<String, DNSRecord>()
            .setCacheName("reversedns")
            .setCacheType(memory ? CacheType.MEMORY : CacheType.DATASTORE)
            .setKVClass(String.class, DNSRecord.class)
            .setPolicy(Policy.MAXIMUM_SIZE)
            .setPolicyArgument(10000)
            .enableStatistics(true)
            .build();
  }

  public List<DNSRecord> parallelLookup(List<String> ips) {
    if (ips == null || ips.size() == 0) {
      return new ArrayList<>();
    }
    DNSRecord[] resArr = new DNSRecord[ips.size()];
    POOL.invoke(new LookupTask(ips, resArr, 0, ips.size() - 1));
    return Arrays.asList(resArr);
  }

  private class LookupTask extends RecursiveAction {
    private static final int THREAD_REQUESTS = 1;
    private List<String> ips;
    private DNSRecord[] records;
    private int start;
    private int end;

    /**
     * @param ips
     * @param records
     * @param start   start is inclusive
     * @param end     end is inclusive
     */
    public LookupTask(List<String> ips, DNSRecord[] records, int start, int end) {
      this.ips = ips;
      this.records = records;
      this.start = start;
      this.end = end;
    }

    @Override
    protected void compute() {
      if (end - start > THREAD_REQUESTS) { // + 1 since inclusive
        int mid = start + (end - start) / 2; // prevents integer overflow on large nums
        LookupTask left = new LookupTask(ips, records, start, mid);
        LookupTask right = new LookupTask(ips, records, mid, end);

        left.fork();
        right.compute();
        left.join();
      } else {
        if (end >= start) {
          for (int i = start; i <= end; i++) {
            records[i] = lookup(ips.get(i));
          }
        }
      }
    }
  }


  public DNSRecord lookup(String ip) {
    DNSRecord cached;
    if ((cached = cache.get(ip)) != null) {
      return cached;
    }
    try {
      String request = dnsRequest(ip);
      GoogleDNS res = new Gson().fromJson(request, GoogleDNS.class);
      DNSRecord record = createRecordFromGoogleDNS(res, ip);
      cache.put(ip, record);
      return record;
    } catch (Exception e) {
      return null;
    }
  }

  public List<DNSRecord> lookup(List<String> ips) {
    if (ips == null || ips.size() == 0) {
      return new ArrayList<>();
    }

    List<String> ipSetList = new ArrayList<>(new HashSet<>(ips));
    List<DNSRecord> res = parallelLookup(ipSetList);

    Map<String, DNSRecord> mapping = IntStream.range(0, ipSetList.size())
        .boxed()
        .collect(Collectors.toMap(ipSetList::get, res::get));

    return ips.stream()
        .map(mapping::get)
        .collect(Collectors.toList());
  }

  public DNSRecord createRecordFromGoogleDNS(GoogleDNS res, String ip) {
    try {
      String data;
      DNSRecord rdns = new DNSRecord();
      if (res.Answer != null) {
        data = res.Answer[res.Answer.length - 1].data;
        rdns.setServer(true);
      } else if (res.Authority != null) {
        data = res.Authority[res.Authority.length - 1].data;
        rdns.setAuthority(true);
      } else {
        return new DNSRecord(ip, false, false);
      }

      Matcher m = dnsPattern.matcher(data);
      List<String> matches = new ArrayList<>();
      while (m.find()) {
        matches.add(m.group());
      }

      data = matches.get(matches.size() - 1);

      if (data.endsWith(".")) {
        data = data.substring(0, data.length() - 1);
      }

      rdns.setDomain(getFQDN(data));
      return rdns;
    } catch (Exception e) {
      return new DNSRecord(ip, false, false);
    }
  }

  public String dnsRequest(String ip) throws Exception {
    rateLimiter.acquire();
    List<String> reverseIP = Arrays.asList(ip.split("\\."));
    Collections.reverse(reverseIP);
    String reverseIPString = String.join(".", reverseIP);

    StringBuilder sb = new StringBuilder();
    URL url = new URL(String
        .format("https://dns.google.com/resolve?name=%s.in-addr.arpa&type=PTR", reverseIPString));
    if (reverseIPString.contains(":")) {
      url = new URL(
          String.format("https://dns.google.com/resolve?name=%s&type=PTR", reverseIPString));
    }
    URLConnection uc = url.openConnection();
    BufferedReader in = new BufferedReader(
        new InputStreamReader(
            uc.getInputStream()));
    String inputLine;

    while ((inputLine = in.readLine()) != null) {
      sb.append(inputLine);
    }
    in.close();
    return sb.toString();
  }

  private String getFQDN(String host) {
    String[] parts = host.split("\\.");
    return parts[parts.length - 2] + "." + parts[parts.length - 1];
  }
}
