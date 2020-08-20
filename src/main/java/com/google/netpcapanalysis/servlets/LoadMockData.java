package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.mockdata.MockDataLoader;

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

    String file = request.getParameter("file");
    String description = request.getParameter("description");
    mockData.CSVDataUpload(file, description);

    response.sendRedirect("/files.html");
  }
}
