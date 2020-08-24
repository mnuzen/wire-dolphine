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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReverseDNSLookupDaoImpl implements ReverseDNSLookupDao {
  private static class GoogleDNS {
    private static class GoogleDNSQuestion {
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

  private static final RateLimiter rateLimiter = RateLimiter.create(500.0);
  private static boolean dnsSwitch;

  private static final String DNS_REGEX =
      "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)(?=.\\s[0-9\\s]+)?";
  private Pattern dnsPattern;
  private Cache<String, DNSRecord> cache;

  public ReverseDNSLookupDaoImpl() {
    dnsPattern = Pattern.compile(DNS_REGEX);
    cache =
        new CacheBuilder<String, DNSRecord>()
            .setCacheName("reversedns")
            .setCacheType(CacheType.MEMORY)
            .setKVClass(String.class, DNSRecord.class)
            .setPolicy(Policy.MAXIMUM_SIZE)
            .setPolicyArgument(10000)
            .build();
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

  private DNSRecord createRecordFromGoogleDNS(GoogleDNS res, String ip) {
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

    rdns.setDomain(data);
    return rdns;
  }

  public String dnsRequest(String ip) throws Exception {
    rateLimiter.acquire(1);
    StringBuilder sb = new StringBuilder();

    URL url;
    if (dnsSwitch) {
      url = getGoogleURL(ip);
    } else {
      url = getCloudflareURL(ip);
    }
    URLConnection uc = url.openConnection();
    uc.setRequestProperty("Accept", "application/dns-json");
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

  private URL getGoogleURL(String ip) throws MalformedURLException {
    dnsSwitch = !dnsSwitch;
    List<String> reverseIP = Arrays.asList(ip.split("\\."));
    Collections.reverse(reverseIP);
    String reverseIPString = String.join(".", reverseIP);
    URL url = new URL(String.format("https://dns.google.com/resolve?name=%s.in-addr.arpa&type=PTR", reverseIPString));
    if(ip.contains(":")) {
      url = new URL(String.format("https://dns.google.com/resolve?name=%s&type=PTR", reverseIPString));
    }
    return url;
  }

  private URL getCloudflareURL(String ip) throws MalformedURLException {
    dnsSwitch = !dnsSwitch;
    List<String> reverseIP = Arrays.asList(ip.split("\\."));
    Collections.reverse(reverseIP);
    String reverseIPString = String.join(".", reverseIP);
    URL url = new URL(String.format("https://cloudflare-dns.com/dns-query?name=%s.in-addr.arpa&type=PTR", reverseIPString));
    if(ip.contains(":")) {
      url = new URL(String.format("https://cloudflare-dns.com/dns-query?name=%s&type=PTR", reverseIPString));
    }
    return url;
  }
}
