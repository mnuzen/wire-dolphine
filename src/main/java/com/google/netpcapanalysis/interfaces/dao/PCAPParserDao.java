package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;

public interface PCAPParserDao {
    public void parseRaw() throws IOException;
    public void putDatastore();
    public ArrayList<PCAPdata> getAllPCAP();
}