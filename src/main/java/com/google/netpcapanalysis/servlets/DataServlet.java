package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.netpcapanalysis.utils.SessionManager;
import com.google.netpcapanalysis.utils.NetUtils;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private PCAPDao datastore = new PCAPDaoImpl();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String entityName = SessionManager.getSessionEntity(request);

    String json = NetUtils.convertPCAPdataToJson(datastore.getPCAPObjects(entityName));
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
