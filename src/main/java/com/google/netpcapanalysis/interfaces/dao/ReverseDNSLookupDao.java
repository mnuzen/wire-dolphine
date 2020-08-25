package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.DNSRecord;
import com.google.netpcapanalysis.models.PCAPdata;
import java.util.List;

public interface ReverseDNSLookupDao {

  /**
   * Checks to find the server/authoritative DNS server associated with a hostname
   * @param ip ipv4 address
   * @return ReverseDNS record signalling server, authority, and host. Returns null if cannot be
   * determined
   */
  public DNSRecord lookup(String ip);
  public List<DNSRecord> lookup(List<String> ip);
  public List<PCAPdata> dnsLookup(List<PCAPdata> data);
}
