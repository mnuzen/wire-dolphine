package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList;

public interface UtilityPCAPDao {
    public ArrayList<PCAPdata> getUniqueIPs(ArrayList<PCAPdata> allData);
    public String findMyIP(ArrayList<PCAPdata> allData);
    public String hashText(String text);

}