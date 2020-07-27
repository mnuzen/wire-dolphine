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


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import java.util.Scanner;

import java.io.*; 

@WebServlet("/load_data")
public class LoadMockData extends HttpServlet {
    ArrayList<PCAPdata> mockData = new ArrayList<PCAPdata>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


      //CSV format: Source,Destination,Domain,Location,Protocal,Size,Flagged,Frequency
        String csvFile = "data.csv"; //CSV located in project dir /webapp
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] pcapLine = line.split(cvsSplitBy);

                Entity pcapEntity = new Entity("data");
                pcapEntity.setProperty("Source", pcapLine[0]);
                pcapEntity.setProperty("Destination", pcapLine[1]);
                pcapEntity.setProperty("Domain", pcapLine[2]);
                pcapEntity.setProperty("Location", pcapLine[3]);
                pcapEntity.setProperty("Protocol", pcapLine[4]);
                pcapEntity.setProperty("Size", Integer.parseInt(pcapLine[5]));
                pcapEntity.setProperty("Flagged", Boolean.parseBoolean(pcapLine[6]));
                pcapEntity.setProperty("Frequency", Integer.parseInt(pcapLine[7]));
                datastore.put(pcapEntity); //pushes new entry to datastore
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        response.sendRedirect("/tables.html");
  }
}
