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
import com.google.netpcapanalysis.models.Flagged;
import com.google.netpcapanalysis.models.MaliciousPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

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
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Transaction;


public class PCAPDaoImpl implements PCAPDao {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final String cacheEntity= "Malicious_IP_Cache";

  public PCAPDaoImpl() {

  }

 //gets all PCAP data under a given entity from datastore
  public ArrayList<PCAPdata> getPCAPObjects(String searchEntity) {
    ArrayList<PCAPdata> dataTable = new ArrayList<>();

    Query query = new Query(searchEntity).addSort("Source", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String source = (String) entity.getProperty("Source");
      String destination = (String) entity.getProperty("Destination");
      String protocol = (String) entity.getProperty("Protocol");
      int size = (int) (long) entity.getProperty("Size");
      boolean outbound = (Boolean) entity.getProperty("Outbound");

      PCAPdata temp = new PCAPdata(source, destination, protocol, size, outbound);

      dataTable.add(temp);
    }
    return dataTable;
  }

  //uploades a given dataobject list to datastore under the given name searchEntity
  public void setPCAPObjects(ArrayList<PCAPdata> data, String searchEntity) {
    Entity entity = new Entity(searchEntity);
    List<Entity> pcapEntityAll = new ArrayList<Entity>();

    for (PCAPdata packet : data) {
    Entity pcapEntity = new Entity(entity.getKey());

    pcapEntity.setProperty("Source", packet.source);
    pcapEntity.setProperty("Destination", packet.destination);
    pcapEntity.setProperty("Size", packet.size);
    pcapEntity.setProperty("Protocol", packet.protocol);
    pcapEntity.setProperty("Outbound", packet.outbound);

    pcapEntityAll.add(pcapEntity);
    }

    datastore.put(pcapEntityAll);

  }

  public String searchMaliciousDB(String searchIP) {
   
    Filter propertyFilter =
    new FilterPredicate("IP", FilterOperator.EQUAL, searchIP);
    Query q = new Query(cacheEntity).setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(q);
    List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(1));

    if(result.size() > 0){
      String value = (String) result.get(0).getProperty("Flagged");

      if(value.equalsIgnoreCase(Flagged.TRUE)){
        return Flagged.TRUE;
      }
      else
      {
        return Flagged.FALSE;
      }
    }
    else
    {
      return Flagged.UNKNOWN;
    }
  }

  public void setMaliciousIPObjects(MaliciousPacket data) {
    Entity Entity = new Entity(cacheEntity);
   
    Entity.setProperty("IP", data.ip);
    Entity.setProperty("Flagged", data.flagged);
    datastore.put(Entity);
  }

}
