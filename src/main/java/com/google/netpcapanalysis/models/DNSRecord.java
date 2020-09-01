package com.google.netpcapanalysis.models;

import java.io.Serializable;

public class DNSRecord implements Serializable {
  public boolean authority;
  public boolean server;
  public String domain;

  public DNSRecord() {
    this("", false, false);
  }

  public DNSRecord(String domain, boolean server, boolean authority) {
    this.domain = domain;
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

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }
}
