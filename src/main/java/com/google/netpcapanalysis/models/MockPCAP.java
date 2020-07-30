package com.google.netpcapanalysis.models;

import com.google.netpcapanalysis.interfaces.models.PCAP;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockPCAP implements PCAP {

  private String id;
  private List<InetAddress> ips;

  public MockPCAP() {
    this.id = "sampleId";
    this.ips = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      ips.add(genIP());
    }
  }

  public String getId() {
    return id;
  }

  @Override
  public List<InetAddress> getIPs() {
    return ips;
  }

  public static InetAddress genIP() {
    InetAddress ip = null;
    Random r = new Random();
    while (ip == null || ip.isLoopbackAddress() ||
        ip.isAnyLocalAddress() ||
        ip.isLinkLocalAddress() ||
        ip.isSiteLocalAddress()) {
      try {
        ip = InetAddress.getByName(
            r.nextInt(256) + "." +
                r.nextInt(256) + "." +
                r.nextInt(256) + "." +
                r.nextInt(256)
        );
      } catch (UnknownHostException ignored) {
      }
    }
    return ip;
  }
}
