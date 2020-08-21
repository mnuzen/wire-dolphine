package com.google.netpcapanalysis.interfaces.dao;

import java.util.ArrayList;
import com.google.netpcapanalysis.models.PCAPdata;
import java.net.InetAddress;

public interface GeolocationDao {

  /**
   * Gets country of IP
   * @param ip
   * @return Returns case-sensitive (ie capitalized), otherwise "unknown" if country unknown
   */
  public String getCountry(InetAddress ip);
  public ArrayList<PCAPdata> getLocation(ArrayList<PCAPdata> data);
}
