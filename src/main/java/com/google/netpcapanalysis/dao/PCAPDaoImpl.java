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

  //Updates all PCAP entities that match Source and Destination IP new String input
  public void updateFlagged(String searchEntity, PCAPdata oldData, String flagged) {
    
    ArrayList<PCAPdata> dataTable = new ArrayList<>();

    Filter propertyFilter = CompositeFilterOperator.and(
        FilterOperator.EQUAL.of("Source", oldData.source),
        FilterOperator.EQUAL.of("Destination", oldData.destination));

    Query query = new Query(searchEntity).setFilter(propertyFilter);
    PreparedQuery results = datastore.prepare(query);

    for (Entity updatedEntity : results.asIterable()) {
    
      updatedEntity.setProperty("Flagged", flagged);
  
      datastore.put(updatedEntity);
    }
  }

  public void updateDomain(String searchEntity, PCAPdata oldData, String domain) {
    
    ArrayList<PCAPdata> dataTable = new ArrayList<>();

    Filter propertyFilter = CompositeFilterOperator.and(
        FilterOperator.EQUAL.of("Source", oldData.source),
        FilterOperator.EQUAL.of("Destination", oldData.destination));

    Query query = new Query(searchEntity).setFilter(propertyFilter);
    PreparedQuery results = datastore.prepare(query);

    for (Entity updatedEntity : results.asIterable()) {
    
      updatedEntity.setProperty("Domain", domain);
  
      datastore.put(updatedEntity);
    }
  }

  public void updateLocation(String searchEntity, PCAPdata oldData, String location) {
    
    ArrayList<PCAPdata> dataTable = new ArrayList<>();

    Filter propertyFilter = CompositeFilterOperator.and(
        FilterOperator.EQUAL.of("Source", oldData.source),
        FilterOperator.EQUAL.of("Destination", oldData.destination));

    Query query = new Query(searchEntity).setFilter(propertyFilter);
    PreparedQuery results = datastore.prepare(query);

    for (Entity updatedEntity : results.asIterable()) {
    
      updatedEntity.setProperty("Location", location);
  
      datastore.put(updatedEntity);
    }
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
