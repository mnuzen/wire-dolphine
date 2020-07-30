
package com.google.sps.servlets;

import com.google.sps.datastore.PCAPdata;
import com.google.sps.datastore.GetDatastore;
import com.google.sps.datastore.SetDatastore;

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
public class TableServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    GetDatastore data = new GetDatastore();
    data.getAll();

    String json = convertToJsonUsingGson(data.dataTable);
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

        PCAPdata tempPCAP = new PCAPdata( source, destination, domain, location, protocol, size, flagged, frequency);
        SetDatastore upload = new SetDatastore(tempPCAP);

        response.sendRedirect("/tables.html");

  }

  private String convertToJsonUsingGson(ArrayList<PCAPdata> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
