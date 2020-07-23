    
// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
      Boolean flagged = (Boolean) entity.getProperty("Flagged");

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
        Boolean flagged = false; //flagged IP value handeled by api

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