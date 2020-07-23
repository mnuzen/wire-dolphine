package com.google.sps.data;

//PCAP storage class
public final class PCAPdata {

  String source;
  String destination;
  String domain;
  String location;
  String size;
  String protocal;
  Boolean flagged;
    

  public PCAPdata(String source, String destination, String domain, 
  String location, String size, String protocal, Boolean flagged) 
  {
  this.source = source;
  this.destination = destination;
  this.domain = domain;
  this.location = location;
  this.size = size;
  this.protocal = protocal;
  this.flagged = flagged;
  }

   public PCAPdata() {
  this.source = null;
  this.destination = null;
  this.domain = null;
  this.location = null;
  this.size = null;
  this.protocal = null;
  this.flagged = null; //false
  }

  public PCAPdata getPCAPdata() {
    PCAPdata temp = new PCAPdata(this.source, this.destination, this.domain, 
    this.location, this.size, this.protocal, this.flagged);

    return temp;
  }

}