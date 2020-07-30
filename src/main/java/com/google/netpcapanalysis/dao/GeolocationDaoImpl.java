package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.interfaces.dao.GeolocationDao;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

public class GeolocationDaoImpl implements GeolocationDao {

  private File database;
  private DatabaseReader reader;

  public GeolocationDaoImpl() {
    try {
      URL geoDBUrl = GeolocationDaoImpl.class.getClassLoader().getResource("GeoLite2-City.mmdb");
      database = new File(geoDBUrl.toURI());
      reader = new DatabaseReader.Builder(database).build();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
  
  public String getCountry(InetAddress ip) {
    try {
      InetAddress ipAddress = InetAddress.getByName(ip.getHostAddress());
      CityResponse response = reader.city(ipAddress);

      Country country = response.getCountry();
      return country.getName();
    } catch (Exception e) {
      return "unknown";
    }
  }
}
