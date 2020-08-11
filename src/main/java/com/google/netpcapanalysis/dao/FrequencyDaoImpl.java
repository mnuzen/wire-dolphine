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
    if (finalMap.size() > 50) {
      condenseByteOne();
    }
    else {
      for (PCAPdata packet : finalMap.values()) {
        finalFreq.add(packet);
      }
    }
    sortIPs();
  }

  private void condenseByteOne() {
      HashMap<String, ArrayList<PCAPdata>> bytes = new HashMap<String, ArrayList<PCAPdata>>();
      // extract all unique first bytes
      for (PCAPdata packet : finalMap.values()) {
        String[] ip = packet.destination.split("\\.");
        String firstByte = ip[0];
        if (bytes.containsKey(firstByte)) {
          ArrayList<PCAPdata> arr = bytes.get(firstByte);
          arr.add(packet);
        }
        else {
          ArrayList<PCAPdata> arr = new ArrayList<PCAPdata>();
          arr.add(packet);
          bytes.put(firstByte, arr);
        }
      }

      // put all unique first bytes into finalFreq with longest common prefixes as destinations
      for (String key : bytes.keySet()) {
        ArrayList<PCAPdata> arr = bytes.get(key);

        // if size if 1, just put that packet in
        if (arr.size() == 1) {
          PCAPdata temp = arr.get(0);
          finalFreq.add(temp);
        }

        // sum up all frequencies in that byte
        else {
          int freq = 0;
          String[] dests = new String[arr.size()];
          int i = 0;
          for (PCAPdata packet : arr) {
            freq += packet.frequency;
            dests[i] = packet.destination;
            i++;
          }

          // domain should be longest common prefix
          String prefix = longestCommonPrefix(dests)+".";

          // PCAPdata takes in (source, destination, domain, location, protocol, size, flagged, frequency) 
          PCAPdata temp = new PCAPdata(myip, prefix, "", "", "IPv4", 1, "false", freq);
          finalFreq.add(temp);
        }
      }
    }
  

  /* Finds longest common prefix between an array of strings in linear time: the algorithm makes log(m) iterations with m*n comparisons 
     each time, meaning our complexity would be O(s*log(m)) where S = sum of all chars in strings, n = number of strings, m = length of strings*/
  private String longestCommonPrefix(String[] strs) {
    if (strs == null || strs.length == 0)
      return "";
    int minLen = Integer.MAX_VALUE;
    for (String str : strs)
      minLen = Math.min(minLen, str.length());
    int low = 1;
    int high = minLen;
    while (low <= high) {
      int middle = (low + high) / 2;
      if (isCommonPrefix(strs, middle))
        low = middle + 1;
      else
        high = middle - 1;
    }
    return strs[0].substring(0, (low + high) / 2);
  }

  private boolean isCommonPrefix(String[] strs, int len){
    String str1 = strs[0].substring(0,len);
    for (int i = 1; i < strs.length; i++)
      if (!strs[i].startsWith(str1))
        return false;
    return true;
  }

  private void sortIPs() {
    // sort by destination ip addresses
    Collections.sort(finalFreq, new Comparator<PCAPdata>() {
      @Override
      public int compare(PCAPdata p1, PCAPdata p2) {
        String[] ip1 = p1.destination.split("\\.");
        String ipFormatted1 = String.format("%3s", ip1[0]);
        String[] ip2 = p2.destination.split("\\.");
        String ipFormatted2 = String.format("%3s",  ip2[0]);
        return ipFormatted1.compareTo(ipFormatted2);
      }
    });
  }

  public ArrayList<PCAPdata> getFinalFreq() {
    return finalFreq;
  }

}
