/*
* Acts as a module between program and datastore.
* us the getPCAPObjects or setPCAPObjects methods 
* to get/set PCAP data each take a String searchEntity, 
* which is used to select which entity in datastore to 
* get/set data.
*/

package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.models.MaliciousPacket;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class PCAPDaoImpl implements PCAPDao {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final String cacheEntity= "Malicious_IP_Cache";

  

  public PCAPDaoImpl() {

  }

  public ArrayList<PCAPdata> getPCAPObjects(String searchEntity) {
    ArrayList<PCAPdata> dataTable = new ArrayList<>();

    Query query = new Query(searchEntity).addSort("Flagged", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String source = (String) entity.getProperty("Source");
      String destination = (String) entity.getProperty("Destination");
      String domain = (String) entity.getProperty("Domain");
      String location = (String) entity.getProperty("Location");
      String protocol = (String) entity.getProperty("Protocol");
      int size = (int) (long) entity.getProperty("Size");
      String flagged = (String) entity.getProperty("Flagged");
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

  public Boolean searchMaliciousDB(String seachIP) {
   
    Filter propertyFilter =
    new FilterPredicate("IP", FilterOperator.EQUAL, seachIP);
    Query q = new Query(cacheEntity).setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(q);
    List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(1));

    if(result.size() == 0){
      return false;
    }
    else{
      return true;
    }
  }

  public void setMaliciousIPObjects(MaliciousPacket data) {
    Entity Entity = new Entity(cacheEntity);
   
    Entity.setProperty("IP", data.ip);
    Entity.setProperty("Flagged", data.flagged);
    System.out.println("in set object");
    datastore.put(Entity);
  }

}
