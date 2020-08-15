package com.google.netpcapanalysis.models;

import java.util.Date;

public final class FileAttribute {

  public String pcapEntity;
  public String fileName;
  public String myIP;
  public Date uploadDate;

  public FileAttribute() {
    this(null, null, null, null);
  }

  public FileAttribute(String pcapEntity, String fileName,String myIP) {
    this.pcapEntity = pcapEntity;
    this.fileName = fileName;
    this.myIP = myIP;
    this.uploadDate = new Date();
  }

  public FileAttribute(String pcapEntity, String fileName,String myIP, Date uploadDate) {
    this.pcapEntity = pcapEntity;
    this.fileName = fileName;
    this.myIP = myIP;
    this.uploadDate = uploadDate;
  }

}
