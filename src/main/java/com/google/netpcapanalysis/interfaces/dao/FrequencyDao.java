package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.io.InputStream;
import java.io.IOException;

public interface FrequencyDao {
    public ArrayList<PCAPdata> getAllPCAP();
    public LinkedHashMap<String, Integer> getFinalMap();
    public String getMyIP();
}