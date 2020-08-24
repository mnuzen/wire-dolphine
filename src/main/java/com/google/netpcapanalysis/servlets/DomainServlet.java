package com.google.netpcapanalysis.servlets;

import com.google.gson.Gson;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import com.google.netpcapanalysis.models.DNSRecord;
import com.google.netpcapanalysis.models.FileAttribute;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.utils.SessionManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/reversedns")
public class DomainServlet extends HttpServlet {

  private ReverseDNSLookupDao reverseDNSLookupDao;
  private PCAPDao pcapDao;

  public void init(ServletConfig conf) {
    this.reverseDNSLookupDao = new ReverseDNSLookupDaoImpl();
    this.pcapDao = new PCAPDaoImpl();
  }

  /**
   * @param request  Requires query string with param `PCAPId`
   * @param response response is a JSON with a `cdn` and `domain` keys containing {[hostname:
   *                 string]: integer} JSON relating hostname to packet #
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

    Map<String, Integer> domainCount = new HashMap<>();
    Map<String, Integer> cdnCount = new HashMap<>();
    Map<String, DNSRecord> records = new HashMap<>();

    for (PCAPdata pcap : analysis) {
      String searchIP;
      if (pcap.source.equals(entity.myIP)) {
        searchIP = pcap.destination;
      } else {
        searchIP = pcap.source;
      }

      DNSRecord record;
      if ((record = records.get(searchIP)) == null) {
        record = this.reverseDNSLookupDao.lookup(searchIP);
        records.put(searchIP, record);
      }

      if (record == null) continue;
      String hostname = record.getHostname();
      if (record.isServer()) {
        domainCount.put(hostname, domainCount.getOrDefault(hostname, 1));
      } else {
        cdnCount.put(hostname, cdnCount.getOrDefault(hostname, 1));
      }
    }

    Map<String, Map<String, Integer>> res = new HashMap<>();
    res.put("domain", domainCount);
    res.put("cdn", cdnCount);

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(new Gson().toJson(res));
  }
}
