package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList; 
import java.io.IOException;

public interface PCAPParserDao {
    public void parseRaw() throws IOException;
    public void putDatastore();
    public ArrayList<PCAPdata> getAllPCAP();
}