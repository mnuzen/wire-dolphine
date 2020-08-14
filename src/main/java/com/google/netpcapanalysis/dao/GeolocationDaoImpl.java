package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import com.google.netpcapanalysis.interfaces.dao.GeolocationDao;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;

public class GeolocationDaoImpl implements GeolocationDao {
  private static final String GEO_DB_LOCATION = "GeoLite2-City.mmdb";
  private File database;
  private DatabaseReader reader;
  private Cache<InetAddress, String> cache;

  public GeolocationDaoImpl() {
    cache = new CacheBuilder<InetAddress, String>()
        .setCacheName("geolocation")
        .setExpiration(600000) // 10 min
        .setMaxItems(1000)
        .setType(CacheType.MEMORY)
        .build();

    try {
      URL geoDBUrl = GeolocationDaoImpl.class.getClassLoader().getResource(GEO_DB_LOCATION);
      database = new File(geoDBUrl.toURI());
      reader = new DatabaseReader.Builder(database).build();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
  
  public String getCountry(InetAddress ip) {
    String result = cache.get(ip);
    if (result != null) {
      return result;
    }

    try {
      InetAddress ipAddress = InetAddress.getByName(ip.getHostAddress());
      CityResponse response = reader.city(ipAddress);

      Country country = response.getCountry();
      cache.putCache(ip, country.getName());
      return country.getName();
    } catch (Exception e) {
      return "unknown";
    }
  }
}
