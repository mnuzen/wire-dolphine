package com.google.netpcapanalysis.interfaces.dao;

import java.util.ArrayList;
import com.google.netpcapanalysis.models.PCAPdata;
import java.net.InetAddress;
import java.util.List;

public interface GeolocationDao {

  /**
   * Gets country of IP
   * @param ip
   * @return Returns case-sensitive (ie capitalized), otherwise "unknown" if country unknown
   */
  public String getCountry(InetAddress ip);
  public List<PCAPdata> getLocation(List<PCAPdata> data);
}
