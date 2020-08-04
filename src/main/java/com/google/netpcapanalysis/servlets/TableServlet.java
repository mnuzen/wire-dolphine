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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/data")
public class TableServlet extends HttpServlet {
  UserService userService = UserServiceFactory.getUserService();
  private PCAPDao data = new PCAPDaoImpl();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (userService.isUserLoggedIn()) {
    String json = convertToJsonUsingGson(data.getPCAPObjects(userService.getCurrentUser().getEmail()));
    response.setContentType("application/json;");
    response.getWriter().println(json);

    } else {
      response.sendRedirect("/login");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (userService.isUserLoggedIn()) {
      // gets values from table forms and puts them into datastore
      String source = request.getParameter("source");
      String destination = request.getParameter("destination");
      String domain = request.getParameter("domain");
      String location = request.getParameter("location");
      String protocol = request.getParameter("protocol");
      int size = Integer.parseInt(request.getParameter("size"));
      boolean flagged = Boolean.parseBoolean(request.getParameter("flagged"));
      int frequency = Integer.parseInt(request.getParameter("frequency"));

      PCAPdata tempPCAP = new PCAPdata(source, destination, domain, location, protocol, size, flagged, frequency);
      data.setPCAPObjects(tempPCAP, userService.getCurrentUser().getEmail());

      response.sendRedirect("/tables.html");

    } else {
      response.sendRedirect("/login");
    }
  }

  private String convertToJsonUsingGson(ArrayList<PCAPdata> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
