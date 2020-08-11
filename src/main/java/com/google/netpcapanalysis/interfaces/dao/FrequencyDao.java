package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;

public interface FrequencyDao {
    public void loadFrequency();
    public ArrayList<PCAPdata> getAllPCAP();
    public HashMap<String, PCAPdata> getFinalMap();
    public ArrayList<PCAPdata> getFinalFreq();
    public String getMyIP();
}