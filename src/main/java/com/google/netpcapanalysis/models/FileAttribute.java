package com.google.netpcapanalysis.models;

import java.util.Date;

public final class FileAttribute {

  public String pcapEntity;
  public String fileName;
  public String myIP;
  public String description;
  public Date uploadDate;

  public FileAttribute() {
    this(null, null, null, null, null);
  }

  public FileAttribute(String pcapEntity, String fileName, String myIP, String description) {
    this.pcapEntity = pcapEntity;
    this.fileName = fileName;
    this.myIP = myIP;
    this.description = description;
    this.uploadDate = new Date();
  }

  public FileAttribute(String pcapEntity, String fileName,String myIP,
   String description, Date uploadDate) {
    this.pcapEntity = pcapEntity;
    this.fileName = fileName;
    this.myIP = myIP;
    this.description = description;
    this.uploadDate = uploadDate;
  }

}
