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

      PCAPdata temp = new PCAPdata(source, destination, protocol, size);

      dataTable.add(temp);
    }
    return dataTable;
  }

  //uploades a given databack to datastore under the given name 
  public void setPCAPObjects(PCAPdata data, String searchEntity) {
    Entity pcapEntity = new Entity(searchEntity);

    pcapEntity.setProperty("Source", data.source);
    pcapEntity.setProperty("Destination", data.destination);
    pcapEntity.setProperty("Size", data.size);
    pcapEntity.setProperty("Protocol", data.protocol);

    datastore.put(pcapEntity);
  }

 //Gets most use IP in PCAPdata
 private String findMyIP(ArrayList<PCAPdata> allData) {
  String myip = "";
  HashMap<String, Integer> hm = new HashMap<String, Integer>();
  for (PCAPdata packet : allData) {
    // source
    if (hm.containsKey(packet.source)) { 
      // if IP already exists, increment
      hm.merge(packet.source, 1, Integer::sum);
    }
    else {
      hm.put(packet.source, 1);
    }
    // destination
    if (hm.containsKey(packet.destination)) { 
      // if IP already exists, increment
      hm.merge(packet.destination, 1, Integer::sum);
    }
    else {
      hm.put(packet.destination, 1);
    }
  }
  // find largest recurrence
  myip = Collections.max(hm.entrySet(), Map.Entry.comparingByValue()).getKey();
  return myip;
}

//Finds all unique IPs and sets myip to source
public ArrayList<PCAPdata> getUniqueIPs(ArrayList<PCAPdata> allData){
  HashMap<String, PCAPdata> finalMap = new HashMap<String, PCAPdata>();
  String myip = findMyIP(allData);;
  String outip = "";

  for (PCAPdata packet : allData) {

    //swaps packet order based on myip
    if (packet.source.equals(myip)) {
      outip = packet.destination;
    }
    else {
      outip = packet.source;
    }
    
    //puts data into map if not already there
    if (!finalMap.containsKey(outip)){
      PCAPdata tempPCAP = new PCAPdata(myip, outip, "", "", packet.protocol, packet.size, packet.flagged, packet.frequency); 
      finalMap.put(outip, tempPCAP);
    }
  }
  return (new ArrayList<PCAPdata>(finalMap.values()));
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
