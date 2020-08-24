package com.google.netpcapanalysis.utils;

import com.google.netpcapanalysis.models.PCAPdata;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class UtilityPCAP {

  private UtilityPCAP() {

  }

  static public String hashText(String text) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(text.getBytes());

      BigInteger no = new BigInteger(1, messageDigest);

      StringBuilder hashtext = new StringBuilder(no.toString(16));
      while (hashtext.length() < 32) {
        hashtext.insert(0, "0");
      }
      return hashtext.toString();
    }

    // For specifying wrong message digest algorithms
    catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  //Gets most use IP in PCAPdata
  static public String findMyIP(List<PCAPdata> allData) {
    String myip;
    HashMap<String, Integer> hm = new HashMap<>();
    for (PCAPdata packet : allData) {
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
    return myip;
  }

  //Finds all unique IPs and sets myip to source
  static public List<PCAPdata> getUniqueIPs(List<PCAPdata> allData) {
    HashMap<String, PCAPdata> finalMap = new HashMap<>();
    String myip = findMyIP(allData);
    String outip;

    for (PCAPdata packet : allData) {
      //swaps packet order based on myip
      if (packet.source.equals(myip)) {
        outip = packet.destination;
      } else {
        outip = packet.source;
      }

      //puts data into map if not already there
      if (!finalMap.containsKey(outip)) {
        PCAPdata tempPCAP = new PCAPdata(myip, outip, packet.protocol, packet.size);
        finalMap.put(outip, tempPCAP);
      }
    }
    return (new ArrayList<PCAPdata>(finalMap.values()));
  }

}
