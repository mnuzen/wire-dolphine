package com.google.netpcapanalysis.models;

public class ReverseDNS {
  public boolean authority;
  public boolean server;
  public String record;

  public ReverseDNS() {
    authority = false;
    server = false;
  }

  public ReverseDNS(String record, boolean server, boolean authority) {

  }
}
