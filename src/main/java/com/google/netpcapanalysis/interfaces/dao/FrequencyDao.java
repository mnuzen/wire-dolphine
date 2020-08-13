package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;

public interface FrequencyDao {
    public ArrayList<PCAPdata> getAllPCAP();
    public LinkedHashMap<String, Integer> getFinalMap();
    public String getMyIP();
}