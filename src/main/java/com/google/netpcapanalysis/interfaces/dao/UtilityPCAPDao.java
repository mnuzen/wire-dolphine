package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.models.FileAttribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.util.ArrayList;

public interface UtilityPCAPDao {

    public String convertPCAPdataToJson(ArrayList<PCAPdata> data);
    public String convertFileToJson(ArrayList<FileAttribute> data);
    public ArrayList<PCAPdata> getUniqueIPs(ArrayList<PCAPdata> allData);
    public String findMyIP(ArrayList<PCAPdata> allData);
    public String hashText(String text);

}