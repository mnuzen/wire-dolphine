package com.google.netpcapanalysis.models;

public final class PCAPdata {

      public String source;
      public String destination;
      public String protocol;
      public int size;
      public String location;
      public String domain;
      public String flagged;
      public boolean outbound;

  public PCAPdata() {
    this(null, null, null, 0, null, null, null, false);
  }

  public PCAPdata(
      String source,
      String destination,
      String protocol,
      int size,
      String location,
      String domain,
      String flagged,
      boolean outbound
  ) {
    this.source = source;
    this.destination = destination;
    this.domain = domain;
    this.location = location;
    this.protocol = protocol;
    this.size = size;
    this.flagged = flagged;
    this.outbound = outbound;
  }

  public PCAPdata(
    String source,
    String destination,
    String protocol,
    int size
) {
  this.source = source;
  this.destination = destination;
  this.protocol = protocol;
  this.size = size;
}
}
