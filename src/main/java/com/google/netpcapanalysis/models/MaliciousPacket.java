package com.google.netpcapanalysis.models;

public final class MaliciousPacket{

  public String ip;
  public String flagged;

  public MaliciousPacket() {
    this(null,null);
  }

  public MaliciousPacket(String ip, String flagged) {
    this.ip = ip;
    this.flagged = flagged;
  }
}
