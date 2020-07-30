package com.google.sps.servlets;

import com.google.sps.datastore.PCAPdata;
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

                PCAPdata tempPCAP = new PCAPdata(pcapLine[0],pcapLine[1],pcapLine[2],pcapLine[3],pcapLine[4],
                Integer.parseInt(pcapLine[5]), Boolean.parseBoolean(pcapLine[6]), Integer.parseInt(pcapLine[7]));
                SetDatastore upload = new SetDatastore(tempPCAP);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        response.sendRedirect("/tables.html");
  }
}
