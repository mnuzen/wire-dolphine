package com.google.netpcapanalysis.servlets;

import java.util.ArrayList;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class TableServlet extends HttpServlet {

  private static final String FILE_NAME = "file_1";
  private PCAPDao datastore = new PCAPDaoImpl();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String json = convertToJsonUsingGson(datastore.getPCAPObjects(FILE_NAME));
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // gets values from table forms and puts them into datastore
    String source = request.getParameter("source");
    String destination = request.getParameter("destination");
    String domain = request.getParameter("domain");
    String location = request.getParameter("location");
    String protocol = request.getParameter("protocol");
    int size = Integer.parseInt(request.getParameter("size"));
    String flagged = request.getParameter("flagged");
    int frequency = Integer.parseInt(request.getParameter("frequency"));

    PCAPdata tempPCAP = new PCAPdata(source, destination, domain, location, protocol,
         size, flagged, frequency);

         datastore.setPCAPObjects(tempPCAP, FILE_NAME);

    response.sendRedirect("/tables.html");
  }

  private String convertToJsonUsingGson(ArrayList<PCAPdata> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
