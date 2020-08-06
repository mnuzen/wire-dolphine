package com.google.netpcapanalysis.models;

public final class MaliciousPacket{

  public String ip;
  public boolean flagged;

  public MaliciousPacket() {
    this(null,false);
  }

  public MaliciousPacket(String ip, boolean flagged) {
    this.ip = ip;
    this.flagged = flagged;
  }
}
