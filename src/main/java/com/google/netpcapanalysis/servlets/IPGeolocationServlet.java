package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.interfaces.dao.GeolocationDao;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.models.PCAP;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.dao.GeolocationDaoImpl;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ipgeolocation")
public class IPGeolocationServlet extends HttpServlet {

  private GeolocationDao geolocationDao;
  private PCAPDao pcapDao;

  public void init(ServletConfig conf) {
    this.geolocationDao = new GeolocationDaoImpl();
    this.pcapDao = new PCAPDaoImpl();
  }

  /**
   * Requires query string with param `PCAPId`
   * @param request
   * @param response response is a {[country: string]: integer} JSON relating country to packet #
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String id = getParameter(request, "PCAPId", "");
    PCAP analysis = this.pcapDao.getPCAP(id);

    if (analysis == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Map<String, Integer> countryCount = new HashMap<>();

    for (InetAddress ip : analysis.getIPs()) {
      String country = this.geolocationDao.getCountry(ip);
      countryCount.put(country, countryCount.getOrDefault(country, 1));
    }

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(new Gson().toJson(countryCount));
  }


  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   * client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
