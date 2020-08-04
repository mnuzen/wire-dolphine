package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.packet.IPPacket;
import io.pkts.protocol.Protocol;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;

/*
* Given a filename and local IP address, parses out individual packets as PCAPdata objects and puts in Datastore.
* We define MYIP to be the IP address of the local machine that was used to create the PCAP file, 
     and OUTIP to be all of the IP addresses that machine connected with.
   * allPCAP HashMap indexes all unique OUTIPs in a PCAP file to a PCAPdata object that stores needed attributes, 
     including the number of connections between MYIP and OUTIP 
   * We load datastore with all of the unique PCAPdata objects from our HashMap with a given entity tag.
   * Hard-coded the source IP address (MYIP) -- need to find a way to retrieve MYIP (look at first packet, ask user, use WhatsMyIP?)
*/
public class PCAPParserDaoImpl implements PCAPParserDao {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  ArrayList<PCAPdata> allPCAP = new ArrayList<PCAPdata>(); 
  HashMap<String, PCAPdata> finalPCAP = new HashMap<String, PCAPdata>();

  private String FILENAME;
  private String MYIP = "";
  private boolean first = true;

  public PCAPParserDaoImpl(String file) { 
      this.FILENAME = file;
  }

   /* Reads PCAP file (from file name) as a stream and puts unique connections into allPCAP HashMap. */
  public void parseRaw() throws IOException {
    final InputStream stream = PCAPParserDaoImpl.class.getClassLoader().getResourceAsStream(FILENAME);
    final Pcap pcap = Pcap.openStream(stream);

    pcap.loop(new PacketHandler() {
      @Override
      public boolean nextPacket(final Packet packet) throws IOException {
      if(packet.hasProtocol(Protocol.IPv4)) {
        IPPacket ip = (IPPacket) packet.getPacket(Protocol.IPv4);

        String srcip = ip.getSourceIP();
        String dstip = ip.getDestinationIP();

        Buffer buffer = ip.getPayload();
        int size = buffer.getReadableBytes();

        String protocol = getProtocol(ip); 

        if (first) {
            MYIP = srcip;
            first = false;
        }
        
        // PCAPdata takes in (source, destination, domain, location, protocol, size, flagged, frequency) 
        PCAPdata rawPCAP = new PCAPdata(srcip, dstip, "", "", protocol, size, false, 1);
        allPCAP.add(rawPCAP);
      }
      return true;
    }
   });
   pcap.close();
  } 

  /* Determine the protocol of IPv4 packet; if unknown, then returns "IPv4" as protocol.
    * Checks for the following protocols — ICMP, IGMP, TCP, UDP, SCTP, SIP, SDP, ETHERNET_II, SLL, IPv4, RTP —
    * which are the only protocols supported by pkts library. */
  private String getProtocol(IPPacket packet) throws IOException {
    String protocol = "IPv4";
    // If packet has UDP or TCP protocol, fetch the port numbers (source/destination).
    if (packet.hasProtocol(Protocol.UDP)) {
      protocol = "UDP";
      UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
      int dstport = udpPacket.getDestinationPort();
      int srcport = udpPacket.getSourcePort();
    }
    else if (packet.hasProtocol(Protocol.TCP)) {
      protocol = "TCP";
      TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
      int dstport = tcpPacket.getDestinationPort();
      int srcport = tcpPacket.getSourcePort();
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
 
  /* Process raw data*/
  public void processData(){
    for (PCAPdata packet : allPCAP) {
      String OUTIP = "";
      String srcip = packet.source;
      String dstip = packet.destination;

      if (srcip == MYIP) {
        OUTIP = dstip;
      }
      else {
        OUTIP = srcip;
      }
      
      // PCAPdata takes in (source, destination, domain, location, protocol, size, flagged, frequency) 
      if (finalPCAP.containsKey(OUTIP)){
        // retrieve current value with OUTIP and increments frequency
        PCAPdata currPCAP = finalPCAP.get(OUTIP);
        currPCAP.incrementFrequency();
      }
      else {
        PCAPdata tempPCAP = new PCAPdata(MYIP, OUTIP, "", "", packet.protocol, packet.size, packet.flagged, packet.frequency); 
        finalPCAP.put(OUTIP, tempPCAP);
      }
    }
  }

  /* Adds all processed packets to datastore through GenericPCAPDao*/
  public void putDatastore(){
    for (PCAPdata packet : finalPCAP.values()) {
      PCAPDao data = new PCAPDaoImpl();
      data.setPCAPObjects(packet, FILENAME);
    }
  } 
}