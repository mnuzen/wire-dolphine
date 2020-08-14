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
  private boolean first = true;
  private int NUM_NODES = 15;

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
        // if IP already exists, increment
        hm.merge(packet.source, 1, Integer::sum);
      } else {
        hm.put(packet.source, 1);
      }
      // destination
      if (hm.containsKey(packet.destination)) {
        // if IP already exists, increment
        hm.merge(packet.destination, 1, Integer::sum);
      } else {
        hm.put(packet.destination, 1);
      }
    }
    // find largest recurrence
    myip = Collections.max(hm.entrySet(), Map.Entry.comparingByValue()).getKey();
  }

  public String getMyIP() {
    return myip;
  }

  /* Fills out finalMap with frequencies based on IP address only (not protocol). */
  private void processData() {
    finalMap = new HashMap<String, PCAPdata>();
    for (PCAPdata packet : allPCAP) {
      String srcip = packet.source;
      String dstip = packet.destination;
      String protocol = "";
      String outip = "";

      if (srcip.equals(myip)) {
        outip = dstip;
      } else {
        outip = srcip;
      }

      // PCAPdata takes in (source, destination, domain, location, protocol, size, flagged, frequency) 
      if (finalMap.containsKey(outip)) {
        // retrieve current value with outip and increments frequency
        PCAPdata currPCAP = finalMap.get(outip);
        currPCAP.incrementFrequency();
      } else {
        PCAPdata tempPCAP = new PCAPdata(myip, outip, "", "", packet.protocol, packet.size,
            packet.flagged, packet.frequency);
        finalMap.put(outip, tempPCAP);
      }
    }
  }

  public HashMap<String, PCAPdata> getFinalMap() {
    return finalMap;
  }

  /* Transfers all unique connections to an arraylist for return. */
  private void putFinalFreq() {
    finalFreq = new ArrayList<PCAPdata>();
    ArrayList<PCAPdata> allValues = new ArrayList<PCAPdata>();

    for (PCAPdata packet : finalMap.values()) {
      allValues.add(packet);
    }
    // sort frequencies
    Collections.sort(allValues, new Comparator<PCAPdata>() {
      @Override
      public int compare(PCAPdata p1, PCAPdata p2) {
        Integer freq1 = p1.frequency;
        Integer freq2 = p2.frequency;
        return freq2.compareTo(freq1); // sorted frequencies in reverse (largest numbers first)
      }
    });

    // add top NUM_NODE frequencies
    for (int i = 0; i < NUM_NODES; i++) {
      if (i < allValues.size()) {
        finalFreq.add(allValues.get(i));
      }
    }
  }

  public ArrayList<PCAPdata> getFinalFreq() {
    return finalFreq;
  }

}