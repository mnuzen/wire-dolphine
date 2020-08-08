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

import com.google.netpcapanalysis.dao.PCAPParserDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;

import com.google.netpcapanalysis.utils.NetUtils;
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

import java.util.*; 
import com.google.gson.Gson;

import java.io.IOException;
import java.io.*;

/** Servlet that processes raw PCAP files within the repo and puts data to Datastore by calling PCAPParserDao. */
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {
  HashMap<String, PCAPdata> allPCAP = new HashMap<String, PCAPdata>();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve user input to determine file name and local IP address.
    String file = NetUtils.getParameter(request, "file-input", "");
   
    PCAPParserDao parser = new PCAPParserDaoImpl(file);
    parser.parseRaw();
    parser.processData();
    parser.putDatastore(); 

    // Respond with the result.
    response.sendRedirect("/packet.html");
  }
}