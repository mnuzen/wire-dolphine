package com.google.netpcapanalysis.models;

public final class PCAPdata {

  public String source;
  public String destination;
  public String domain;
  public String location;
  public String protocol;
  public int size;
  public boolean flagged;
  public int frequency;

  public PCAPdata() {
    this(null, null, null, null, null, 0, false, 0);
  }

  public PCAPdata(
      String source,
      String destination,
      String domain,
      String location,
      String protocol,
      int size,
      boolean flagged,
      int frequency
  ) {
    this.source = source;
    this.destination = destination;
    this.domain = domain;
    this.location = location;
    this.protocol = protocol;
    this.size = size;
    this.flagged = flagged;
    this.frequency = frequency;
  }

  public int getFrequency(){
    return this.frequency;
  }

}
