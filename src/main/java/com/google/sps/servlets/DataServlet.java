
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
    
      
    Query query = new Query("data").addSort("Time", SortDirection.DESCENDING); //switch to time when database gets new datafield
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {

      String source = (String) entity.getProperty("Source");
      String destination = (String) entity.getProperty("Destination");
      String domain = (String) entity.getProperty("Domain");
      String location = (String) entity.getProperty("Location");
      String size = (String) entity.getProperty("Size");
      String protocol = (String) entity.getProperty("Protocol");
      String time = (String) entity.getProperty("Time");
      String flagged = (String) entity.getProperty("Flagged");

      PCAPdata temp = new PCAPdata(source, destination, domain, 
      location, size, protocol, time, flagged);
       
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
        String size = request.getParameter("size");
        String protocol = request.getParameter("protocol");
        String time = request.getParameter("time");
        String flagged = request.getParameter("flagged");

        Entity pcapEntity = new Entity("data"); //creates entitiy tab in datastore

        pcapEntity.setProperty("Source", source);
        pcapEntity.setProperty("Destination", destination);
        pcapEntity.setProperty("Domain", domain);
        pcapEntity.setProperty("Location", location);
        pcapEntity.setProperty("Size", size);
        pcapEntity.setProperty("Protocol", protocol);
        pcapEntity.setProperty("Time", time);
        pcapEntity.setProperty("Flagged", flagged);

        datastore.put(pcapEntity); //pushes new pcap entry to datastore

        response.sendRedirect("/tables.html");

  }

  private String convertToJsonUsingGson(ArrayList<PCAPdata> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
