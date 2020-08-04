package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;

public interface PCAPParserDao {
    public void parseRaw() throws IOException;
    public void processData();
    public void putDatastore();
}