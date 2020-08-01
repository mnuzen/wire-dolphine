package com.google.sps.datastore;

//PCAP storage class
public final class PCAPdata {

  String source;
  String destination;
  String domain;
  String location;
  String protocol;
  int size;
  boolean flagged;
  int frequency;

  public PCAPdata(String source, String destination, String domain, String location, String protocol, 
      int size, boolean flagged, int frequency) {
    this.source = source;
    this.destination = destination;
    this.domain = domain;
    this.location = location;
    this.protocol = protocol;
    this.size = size;
    this.flagged = flagged;
    this.frequency = frequency;
  }

  public PCAPdata() {
    this.source = null;
    this.destination = null;
    this.domain = null;
    this.location = null;
    this.protocol = null;
    this.size = 0;
    this.flagged = false;
    this.frequency = 0;
  }

  public int getFrequency() {
    return this.frequency;
  }

}