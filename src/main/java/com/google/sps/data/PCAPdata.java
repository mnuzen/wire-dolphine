package com.google.sps.data;

//PCAP storage class
public final class PCAPdata {

  String source;
  String destination;
  String domain;
  String location;
  String size;
  String protocol;
  String time;
  String flagged;
    

  public PCAPdata(String source, String destination, String domain, 
  String location, String size, String protocol,String time, String flagged) 
  {
  this.source = source;
  this.destination = destination;
  this.domain = domain;
  this.location = location;
  this.size = size;
  this.protocol = protocol;
  this.time = time;
  this.flagged = flagged;
  }

   public PCAPdata() {
  this.source = null;
  this.destination = null;
  this.domain = null;
  this.location = null;
  this.size = null;
  this.protocol = null;
  this.time = null;
  this.flagged = null;
  }

  public PCAPdata getPCAPdata() {
    PCAPdata temp = new PCAPdata(this.source, this.destination, this.domain, 
    this.location, this.size, this.protocol, this.time, this.flagged);

    return temp;
  }

}