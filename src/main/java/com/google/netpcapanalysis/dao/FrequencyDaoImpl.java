package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.models.Flagged;
import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.util.Map.Entry;

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
  private LinkedHashMap<String, Integer> finalMap;

  private String myip = "";
  private boolean first = true;
  private int NUM_NODES = 15;

  public FrequencyDaoImpl(ArrayList<PCAPdata> packets) {
    allPCAP = packets; 
    findMyIP();
    processData();
  }

  public ArrayList<PCAPdata> getAllPCAP() {
    return allPCAP;
  }

  /* Find local IP address based on highest recurring IP address */
  private void findMyIP() {
    HashMap<String, Integer> hm = new HashMap<String, Integer>();
    for (PCAPdata packet : allPCAP) {
      // source
      if (hm.containsKey(packet.source)) { 
        // if IP already exists, increment
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
  }

  public String getMyIP() {
    return myip;
  }

  /* Fills out finalMap with frequencies based on IP address only (not protocol). */
  private void processData(){
    LinkedHashMap<String, Integer> hm = getUniqueOutIPs();
    getFrequentIPs(hm);
  } 
  
  /* Sort IPs by frequency and get most frequent addresses*/
  private void getFrequentIPs(LinkedHashMap<String, Integer> hm) {
    finalMap = new LinkedHashMap<String, Integer>();
    Set<Map.Entry<String, Integer>> set = hm.entrySet();
    List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(set);
    Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
        return e2.getValue().compareTo(e1.getValue()); // reverse order
      }
    });
    
    // add top NUM_NODE frequencies
    int counter = 0;
    for(Map.Entry<String, Integer> map : entries){
      if (counter < NUM_NODES) {
        finalMap.put(map.getKey(), map.getValue());
        counter++;
      }
    }
  }

  /* Gets all unique outgoing IPs and puts OUTIP as key in hashmap, where value represents frequency */
  private LinkedHashMap<String, Integer> getUniqueOutIPs() {
    LinkedHashMap<String, Integer> hm = new LinkedHashMap<String, Integer>();
    for (PCAPdata packet : allPCAP) {
      String srcip = packet.source;
      String dstip = packet.destination;
      String outip = "";

      if (srcip.equals(myip)) {
        outip = dstip;
      }
      else {
        outip = srcip;
      }
      
      if (hm.containsKey(outip)){
        // retrieve current value with outip and increments frequency
        hm.merge(outip, 1, Integer::sum);
      }
      else {
        hm.put(outip, 1);
      }
    }
    return hm;
  }

  public LinkedHashMap<String, Integer> getFinalMap() {
    return finalMap;
  }

}