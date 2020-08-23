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

import java.util.Date;
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
  private final String fileEntity= "File_Attributes";

  public PCAPDaoImpl() {

  }

 //gets all PCAP data under a given entity from datastore
  public ArrayList<PCAPdata> getPCAPObjects(String searchEntity) {
    ArrayList<PCAPdata> dataTable = new ArrayList<>();

    try{
      Query query = new Query(searchEntity).addSort("Source", SortDirection.DESCENDING);
      PreparedQuery results = datastore.prepare(query);
      FetchOptions options = FetchOptions.Builder.withChunkSize(500);

      for (Entity entity : results.asIterable(options)) {
        String source = (String) entity.getProperty("Source");
        String destination = (String) entity.getProperty("Destination");
        String protocol = (String) entity.getProperty("Protocol");
        int size = (int) (long) entity.getProperty("Size");

        PCAPdata temp = new PCAPdata(source, destination, protocol, size);

        dataTable.add(temp);
      }
    }catch(Exception e)
    {
      
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

  private Entity getEntityAttribute(String searchEntity)
  {
    FileAttribute temp = new FileAttribute();;
    Filter propertyFilter = 
    new FilterPredicate("PCAP_Entity", FilterOperator.EQUAL, searchEntity);
    Query q = new Query(fileEntity).setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(q);

    List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(1));
    if(result.size() > 0){
      return result.get(0);
    }

    return null;
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
      String description = (String) result.get(0).getProperty("Description");
      Date uploadDate = (Date) result.get(0).getProperty("Upload_Date");
       temp = new FileAttribute(pcapEntity, fileName, myIP, description, uploadDate);
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
      String description = (String) entity.getProperty("Description");
      Date uploadDate = (Date) entity.getProperty("Upload_Date");

      FileAttribute temp = new FileAttribute(pcapEntity, fileName, myIP, description, uploadDate);

      fileList.add(temp);
    }
    return fileList;
  }

  public void setFileAttribute(FileAttribute data){
    
    Entity prevEntity = getEntityAttribute(data.pcapEntity);

    if(prevEntity == null) //.equals(null)? |.equals(new Enity())  | .toString() is null
    {
      Entity newEntity = new Entity(fileEntity);
   
      newEntity.setProperty("PCAP_Entity", data.pcapEntity);
      newEntity.setProperty("File_Name", data.fileName);
      newEntity.setProperty("My_IP", data.myIP);
      newEntity.setProperty("Description", data.description);
      newEntity.setProperty("Upload_Date", data.uploadDate);
      datastore.put(newEntity);
    }
    //if file already uploaded then update fields
    else{
      prevEntity.setProperty("Description", data.description);
      prevEntity.setProperty("My_IP", data.myIP);
      prevEntity.setProperty("Upload_Date", data.uploadDate);
      datastore.put(prevEntity);
    }
  }

}
