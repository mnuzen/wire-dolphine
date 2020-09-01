package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.List;

public interface MaliciousIPDao {

  public List<PCAPdata> run(List<PCAPdata> allData, String myIP);
  public PCAPdata isMalicious(PCAPdata data);
    
}