package com.google.sps.models;

public class PCAPAnalysis {

  private String id;
  private String[] ips;

  public PCAPAnalysis(String id, String[] ips) {
    this.id = id;
    this.ips = ips;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String[] getIps() {
    return ips;
  }

  public void setIps(String[] ips) {
    this.ips = ips;
  }
}
