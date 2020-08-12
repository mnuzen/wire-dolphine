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
import com.google.netpcapanalysis.interfaces.dao.BucketDao;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BucketDaoImpl implements BucketDao {
  private ArrayList<PCAPdata> allPCAP; 
  private ArrayList<PCAPdata> sortedPCAP; 
  private LinkedHashMap<String, int[]> bucketData = new LinkedHashMap<>();
  private String myip = "";

  // int[UDP, TCP, IPv4, TOT]
  private int[] classA = new int[]{0, 0, 0, 0};
  private int[] classB = new int[]{0, 0, 0, 0};
  private int[] classC = new int[]{0, 0, 0, 0};
  private int[] classDE = new int[]{0, 0, 0, 0};

  public BucketDaoImpl(ArrayList<PCAPdata> packets) {
    allPCAP = packets; 
    orderIPs();
    loadBuckets();    
  }

  public LinkedHashMap<String, int[]> getBuckets() {
    return bucketData;
  }

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

  private void sortIPs() {
    // sort by destination ip addresses
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

  private void loadBuckets() {
    // loop through sorted IPs 
    for (PCAPdata packet : sortedPCAP) {
      String[] ip = packet.destination.split("\\.");
      String byteStr = ip[0];
      int byteInt = Integer.parseInt(byteStr);

      // Class A -- [1.0.0.0, 128.0.0.0)
      if (byteInt < 128) {
        classA[3] += 1;
        // int[UDP, TCP, IPv4, TOT]
        if (packet.protocol.equals("UDP")) {
          classA[0] += 1;
        }
        else if (packet.protocol.equals("TCP")) {
          classA[1] += 1;
        }
        else {
          classA[2] += 1;
        }
      }

      // Class B -- [128.0.0.0, 192.0.0.0)
      else if (byteInt < 192) {
        // int[UDP, TCP, IPv4, TOT]
        classB[3] += 1;
        if (packet.protocol.equals("UDP")) {
          classB[0] += 1;
        }
        else if (packet.protocol.equals("TCP")) {
          classB[1] += 1;
        }
        else {
          classB[2] += 1;
        }
      }

      // Class C -- [192.0.0.0, 224.0.0.0)
      else if (byteInt < 224) {
        classC[3] += 1;
        // int[UDP, TCP, IPv4, TOT]
        if (packet.protocol.equals("UDP")) {
          classC[0] += 1;
        }
        else if (packet.protocol.equals("TCP")) {
          classC[1] += 1;
        }
        else {
          classC[2] += 1;
        }
      }

      // Class D & E-- [224.0.0.0, 255.0.0.0)
      else {
        classDE[3] += 1;
        // int[UDP, TCP, IPv4, TOT]
        if (packet.protocol.equals("UDP")) {
          classDE[0] += 1;
        }
        else if (packet.protocol.equals("TCP")) {
          classDE[1] += 1;
        }
        else {
          classDE[2] += 1;
        }
      }

       
    } // end of for loop
    
    bucketData.put("Class A", classA);
    bucketData.put("Class B", classB);
    bucketData.put("Class C", classC);
    bucketData.put("Classes D & E", classDE);
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
