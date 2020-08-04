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
  HashMap<String, PCAPdata> allPCAP = new HashMap<String, PCAPdata>();

  private String FILENAME;
  private String MYIP;

  public PCAPParserDaoImpl(String file, String myip) {
      this.FILENAME = file;
      this.MYIP = myip;
  }

   /* Reads PCAP file (from file name) as a stream and puts unique connections into allPCAP HashMap. */
  public void parseRaw() throws IOException {
    final InputStream stream = new FileInputStream(FILENAME);
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
  private String getProtocol(IPPacket packet) throws IOException {
    String protocol = "IPv4";
    /* If packet has UDP or TCP protocol, fetch the port numbers (source/destination).*/
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

  /* Adds all processed packets to datastore through GenericPCAPDao*/
  public void putDatastore(){
    for (PCAPdata temp : allPCAP.values()) {
      PCAPDao data = new PCAPDaoImpl();
      data.setPCAPObjects(temp, FILENAME);
    }
  } 
}