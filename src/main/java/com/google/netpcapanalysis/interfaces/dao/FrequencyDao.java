package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface FrequencyDao {
    public ArrayList<PCAPdata> getAllPCAP();
    public LinkedHashMap<String, Integer> getFinalMap();
    public String getMyIP();
}