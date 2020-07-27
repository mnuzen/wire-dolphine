package com.google.sps.dao;

import com.google.sps.Mock;
import com.google.sps.models.PCAPAnalysis;

public class AnalysisDao {

  public static PCAPAnalysis getAnalysis(String id) {
    return Mock.PCAP_ANALYSIS;
  }

}
