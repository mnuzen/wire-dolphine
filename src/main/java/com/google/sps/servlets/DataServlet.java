
package com.google.sps.servlets;

import com.google.sps.data.PCAPdata;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<PCAPdata> dataTable = new ArrayList<PCAPdata>();
    
      
    Query query = new Query("data").addSort("Flagged", SortDirection.DESCENDING); //switch to time when database gets new datafield
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {

      String source = (String) entity.getProperty("Source");
      String destination = (String) entity.getProperty("Destination");
      String domain = (String) entity.getProperty("Domain");
      String location = (String) entity.getProperty("Location");
      String protocol = (String) entity.getProperty("Protocol");
      int size = (int) (long) entity.getProperty("Size"); //datastore stores ints as long by default 
      boolean flagged = (Boolean) entity.getProperty("Flagged");
      int frequency = (int) (long) entity.getProperty("Frequency");

      PCAPdata temp = new PCAPdata(source, destination, domain, 
      location,protocol, size, flagged, frequency);
       
      dataTable.add(temp);
    }


    String json = convertToJsonUsingGson(dataTable);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //gets values from table forms and puts them into datastore
        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String domain = request.getParameter("domain");
        String location = request.getParameter("location");
        String protocol = request.getParameter("protocol");
        int size = Integer.parseInt(request.getParameter("size")); 
        boolean flagged = Boolean.parseBoolean(request.getParameter("flagged"));
        int frequency = Integer.parseInt(request.getParameter("frequency"));

        Entity pcapEntity = new Entity("data"); //creates entitiy tab in datastore

        pcapEntity.setProperty("Source", source);
        pcapEntity.setProperty("Destination", destination);
        pcapEntity.setProperty("Domain", domain);
        pcapEntity.setProperty("Location", location);
        pcapEntity.setProperty("Size", size);
        pcapEntity.setProperty("Protocol", protocol);
        pcapEntity.setProperty("Flagged", flagged);
        pcapEntity.setProperty("Frequency", frequency);

        datastore.put(pcapEntity); //pushes new pcap entry to datastore

        response.sendRedirect("/tables.html");

  }

  private String convertToJsonUsingGson(ArrayList<PCAPdata> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
