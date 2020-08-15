package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.models.Flagged;
import com.google.netpcapanalysis.models.PCAPdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

import java.io.InputStream;
import java.io.IOException;

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
import com.google.netpcapanalysis.interfaces.dao.BucketDao;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BucketDaoImpl implements BucketDao {
  private ArrayList<PCAPdata> allPCAP; 
  private ArrayList<PCAPdata> sortedPCAP; 
  private String myip = "";

  // Map to store <String Class, HashMap<> of protocols and frequencies >
  private LinkedHashMap<String, HashMap<String, Integer>> bucketData;
  
  private String classA = "Class A";
  private String classB = "Class B";
  private String classC = "Class C";
  private String classDE = "Class D & E";

  public BucketDaoImpl(ArrayList<PCAPdata> packets) {
    allPCAP = packets; 
    orderIPs();
    loadBuckets();    
  }

  public LinkedHashMap<String, HashMap<String, Integer>> getBuckets() {
    return bucketData;
  }
 
  /* Put packets into MYIP, OUTIP format and order by destination IP (ascending). */
  private void orderIPs(){
    // find local IP 
    findMyIP();

    // sort all packets such that MYIP, OUTIP is the formatting
    sortedPCAP = new ArrayList<PCAPdata>(); 
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
      
      PCAPdata tempPCAP = new PCAPdata(myip, outip, "", "", packet.protocol, packet.size, packet.flagged, packet.frequency); 
      sortedPCAP.add(tempPCAP);
    }
    // sort all packets in order of OUTIP
    sortIPs();
  }

  public ArrayList<PCAPdata> getSortedPCAP(){
    return sortedPCAP;
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

  /* Sort array of PCAPdata objects by destination IP address. */
  private void sortIPs() {
    Collections.sort(sortedPCAP, new Comparator<PCAPdata>() {
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

  /* Parsing protocols for each class. */
  private void loadBuckets() {
    bucketData = new LinkedHashMap<String, HashMap<String, Integer>>();

    // Map to store <String Protocol, int Frequency of appearance>
    HashMap<String, Integer> protocolA = new HashMap<String, Integer>();
    HashMap<String, Integer> protocolB = new HashMap<String, Integer>();
    HashMap<String, Integer> protocolC = new HashMap<String, Integer>();
    HashMap<String, Integer> protocolDE = new HashMap<String, Integer>();
   
    // loop through sorted IPs 
    for (PCAPdata packet : sortedPCAP) {
      String[] ip = packet.destination.split("\\.");
      String byteStr = ip[0];
      int byteInt = Integer.parseInt(byteStr);

      // Class A -- [1.0.0.0, 128.0.0.0)
      if (byteInt < 128) {
        // puts protocol into map
        String proto = packet.protocol;
        if (protocolA.containsKey(proto)) {
          protocolA.merge(proto, 1, Integer::sum);
        }
        else {
          protocolA.put(proto, 1);
        }
      }

      // Class B -- [128.0.0.0, 192.0.0.0)
      else if (byteInt >= 128 && byteInt < 192) {
        // puts protocol into map
        String proto = packet.protocol;
        if (protocolB.containsKey(proto)) {
          protocolB.merge(proto, 1, Integer::sum);
        }
        else {
          protocolB.put(proto, 1);
        }
      }

      // Class C -- [192.0.0.0, 224.0.0.0)
      else if (byteInt >= 192 && byteInt < 224) {
        // puts protocol into map
        String proto = packet.protocol;
        if (protocolC.containsKey(proto)) {
          protocolC.merge(proto, 1, Integer::sum);
        }
        else {
          protocolC.put(proto, 1);
        }
      }

      // Class D & E-- [224.0.0.0, 255.0.0.0)
      else {
        // puts protocol into map
        String proto = packet.protocol;
        if (protocolDE.containsKey(proto)) {
          protocolDE.merge(proto, 1, Integer::sum);
        }
        else {
          protocolDE.put(proto, 1);
        }
      }
    } // end of for loop

    bucketData.put(classA, protocolA);
    bucketData.put(classB, protocolB);
    bucketData.put(classC, protocolC);
    bucketData.put(classDE, protocolDE);
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
}
