package com.google.netpcapanalysis.interfaces.dao;

import java.net.InetAddress;

public interface GeolocationDao {

  /**
   * Gets country of IP
   * @param ip
   * @return Returns case-sensitive (ie capitalized), otherwise "unknown" if country unknown
   */
  public String getCountry(InetAddress ip);
}
