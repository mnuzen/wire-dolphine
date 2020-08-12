package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.models.MaliciousPacket;
import java.util.ArrayList;

public interface PCAPDao {

    public ArrayList<PCAPdata> getPCAPObjects(String searchEntity);
    public void setPCAPObjects(PCAPdata data, String searchEntity);
    public void updateFlagged(String searchEntity, PCAPdata oldData, String flagged);
    public void updateDomain(String searchEntity, PCAPdata oldData, String domain);
    public void updateLocation(String searchEntity, PCAPdata oldData, String location);
    public String searchMaliciousDB(String seachIP);
    public void setMaliciousIPObjects(MaliciousPacket data);
}