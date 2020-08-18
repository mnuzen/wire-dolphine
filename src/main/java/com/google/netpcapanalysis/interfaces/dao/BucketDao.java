package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface BucketDao {
    public String getMyIP();
    public ArrayList<PCAPdata> getSortedPCAP();
    public LinkedHashMap<String, HashMap<String, Integer>> getFinalBuckets();
    public LinkedHashMap<String, Integer> getFinalMap();
    public String longestCommonPrefix(String[] strs);
}