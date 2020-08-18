// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License. 

package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.models.PCAPdata;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;

import com.google.netpcapanalysis.dao.BucketDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.BucketDao;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.packet.IPPacket;
import io.pkts.protocol.Protocol;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.InputStream;
import java.io.IOException;

/** Servlet that retrieves and returns IP addresses. */
@WebServlet("/PCAP-IP")
public class IPLoaderServlet extends HttpServlet {
  private String filename;
  private PCAPDao data = new PCAPDaoImpl();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BucketDao bucket = new BucketDaoImpl(data.getPCAPObjects(filename));
    LinkedHashMap<String, Integer> finalFreq = bucket.getFinalMap();
    String json = convertToJsonUsingGson(finalFreq);
    response.setContentType("application/json;");
    response.getWriter().println(json); 
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    filename = getParameter(request, "file-input", "");
    response.sendRedirect("/bucket.html");
  }

  private String convertToJsonUsingGson(LinkedHashMap<String, Integer> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

} // end of BucketLoaderServlet class