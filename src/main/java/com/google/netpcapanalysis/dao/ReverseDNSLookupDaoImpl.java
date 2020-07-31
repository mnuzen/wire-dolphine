package com.google.netpcapanalysis.dao;

import com.google.gson.Gson;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import com.google.netpcapanalysis.models.ReverseDNS;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReverseDNSLookupDaoImpl implements ReverseDNSLookupDao {
  private class GoogleDNS {
    private class GoogleDNSQuestion {
      public String name;
      public Integer type;
    }

    private class GoogleDNSAnswer {
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

  private static final String DNS_REGEX =
      "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)(?=.\\s[0-9\\s]+)?";
  private Pattern dnsPattern;

  public ReverseDNSLookupDaoImpl() {
    dnsPattern = Pattern.compile(DNS_REGEX);
  }

  public ReverseDNS lookup(String ip) {
    try {
      String request = dnsRequest(ip);
      GoogleDNS res = new Gson().fromJson(request, GoogleDNS.class);

      String data;
      ReverseDNS rdns = new ReverseDNS();
      if (res.Answer != null) {
        data = res.Answer[res.Answer.length - 1].data;
        rdns.server = true;
      } else if (res.Authority != null) {
        data = res.Authority[res.Authority.length - 1].data;
        rdns.authority = true;
      } else {
        throw new Error("invalid dns request");
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

      rdns.record = data;
      return rdns;
    } catch (Exception e) {
      return null;
    }
  }

  public String dnsRequest(String ip) throws Exception {
    List<String> reverseIP = Arrays.asList(ip.split("\\."));
    Collections.reverse(reverseIP);
    String reverseIPString = String.join(".", reverseIP);

    StringBuilder sb = new StringBuilder();
    URL yahoo = new URL(String.format("https://dns.google.com/resolve?name=%s.in-addr.arpa&type=PTR", reverseIPString));
    URLConnection yc = yahoo.openConnection();
    BufferedReader in = new BufferedReader(
        new InputStreamReader(
            yc.getInputStream()));
    String inputLine;

    while ((inputLine = in.readLine()) != null) {
      sb.append(inputLine);
    }
    in.close();
    return sb.toString();
  }
}
