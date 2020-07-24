
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
    ArrayList<PCAPdata> data = new ArrayList<PCAPdata>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
      
    Query query = new Query("data").addSort("Flagged", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {

      String source = (String) entity.getProperty("Source");
      String destination = (String) entity.getProperty("Destination");
      String domain = (String) entity.getProperty("Domain");
      String location = (String) entity.getProperty("Location");
      String size = (String) entity.getProperty("Size");
      String protocal = (String) entity.getProperty("Protocal");
      String flagged = (String) entity.getProperty("Flagged");

      PCAPdata temp = new PCAPdata(source, destination, domain, 
      location, size, protocal, flagged);
       
      data.add(temp);
    }


    String json = convertToJsonUsingGson(data);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String domain = request.getParameter("domain");
        String location = request.getParameter("location");
        String size = request.getParameter("size");
        String protocal = request.getParameter("protocal");
        String flagged = request.getParameter("flagged");

        Entity commentEntity = new Entity("data"); //creates entitiy that stores properties similar to a data structure

        commentEntity.setProperty("Source", source);
        commentEntity.setProperty("Destination", destination);
        commentEntity.setProperty("Domain", domain);
        commentEntity.setProperty("Location", location);
        commentEntity.setProperty("Size", size);
        commentEntity.setProperty("Protocal", protocal);
        commentEntity.setProperty("Flagged", flagged);

        datastore.put(commentEntity); //pushes new comment to datastore

        response.sendRedirect("/tables.html");

  }

  private String convertToJsonUsingGson(ArrayList<PCAPdata> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
