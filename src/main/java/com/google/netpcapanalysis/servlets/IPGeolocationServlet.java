package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.interfaces.dao.GeolocationDao;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.dao.GeolocationDaoImpl;
import com.google.gson.Gson;
import com.google.netpcapanalysis.models.FileAttribute;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.utils.NetUtils;
import com.google.netpcapanalysis.utils.SessionManager;

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
    //String id = NetUtils.getParameter(request, "PCAPId", "");
    String id = SessionManager.getSessionEntity(request);
    List<PCAPdata> analysis = this.pcapDao.getPCAPObjects(id);
    FileAttribute entity = this.pcapDao.getFileAttribute(id);

    if (analysis == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Map<String, Integer> countryCount = new HashMap<>();

    for (PCAPdata pcap: analysis) {

      String searchIP;
      if(pcap.source.equals(entity.myIP))
      {
          searchIP = pcap.destination;
      }
      else{
          searchIP = pcap.source;
      }

      String country = this.geolocationDao.getCountry(InetAddress.getByName(searchIP));
      countryCount.put(country, countryCount.getOrDefault(country, 1) + 1);
    }

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(new Gson().toJson(countryCount));
  }
}
