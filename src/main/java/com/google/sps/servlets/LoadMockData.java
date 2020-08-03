package com.google.sps.servlets;

import com.google.sps.mockdata.MockDataLoader;

import com.google.gson.Gson;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/load_mock_data")
public class LoadMockData extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    MockDataLoader mockData = new MockDataLoader();
    mockData.CSVDataLoader();


    response.sendRedirect("/tables.html");
  }
}
