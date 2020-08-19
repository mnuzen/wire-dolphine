package com.google.netpcapanalysis.models;

public final class PCAPdata {

  public String source;
  public String destination;
  public String domain;
  public String location;
  public String protocol;
  public int size;
  public String flagged;
  public int port;
  public long time;

  public PCAPdata() {
    this(null, null, null, null, null, 0, null, 0, 0L);
  }

  public PCAPdata(
      String source,
      String destination,
      String domain,
      String location,
      String protocol,
      int size,
      String flagged,
      int port,
      long time
  ) {
    this.source = source;
    this.destination = destination;
    this.domain = domain;
    this.location = location;
    this.protocol = protocol;
    this.size = size;
    this.flagged = flagged;
    this.port = port;
    this.time = time;
  }

  public PCAPdata(
    String source,
    String destination,
    String protocol,
    int size,
    int port,
    long time
) {
  this.source = source;
  this.destination = destination;
  this.protocol = protocol;
  this.size = size;
  this.port = port;
  this.time = time;
}

}
