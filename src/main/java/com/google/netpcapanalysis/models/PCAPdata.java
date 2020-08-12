package com.google.netpcapanalysis.models;

public final class PCAPdata {

  public String source;
  public String destination;
  public String domain;
  public String location;
  public String protocol;
  public int size;
  public String flagged;
  public int frequency;

  public PCAPdata() {
    this(null, null, null, null, null, 0, null, 0);
  }

  public PCAPdata(
      String source,
      String destination,
      String domain,
      String location,
      String protocol,
      int size,
      String flagged,
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

  public int getFrequency() {
    return this.frequency;
  }

  public void incrementFrequency() {
    this.frequency++;
  }

  public boolean equals(PCAPdata other) {
    if (!this.source.equals(other.source)) {
      return false;
    }
    if (!this.destination.equals(other.destination)) {
      return false;
    }
    if (!this.domain.equals(other.domain)) {
      return false;
    }
    if (!this.location.equals(other.location)) {
      return false;
    }
    if (!this.protocol.equals(other.protocol)) {
      return false;
    }
    if (this.size != other.size) {
      return false;
    }
    if (!this.flagged.equals(other.flagged)) {
      return false;
    }
    if (this.frequency != other.frequency) {
      return false;
    }
    return true;
  }

}
