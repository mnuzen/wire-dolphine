package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList;

public interface MaliciousIPDao {

  public PCAPdata isMalicious(PCAPdata data);
    
}