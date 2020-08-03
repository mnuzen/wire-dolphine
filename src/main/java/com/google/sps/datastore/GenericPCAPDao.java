

package com.google.sps.datastore;

import java.util.ArrayList;
import com.google.sps.datastore.GenericPCAPDao;

public interface GenericPCAPDao {

    public ArrayList<PCAPdata> getPCAPObjects(String searchEntity);
    public void setPCAPObjects(PCAPdata data, String searchEntity);
    
}