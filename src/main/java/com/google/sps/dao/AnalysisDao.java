package com.google.sps.dao;

import com.google.sps.Mock;
import com.google.sps.models.PCAPAnalysis;
import java.io.File;

public class AnalysisDao {

  private static File database;

  public static PCAPAnalysis getAnalysis(String id) {
    return Mock.PCAP_ANALYSIS;
  }

}
