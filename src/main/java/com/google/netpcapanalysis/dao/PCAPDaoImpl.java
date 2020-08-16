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
import com.google.netpcapanalysis.models.FileAttribute;
import com.google.netpcapanalysis.models.MaliciousPacket;

import java.util.Date;
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
  private final String fileEntity= "File_Attributes";
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

  public void setPCAPObjects(ArrayList<PCAPdata> data, String searchEntity) {
    Entity entity = new Entity(searchEntity);
    List<Entity> pcapEntityAll = new ArrayList<Entity>();

    for (PCAPdata packet : data) {
      Entity pcapEntity = new Entity(entity.getKey());

      pcapEntity.setProperty("Source", packet.source);
      pcapEntity.setProperty("Destination", packet.destination);
      pcapEntity.setProperty("Size", packet.size);
      pcapEntity.setProperty("Protocol", packet.protocol);

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

  private boolean searchFileAttribute(String searchEntity)
  {
    FileAttribute temp = new FileAttribute();;
    Filter propertyFilter = 
    new FilterPredicate("PCAP_Entity", FilterOperator.EQUAL, searchEntity);
    Query q = new Query(fileEntity).setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(q);

    List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(1));

    if(result.size() > 0){
      return true;
    }
    else{
      return false;
    }
  }

  private Entity getEntityAttribute(String searchEntity)
  {
    FileAttribute temp = new FileAttribute();;
    Filter propertyFilter = 
    new FilterPredicate("PCAP_Entity", FilterOperator.EQUAL, searchEntity);
    Query q = new Query(fileEntity).setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(q);

    List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(1));

    return result.get(0);
  }

  public FileAttribute getFileAttribute(String searchEntity)
  {
    FileAttribute temp = new FileAttribute();;
    Filter propertyFilter = 
    new FilterPredicate("PCAP_Entity", FilterOperator.EQUAL, searchEntity);
    Query q = new Query(fileEntity).setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(q);

    List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(1));

    if(result.size() > 0){
      String pcapEntity = (String) result.get(0).getProperty("PCAP_Entity");
      String fileName = (String) result.get(0).getProperty("File_Name");
      String myIP = (String) result.get(0).getProperty("My_IP");
      Date uploadDate = (Date) result.get(0).getProperty("Upload_Date");
       temp = new FileAttribute(pcapEntity, fileName, myIP, uploadDate);
    }
    
   return temp;
  }

  public ArrayList<FileAttribute> getFileAttributes(String searchEntity) {
    ArrayList<FileAttribute> fileList = new ArrayList<>();

    Query query = new Query(searchEntity);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String pcapEntity = (String) entity.getProperty("PCAP_Entity");
      String fileName = (String) entity.getProperty("File_Name");
      String myIP = (String) entity.getProperty("My_IP");
      Date uploadDate = (Date) entity.getProperty("Upload_Date");

      FileAttribute temp = new FileAttribute(pcapEntity, fileName, myIP, uploadDate);

      fileList.add(temp);
    }
    return fileList;
  }

  public void setFileAttribute(FileAttribute data){
    
    if(!searchFileAttribute(data.pcapEntity))
    {
      Entity newEntity = new Entity(fileEntity);
   
      newEntity.setProperty("PCAP_Entity", data.pcapEntity);
      newEntity.setProperty("File_Name", data.fileName);
      newEntity.setProperty("My_IP", data.myIP);
      newEntity.setProperty("Upload_Date", data.uploadDate);
      datastore.put(newEntity);
    }
    //if file already uploaded then update fields
    else{
      Entity prevEntity = getEntityAttribute(data.pcapEntity);

      prevEntity.setProperty("My_IP", data.myIP);
      prevEntity.setProperty("Upload_Date", data.uploadDate);
      datastore.put(prevEntity);
    }
  }

}
