/*
* Acts as a module between program and datastore.
* us the getPCAPObjects or setPCAPObjects methods 
* to get/set PCAP data each take a String searchEntity, 
* which is used to select which entity in datastore to 
* get/set data.
*/

package com.google.sps.datastore;

import java.util.ArrayList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.datastore.GenericPCAPDao;


public class GenericPCAPDaoImpl implements GenericPCAPDao {
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  public GenericPCAPDaoImpl() {

  }

  public ArrayList<PCAPdata> getPCAPObjects(String searchEntity) {
    ArrayList<PCAPdata> dataTable = new ArrayList<PCAPdata>();

    Query query = new Query(searchEntity).addSort("Flagged", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {

      String source = (String) entity.getProperty("Source");
      String destination = (String) entity.getProperty("Destination");
      String domain = (String) entity.getProperty("Domain");
      String location = (String) entity.getProperty("Location");
      String protocol = (String) entity.getProperty("Protocol");
      int size = (int) (long) entity.getProperty("Size");
      boolean flagged = (Boolean) entity.getProperty("Flagged");
      int frequency = (int) (long) entity.getProperty("Frequency");

      PCAPdata temp = new PCAPdata(source, destination, domain, location, 
          protocol, size, flagged, frequency);

      dataTable.add(temp);
    }
    return dataTable;
  }

  public void setPCAPObjects(PCAPdata data, String searchEntity) {
    Entity pcapEntity = new Entity(searchEntity);

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
