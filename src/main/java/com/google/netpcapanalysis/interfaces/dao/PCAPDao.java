package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.interfaces.models.PCAP;

public interface PCAPDao {

  /**
   * Gets a raw PCAP file
   * @param id
   * @return PCAP
   */
  public PCAP getPCAP(String id);
}
