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
import com.google.netpcapanalysis.dao.UtilityPCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.UtilityPCAPDao;
import com.google.netpcapanalysis.utils.SessionManager;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private PCAPDao datastore = new PCAPDaoImpl();
  private UtilityPCAPDao pcapUtility = new UtilityPCAPDaoImpl();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String entityName = SessionManager.getSessionEntity(request);

    String json = pcapUtility.convertPCAPdataToJson(datastore.getPCAPObjects(entityName));
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
