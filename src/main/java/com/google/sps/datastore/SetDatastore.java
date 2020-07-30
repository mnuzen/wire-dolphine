
package com.google.sps.datastore;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

//PCAP storage class
public class SetDatastore {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 

  public SetDatastore(PCAPdata data) 
  {
        Entity pcapEntity = new Entity("data"); 

        pcapEntity.setProperty("Source", data.source);
        pcapEntity.setProperty("Destination", data.destination);
        pcapEntity.setProperty("Domain", data.domain);
        pcapEntity.setProperty("Location", data.location);
        pcapEntity.setProperty("Size", data.size);
        pcapEntity.setProperty("Protocol", data.protocol);
        pcapEntity.setProperty("Flagged", data.flagged);
        pcapEntity.setProperty("Frequency", data.frequency);

        datastore.put(pcapEntity);
  }

  public SetDatastore(PCAPdata data, String storeEntity) 
  {
        Entity pcapEntity = new Entity(storeEntity); 

        pcapEntity.setProperty("Source", data.source);
        pcapEntity.setProperty("Destination", data.destination);
        pcapEntity.setProperty("Domain", data.domain);
        pcapEntity.setProperty("Location", data.location);
        pcapEntity.setProperty("Size", data.size);
        pcapEntity.setProperty("Protocol", data.protocol);
        pcapEntity.setProperty("Flagged", data.flagged);
        pcapEntity.setProperty("Frequency", data.frequency);

        datastore.put(pcapEntity);
  }

}
