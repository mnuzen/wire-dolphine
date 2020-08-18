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
import java.util.Set;
import java.util.List;

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
  private static final int IP_SIZE = 4; // size of full IP address
  
  private ArrayList<PCAPdata> allPCAP; 
  private ArrayList<PCAPdata> sortedPCAP; 
  private String myip = "";
  
  private String classA = "Class A";
  private String classB = "Class B";
  private String classC = "Class C";
  private String classDE = "Class D & E";

  private HashMap<String, Integer> protocolsA;
  private HashMap<String, Integer> protocolsB;
  private HashMap<String, Integer> protocolsC;
  private HashMap<String, Integer> protocolsDE;

  // Map to store <String Class, HashMap<> of protocols and frequencies 
  private LinkedHashMap<String, HashMap<String, Integer>> bucketData;
  
  // Map to store <IP, frequency> regardless of protocol
  private LinkedHashMap<String, Integer> finalMap;


  public BucketDaoImpl(ArrayList<PCAPdata> packets) {
    allPCAP = packets; 
    orderIPs();
    condenseByteOne();
    loadBuckets();    
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
    sortIPList();
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

  /* Sort array of PCAPdata objects by destination IP address. */
  private void sortIPList() {
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
    // Maps to store <String Protocol, int Frequency of appearance>
    protocolsA = new HashMap<String, Integer>();
    protocolsB = new HashMap<String, Integer>();
    protocolsC = new HashMap<String, Integer>();
    protocolsDE = new HashMap<String, Integer>();
   
    // loop through sorted IPs 
    for (PCAPdata packet : sortedPCAP) {
      String[] ip = packet.destination.split("\\.");
      int byteInt = Integer.parseInt(ip[0]);
      getIPClass(byteInt, packet);
    } 

    bucketData.put(classA, protocolsA);
    bucketData.put(classB, protocolsB);
    bucketData.put(classC, protocolsC);
    bucketData.put(classDE, protocolsDE);
  }

  private void getIPClass(int byteInt, PCAPdata packet) {
    // Class A -- [1.0.0.0, 128.0.0.0)
    if (byteInt < 128) {
      // puts protocol into map
      putProtocolMap(protocolsA, packet.protocol);
    }
    // Class B -- [128.0.0.0, 192.0.0.0)
    else if (byteInt >= 128 && byteInt < 192) {
      putProtocolMap(protocolsB, packet.protocol);
    }
    // Class C -- [192.0.0.0, 224.0.0.0)
    else if (byteInt >= 192 && byteInt < 224) {
      putProtocolMap(protocolsA, packet.protocol);
    }
    // Class D & E-- [224.0.0.0, 255.0.0.0)
    else {
      putProtocolMap(protocolsDE, packet.protocol);
    }
  }

  private void putProtocolMap(HashMap<String, Integer> protocolMap, String proto) {
    if (protocolMap.containsKey(proto)) {
      protocolMap.merge(proto, 1, Integer::sum);
    }
    else {
      protocolMap.put(proto, 1);
    }
  }

  /* Retrieves and condenses IP addresses based on first 8 bits */
  private void condenseByteOne() {
    HashMap<String, ArrayList<PCAPdata>> bytes = new HashMap<String, ArrayList<PCAPdata>>();
    // extract all unique first bytes
    for (PCAPdata packet : sortedPCAP) {
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

    LinkedHashMap<String, Integer> prefixMap = new LinkedHashMap<String, Integer>();
      // put all unique first bytes into map with longest common prefixes as destinations
    for (String key : bytes.keySet()) {
      ArrayList<PCAPdata> arr = bytes.get(key);
      // if size is 1, just put that packet in
      if (arr.size() == 1) {
        PCAPdata temp = arr.get(0);
        prefixMap.put(temp.destination, 1);
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
        // longest common prefix
        String prefix = longestCommonPrefix(dests);
        prefixMap.put(prefix, dests.length);
      }
    }

    sortIPMap(prefixMap);
  } 

  /* Sorts a Map of addresses based on first byte */
  private void sortIPMap(LinkedHashMap<String, Integer> prefixMap) {
    // sort ips based on first byte
    Set<Map.Entry<String, Integer>> set = prefixMap.entrySet();
    List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(set);
    Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> p1, Entry<String, Integer> p2) {
        String[] byte1 = p1.getKey().split("\\.");
        Integer b1 = Integer.parseInt(byte1[0]);
        String[] byte2 = p2.getKey().split("\\.");
        Integer b2 = Integer.parseInt(byte2[0]);
        return b1.compareTo(b2); // increasing order
      }
    });

    finalMap = new LinkedHashMap<String, Integer>();
    for(Map.Entry<String, Integer> map : entries){
      String key = parsePrefix(map.getKey());
      finalMap.put(key, map.getValue());
    }
  }

  /* Retrieves proper prefix namings */
  private String parsePrefix(String ip) {
    String[] ips = ip.split("\\.");
    int len = ips.length;
    String ret = "";
    if (len == IP_SIZE) { // full IP address is returned
      ret = ip;
    }
    else if (len == IP_SIZE-1) {
      ret = ips[0] + "." + ips[1] + "." + ips[2] + ".0/16";
    }
    else if (len == IP_SIZE-2) {
      ret = ips[0] + "." + ips[1] + ".0.0/16";
    }
    else {
      ret = ips[0] + ".0.0.0/8";
    }
    return ret;
  }

  public String getMyIP() {
    return myip;
  }

  public ArrayList<PCAPdata> getSortedPCAP(){
    return sortedPCAP;
  }

  public LinkedHashMap<String, HashMap<String, Integer>> getFinalBuckets() {
    return bucketData;
  }

  public LinkedHashMap<String, Integer> getFinalMap() {
    return finalMap;
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
