# 2020 STEP Pod Capstone Project

This repo contains our pod's 2020 STEP Capstone project.

Authors: handeland@google.com, jevingu@google.com, mnuzen@google.com 
Reviewers: arunkaly@, promanov@


## To add sample PCAP files: 

1. Download `file1.pcap` and `file1.pcap` from GDrive.
2. Put in repo under `resources/files/file1.pcap` and `resources/files/file2.pcap`, respectively.

## To use geolocation: 

1. Download `GeoLite2-City.mmdb` from GDrive.
2. Put in repo under `resources/GeoLite2-City.mmdb`.

## MaxmindDB perf: 
Averaged 14572 ms for 1000000 requests, 68623.19 rps on i7-9750H single core.

## Adding privileged information: 
1. Add to resources folder
2. To use in code, get the resource using resource loader like so:

```$java
      URL geoDBUrl = GeolocationDaoImpl.class.getClassLoader().getResource(GEO_DB_LOCATION);
```