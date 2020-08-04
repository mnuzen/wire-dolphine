package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.ReverseDNS;
import java.net.InetAddress;

public interface ReverseDNSLookupDao {

  /**
   * Checks to find the server/authoritative DNS server associated with a hostname
   * @param ip ipv4 address
   * @return ReverseDNS record signalling server, authority, and host. Returns null if cannot be
   * determined
   */
  public ReverseDNS lookup(String ip);
}
