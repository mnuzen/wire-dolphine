package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.models.PCAP;
import com.google.netpcapanalysis.models.MockPCAP;

public class PCAPDaoImpl implements PCAPDao {

  @Override
  public PCAP getPCAP(String id) {
    return new MockPCAP();
  }

}
