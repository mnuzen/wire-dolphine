package com.google.netpcapanalysis.servlets;

import com.google.gson.Gson;
import com.google.netpcapanalysis.dao.GeolocationDaoImpl;
import com.google.netpcapanalysis.dao.KeystoreDaoImpl;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.GeolocationDao;
import com.google.netpcapanalysis.interfaces.dao.KeystoreDao;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.utils.NetUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/keystore")
public class KeystoreServlet extends HttpServlet {

  private KeystoreDao keystoreDao;

  public void init(ServletConfig conf) {
    this.keystoreDao = new KeystoreDaoImpl();
  }

  /**
   * Requires query string with requested key
   *
   * @param request
   * @param response response is a string
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String id = NetUtils.getParameter(request, "key", "");
    String key = "";

    if (id.equals("mapsAPIKey")) {
      key = keystoreDao.getKeystore().getMapsAPIKey();
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    response.setContentType("text/html;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(key);
  }
}
