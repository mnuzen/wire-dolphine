package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.models.Flagged;
import com.google.netpcapanalysis.models.PCAPdata;
import java.util.ArrayList; 
import java.io.IOException;
import java.io.InputStream;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.packet.IPPacket;
import io.pkts.protocol.Protocol;

import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;

import com.google.netpcapanalysis.utils.UtilityPCAP;
import com.google.netpcapanalysis.models.FileAttribute;

import java.nio.file.Path;
import java.nio.file.Paths;

/*
* Given a filename and local IP address, parses out individual packets as PCAPdata objects and puts in Datastore.
* We define myip to be the IP address of the local machine that was used to create the PCAP file, 
     and outip to be all of the IP addresses that machine connected with.
   * allPCAP HashMap indexes all unique outips in a PCAP file to a PCAPdata object that stores needed attributes, 
     including the number of connections between myip and outip 
   * We load datastore with all of the unique PCAPdata objects from our HashMap with a given entity tag.
   * Hard-coded the source IP address (myip) -- need to find a way to retrieve myip (look at first packet, ask user, use Whatsmyip?)
*/
public class PCAPParserDaoImpl implements PCAPParserDao {
  private final PCAPDao datastore = new PCAPDaoImpl();
  private ArrayList<PCAPdata> allPCAP = new ArrayList<PCAPdata>(); 
  private String filename;
  private String description;

  public PCAPParserDaoImpl(String file, String descript) { 
      this.filename = file;
      this.description = descript;
  }

   /* Reads PCAP file (from file name) as a stream and puts unique connections into allPCAP HashMap. */
  public void parseRaw() throws IOException {
    Path path = Paths.get(filename);
    InputStream stream = PCAPParserDaoImpl.class.getClassLoader().getResourceAsStream(path.toString());
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

        // PCAPdata takes in (source, destination, domain, location, protocol, size, flagged, frequency) 
        PCAPdata rawPCAP = new PCAPdata(srcip, dstip, "", "", protocol, size, Flagged.UNKNOWN, 1);
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
    String protocol = "Other";
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
 
  /* Retrieves hashed version of filename and adds all raw packets to datastore (under that name) through GenericPCAPDao*/
  public void putDatastore(){
    String entityName = UtilityPCAP.hashText(filename);
    datastore.setPCAPObjects(allPCAP, entityName);
    String myip = UtilityPCAP.findMyIP(allPCAP);
    FileAttribute data = new FileAttribute(entityName, filename, myip, description);
    datastore.setFileAttribute(data);
  } 

  /* Access elements for testing. */
  public ArrayList<PCAPdata> getAllPCAP() {
    return allPCAP;
  }
  
}