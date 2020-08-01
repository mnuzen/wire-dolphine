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


/** Servlet that processes raw PCAP files within the repo. 
   * We define MYIP to be the IP address of the local machine that was used to create the PCAP file, 
     and OUTIP to be all of the IP addresses that machine connected with.
   * allPCAP HashMap indexes all unique OUTIPs in a PCAP file to a PCAPdata object that stores needed attributes, 
     including the number of connections between MYIP and OUTIP 
   * We load datastore with all of the unique PCAPdata objects from our HashMap with a given entity tag.
*/
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {
  HashMap<String, PCAPdata> allPCAP = new HashMap<String, PCAPdata>();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); //creates database

  //static final String FILENAME = "WEB-INF/files/traffic.pcap";
  //static final String FILENAME = "WEB-INF/chargen-udp.pcap";
  //static final String FILENAME = "WEB-INF/files/chargen-tcp.pcap";
  static final String FILENAME = "WEB-INF/files/smallFlows.pcap";

  static final String MYIP = "192.168.3.131"; // for smallFlows.pcap 
  static final String ENTITY_TAG = "file_1";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    parseRaw(FILENAME);
    putDatastore(ENTITY_TAG);
  }

  /* Reads PCAP file (from file name) as a stream and puts unique connections into allPCAP HashMap. */
  public void parseRaw(String file) throws IOException {
    final InputStream stream = new FileInputStream(file);
    final Pcap pcap = Pcap.openStream(stream);

    pcap.loop(new PacketHandler() {
      @Override
      public boolean nextPacket(final Packet packet) throws IOException {
      if(packet.hasProtocol(Protocol.IPv4)) {// is the first packet always (MYIP, OUTIP)?
        IPPacket ip = (IPPacket) packet.getPacket(Protocol.IPv4);
        String protocol = getProtocol(ip); 
        String OUTIP = "";

        Buffer buffer = ip.getPayload();
        int size = buffer.getReadableBytes();
            
        //The IP addresses involved
        String srcip = ip.getSourceIP();
        String dstip = ip.getDestinationIP();

        if (srcip == MYIP) {
            OUTIP = dstip;
        }
        else {
            OUTIP = srcip;
        }

        // PCAPdata tempPCAP = new PCAPdata(source, destination, domain, location, protocol, size, flagged, frequency);
        if (allPCAP.containsKey(OUTIP)){
            PCAPdata tempPCAP = new PCAPdata(MYIP, OUTIP, "", "", protocol, size, false, allPCAP.get(OUTIP).getFrequency()+1); // how to use increment frequency method instead of getting freq++?
            allPCAP.put(OUTIP, tempPCAP);
        }
        else {
            PCAPdata tempPCAP = new PCAPdata(MYIP, OUTIP, "", "", protocol, size, false, 1);
            allPCAP.put(OUTIP, tempPCAP);
        }
      }
      return true;
    }
   });
   pcap.close();
  } 

  /* Determine the protocol of IPv4 packet; if unknown, then returns "IPv4" as protocol.
    * Checks for the following protocols — ICMP, IGMP, TCP, UDP, SCTP, SIP, SDP, ETHERNET_II, SLL, IPv4, RTP —
    * which are the only protocols supported by pkts library. */
  public String getProtocol(IPPacket packet) throws IOException {
    String protocol = "IPv4";
    if (packet.hasProtocol(Protocol.UDP)) {
      protocol = "UDP";
    }
    else if (packet.hasProtocol(Protocol.TCP)) {
      protocol = "TCP";
    } 
    else if (packet.hasProtocol(Protocol.ICMP)) {
      protocol = "ICMP";
    }
    else if (packet.hasProtocol(Protocol.IGMP)) {
      protocol = "IGMP";
    }
    else if (packet.hasProtocol(Protocol.SCTP)) {
      protocol = "SCTP";
    }
    else if (packet.hasProtocol(Protocol.SIP)) {
      protocol = "SIP";
    }
    else if (packet.hasProtocol(Protocol.SDP)) {
      protocol = "SDP";
    }
    else if (packet.hasProtocol(Protocol.ETHERNET_II)) {
      protocol = "ETHERNET_II";
    }
    else if (packet.hasProtocol(Protocol.SLL)) {
      protocol = "SLL";
    }
    return protocol;
  }

  /* Adds all processed packets to datastore through GenericPCAPDao*/
  public void putDatastore(String tag){
    for (PCAPdata temp : allPCAP.values()) {
      GenericPCAPDao data = new GenericPCAPDao();
      data.setPCAPObjects(temp, tag);
    }
  } 


} // end of PacketParserServlet class




/* If packet has UDP or TCP protocol, fetch the port numbers (source/destination).*/
/**    if (packet.hasProtocol(Protocol.UDP)) {
            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
            int dstport = udpPacket.getDestinationPort();
            int srcport = udpPacket.getSourcePort();
          }
          else if (packet.hasProtocol(Protocol.TCP)) {
            TCPPacket tcpPacket = (Tmvn cCPPacket) packet.getPacket(Protocol.TCP);
            int dstport = tcpPacket.getDestinationPort();
            int srcport = tcpPacket.getSourcePort();
          }*/