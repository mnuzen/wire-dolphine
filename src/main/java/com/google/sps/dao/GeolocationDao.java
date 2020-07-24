package com.google.sps.dao;

import com.google.sps.Mock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GeolocationDao {

  public static Map<String, String> ipCountryMap;

  static {
    Map<String, String> ipMap = new HashMap<>();

    for (int i = 0; i < 4; i++) {
      ipMap.put(Mock.IPs[i], Mock.COUNTRIES[i]);
    }
    ipCountryMap = Collections.unmodifiableMap(ipMap);
  }

  public static String getCountryByIP(String ip) {
    return ipCountryMap.getOrDefault(ip, "unknown");
  }
}
