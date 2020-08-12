package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList;

public interface MaliciousIPDao {

  public ArrayList<PCAPdata> run(ArrayList<PCAPdata> allData);
    
}