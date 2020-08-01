package com.google.sps.servlets;

import com.google.sps.datastore.PCAPdata;
import com.google.sps.datastore.GenericPCAPDao;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/load_mock_data")
public class LoadMockData extends HttpServlet {
  public static final String FILE_NAME = "file_1";
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // CSV format:
    // Source,Destination,Domain,Location,Protocal,Size,Flagged,Frequency
    String csvFile = "data.csv"; // CSV located in project dir /webapp
    String line = "";
    String cvsSplitBy = ",";

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] pcapLine = line.split(cvsSplitBy);

        PCAPdata tempPCAP = new PCAPdata(pcapLine[0], pcapLine[1], pcapLine[2], pcapLine[3],
             pcapLine[4], Integer.parseInt(pcapLine[5]), Boolean.parseBoolean(pcapLine[6]), 
             Integer.parseInt(pcapLine[7]));

        GenericPCAPDao data = new GenericPCAPDao();
        data.setPCAPObjects(tempPCAP, FILE_NAME);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    response.sendRedirect("/tables.html");
  }
}
