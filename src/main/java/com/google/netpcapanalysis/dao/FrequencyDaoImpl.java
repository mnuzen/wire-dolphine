package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.models.Flagged;
import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;
import java.lang.*;

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
import com.google.netpcapanalysis.interfaces.dao.FrequencyDao;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FrequencyDaoImpl implements FrequencyDao {
  private ArrayList<PCAPdata> allPCAP; 
  private ArrayList<PCAPdata> finalFreq;
  private HashMap<String, PCAPdata> finalMap;
  private String myip = "";
  private String filename;  
  private boolean first = true;

  public FrequencyDaoImpl(ArrayList<PCAPdata> packets) {
    allPCAP = packets; 
  }

  /* Retrieve all necessary entities, processes, and puts into final arraylist */
  public void loadFrequency() {
    findMyIP();
    processData();
    putFinalFreq();
  }

  public ArrayList<PCAPdata> getAllPCAP() {
    return allPCAP;
  }

  /* Find local ip address based on highest recurring IP address */
  private void findMyIP() {
    HashMap<String, Integer> hm = new HashMap<String, Integer>();
    for (PCAPdata packet : allPCAP) {
      // source
      if (hm.containsKey(packet.source)) { 
        // if IP already exists, increment by 1
        hm.merge(packet.source, 1, Integer::sum);
      }
      else {
        hm.put(packet.source, 1);
      }
      // destination
      if (hm.containsKey(packet.destination)) { 
        // if IP already exists, increment
        hm.merge(packet.destination, 1, Integer::sum);
      }
      else {
        hm.put(packet.destination, 1);
      }
    }
    // find largest recurrence
    myip = Collections.max(hm.entrySet(), Map.Entry.comparingByValue()).getKey();
    //myip = key;
  }

  public String getMyIP() {
    return myip;
  }

  /* Fills out finalMap with frequencies based on IP address only (not protocol). */
  private void processData(){
    finalMap = new HashMap<String, PCAPdata>();
    for (PCAPdata packet : allPCAP) {
      String srcip = packet.source;
      String dstip = packet.destination;
      String protocol = "";
      String outip = "";

      if (srcip.equals(myip)) {
        outip = dstip;
      }
      else {
        outip = srcip;
      }
      
      // PCAPdata takes in (source, destination, domain, location, protocol, size, flagged, frequency) 
      if (finalMap.containsKey(outip)){
        // retrieve current value with outip and increments frequency
        PCAPdata currPCAP = finalMap.get(outip);
        currPCAP.incrementFrequency();
      }
      else {
        PCAPdata tempPCAP = new PCAPdata(myip, outip, "", "", packet.protocol, packet.size, packet.flagged, packet.frequency); 
        finalMap.put(outip, tempPCAP);
      }
    }
  }

  public HashMap<String, PCAPdata> getFinalMap() {
    return finalMap;
  }

  /* Transfers all unique connections to an arraylist and sorts for return. */
  private void putFinalFreq() {
    finalFreq = new ArrayList<PCAPdata>();
    for (PCAPdata packet : finalMap.values()) {
      finalFreq.add(packet);
    }

    // sort by destination ip addresses
    Collections.sort(finalFreq, new Comparator<PCAPdata>() {
      @Override
      public int compare(PCAPdata p1, PCAPdata p2) {
        String[] ip1 = p1.destination.split("\\.");
        String ipFormatted1 = String.format("%3s.%3s.%3s.%3s", ip1[0],ip1[1],ip1[2],ip1[3]);
        String[] ip2 = p2.destination.split("\\.");
        String ipFormatted2 = String.format("%3s.%3s.%3s.%3s",  ip2[0],ip2[1],ip2[2],ip2[3]);
        return ipFormatted1.compareTo(ipFormatted2);
      }
    });
  }

  public ArrayList<PCAPdata> getFinalFreq() {
    return finalFreq;
  }

}