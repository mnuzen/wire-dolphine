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

import java.util.Random; 


@WebServlet("/load_data")
public class LoadMockData extends HttpServlet {
    ArrayList<PCAPdata> mockData = new ArrayList<PCAPdata>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Random rand = new Random(); 
        int rand_int = rand.nextInt(4); 
        String[] source = {"192.168.0.2", "192.168.0.43", "192.168.0.32", "192.168.0.20"};
        String[] destination = {"103.31.132.193", "174.23.43.120", "99.32.123.32", "42.123.232.74"};
        String[] domain = {"reddit.com", "google.com", "amazon.com", "apple.com"};
        String[] location = {"United States", "Canada", "China", "France"};
        String[] size = {"12", "43", "37", "29"};
        String[] protocal = {"HTTP", "HTTPS", "UDP", "TCP"};
        String[] flagged = {"true", "false", "false", "false"};

        for(int i=0; i<25;i++)
        {
            Entity commentEntity = new Entity("data"); //creates entitiy that stores properties similar to a data structure

            rand_int = rand.nextInt(4);
            commentEntity.setProperty("Source", source[rand_int]);
            rand_int = rand.nextInt(4);
            commentEntity.setProperty("Destination", destination[rand_int]);
            rand_int = rand.nextInt(4);
            commentEntity.setProperty("Domain", domain[rand_int]);
            rand_int = rand.nextInt(4);
            commentEntity.setProperty("Location", location[rand_int]);
            rand_int = rand.nextInt(4);
            commentEntity.setProperty("Size", Integer.toString(i)); //size[rand_int]
            rand_int = rand.nextInt(4);
            commentEntity.setProperty("Protocal", protocal[rand_int]);
            rand_int = rand.nextInt(4);
            commentEntity.setProperty("Flagged", flagged[rand_int]);

            datastore.put(commentEntity); //pushes new comment to datastore
        }

        response.sendRedirect("/tables.html");

  }
}
