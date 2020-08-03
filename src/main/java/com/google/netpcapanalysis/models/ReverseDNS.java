package com.google.netpcapanalysis.models;

public class ReverseDNS {
  public boolean authority;
  public boolean server;
  public String record;

  public ReverseDNS() {
    this("", false, false);
  }

  public ReverseDNS(String record, boolean server, boolean authority) {
    this.record = record;
    this.authority = authority;
    this.server = server;
  }

  public boolean isAuthority() {
    return authority;
  }

  public void setAuthority(boolean authority) {
    this.authority = authority;
  }

  public boolean isServer() {
    return server;
  }

  public void setServer(boolean server) {
    this.server = server;
  }

  public String getRecord() {
    return record;
  }

  public void setRecord(String record) {
    this.record = record;
  }
}
