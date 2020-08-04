

package com.google.netpcapanalysis.interfaces.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList;

public interface PCAPDao {

    public ArrayList<PCAPdata> getPCAPObjects(String searchEntity);
    public void setPCAPObjects(PCAPdata data, String searchEntity);
    
}