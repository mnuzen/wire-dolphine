package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.models.Flagged;
import com.google.netpcapanalysis.models.PCAPdata;
import java.util.*; 
import java.io.*;
import java.lang.*;
import java.util.Map.Entry;

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

  // Map to store <String Class, LinkedHashMap<> of protocols and frequencies >
  private LinkedHashMap<String, LinkedHashMap<String, Integer>> bucketData;
  
  private String classA = "Class A";
  private String classB = "Class B";
  private String classC = "Class C";
  private String classDE = "Class DE";

  public BucketDaoImpl(ArrayList<PCAPdata> packets) {
    allPCAP = packets; 
    orderIPs();
    loadBuckets();    
  }

  public LinkedHashMap<String, LinkedHashMap<String, Integer>> getBuckets() {
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
    bucketData = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();

    // Map to store <String Protocol, int Frequency of appearance>
    LinkedHashMap<String, Integer> protocolA = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> protocolB = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> protocolC = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> protocolDE = new LinkedHashMap<String, Integer>();
   
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

  /* Sorts protocols in alphabetical order for ease of viewing in visualization */
  /*private LinkedHashMap<String, Integer> sortProtocols(LinkedHashMap<String, Integer> hm){
    Set<Map.Entry<String, Integer>> set = hm.entrySet();
    List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(set);

    // sort entries
    Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
        return e1.getKey().compareTo(e2.getKey()); // sort them in reverse order for visualization 
      }
    });

    // put back to HashMap
    LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
    for (Map.Entry<String, Integer> o : entries) { 
      temp.put(o.getKey(), o.getValue()); 
    } 
    return temp; 
  }*/

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
