package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;

public interface BucketDao {
    public LinkedHashMap<String, HashMap<String, Integer>> getBuckets();
    public String getMyIP();
    public ArrayList<PCAPdata> getSortedPCAP();
}