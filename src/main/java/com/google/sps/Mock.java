package com.google.sps;

import com.google.sps.models.PCAPAnalysis;

public class Mock {

  public static final String PCAP_ID = "8395f9e1-cc56-45d3-b801-aaa9a2ac8848";

  public static final String[] IPs = new String[] {
      "127.0.0.1",
      "1.1.1.1",
      "2.2.2.2",
      "3.3.3.3"
  };

  public static final String[] COUNTRIES = new String[] {
      "China",
      "Russia",
      "Brazil",
      "India"
  };

  public static final PCAPAnalysis PCAP_ANALYSIS = new PCAPAnalysis(
      PCAP_ID,
      IPs
  );

}
