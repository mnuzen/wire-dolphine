package com.google.sps.dao;

import com.google.sps.Mock;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GeolocationDao {

  private static File database;
  private static DatabaseReader reader;

  static {
    database = new File("./data/GeoLite2-City.mmdb");
    try {
      reader = new DatabaseReader.Builder(database).build();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getCountryByIP(String ip) {
    try {
      InetAddress ipAddress = InetAddress.getByName(ip);
      CityResponse response = reader.city(ipAddress);

      Country country = response.getCountry();
      return country.getName();
    } catch (Exception e) {
      e.printStackTrace();
      return "unknown";
    }
  }
}
