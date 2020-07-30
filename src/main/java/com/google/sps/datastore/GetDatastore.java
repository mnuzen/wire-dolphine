package com.google.sps.datastore;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

//PCAP storage class
public class GetDatastore {

    public ArrayList<PCAPdata> dataTable = new ArrayList<PCAPdata>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  public GetDatastore() 
  {
      
  }


  //get all properties in the entity "data" 
  public void getAll(){
    Query query = new Query("data").addSort("Flagged", SortDirection.DESCENDING);
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

      PCAPdata temp = new PCAPdata(source, destination, domain, 
      location,protocol, size, flagged, frequency);
       
      dataTable.add(temp);
    }
  }


  //get all properties in a given entity (have each pcap files as seperate entity in datastore)
  public void getEntity(String searchEntity){
    
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

      PCAPdata temp = new PCAPdata(source, destination, domain, 
      location,protocol, size, flagged, frequency);
       
      dataTable.add(temp);
    }
  }



}