package com.google.sps.servlets;

import com.google.sps.dao.AnalysisDao;
import com.google.sps.dao.GeolocationDao;
import com.google.sps.models.PCAPAnalysis;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ipgeolocation")
public class IPGeolocationServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String id = getParameter(request, "PCAPId", "");
      PCAPAnalysis analysis = AnalysisDao.getAnalysis(id);

      if (analysis == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }

      Map<String, Integer> countryCount = new HashMap<>();

      for (String ip: analysis.getIps()) {
        String country = GeolocationDao.getCountryByIP(ip);
        countryCount.put(country, countryCount.getOrDefault(country, 1));
      }

      // mock
      countryCount.put("Armenia", 43);
      countryCount.put("Jordan", 67);
      countryCount.put("Estonia", 99);
      countryCount.put("South Korea", 21);
      countryCount.put("Canada", 66);

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
