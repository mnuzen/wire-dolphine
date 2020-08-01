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

package com.google.sps.servlets;

import com.google.sps.datastore.PCAPdata;
import com.google.sps.datastore.GenericPCAPDao;

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


/** Servlet that processes comments.*/
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {
  ArrayList<PCAPdata> allData = new ArrayList<PCAPdata>();
  HashMap<String, PCAPdata> allPCAP = new HashMap<String, PCAPdata>();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  //static final String FILENAME = "WEB-INF/files/traffic.pcap";
  //static final String FILENAME = "WEB-INF/chargen-udp.pcap";
  static final String FILENAME = "WEB-INF/files/chargen-tcp.pcap";
  static final String MYIP = "185.47.63.113";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    parseRaw();
    addToPCAPDao();
  }

  /* Reads PCAP file (from filename) as a stream and puts into datastore. */
  public void parseRaw() throws IOException {
    final InputStream stream = new FileInputStream(FILENAME);
    final Pcap pcap = Pcap.openStream(stream);

    pcap.loop(new PacketHandler() {
      @Override
      public boolean nextPacket(final Packet packet) throws IOException {
      if(packet.hasProtocol(Protocol.IPv4)) {
        IPPacket ip = (IPPacket) packet.getPacket(Protocol.IPv4);
        String protocol = "IPv4";
            
        //The IP addresses involved
        String dstip = ip.getDestinationIP();
        String srcip = ip.getSourceIP();

        if (packet.hasProtocol(Protocol.UDP)) {
          protocol = "UDP";
        }
        else if (packet.hasProtocol(Protocol.TCP)) {
          protocol = "TCP";
        }

        // PCAPdata tempPCAP = new PCAPdata(source, destination, domain, location, protocol, size, flagged, frequency);
        PCAPdata tempPCAP = new PCAPdata(srcip, dstip, "wiki", "loc", protocol, 2, false, 1);
        tempPCAP.incrementFrequency();
        allPCAP.put(dstip, tempPCAP);
      }
      return true;
    }
   });
   pcap.close();
  } // end of parseRaw function

  public void addToPCAPDao(){
    for (PCAPdata temp : allPCAP.values()) {
      GenericPCAPDao data = new GenericPCAPDao();
      data.setPCAPObjects(temp, "file_1");
    }
  } // end of addToPCAPDao function


} // end of PacketParserServlet class

/**    if (packet.hasProtocol(Protocol.UDP)) {
            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
            int dstport = udpPacket.getDestinationPort();
            int srcport = udpPacket.getSourcePort();
          }
          else if (packet.hasProtocol(Protocol.TCP)) {
            TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
            int dstport = tcpPacket.getDestinationPort();
            int srcport = tcpPacket.getSourcePort();
          }*/