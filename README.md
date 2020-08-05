# 2020 STEP Pod Capstone Project

This repo contains our pod's 2020 STEP Capstone project.

Authors: handeland@google.com, jevingu@google.com, mnuzen@google.com 
Reviewers: arunkaly@, promanov@

## Setup: 
1. `gsutil cp gs://erik-jevin-melba-step-2020/ resources/`

## To use geolocation: 

1. Download `GeoLite2-City.mmdb` from GDrive
2. Put in repo under `resources/GeoLite2-City.mmdb`

## MaxmindDB perf: 
Averaged 14572 ms for 1000000 requests, 68623.19 rps on i7-9750H single core.

## Adding privileged information: 
1. Add to resources folder
2. To use in code, get the resource using resource loader like so:

```$java
      URL geoDBUrl = GeolocationDaoImpl.class.getClassLoader().getResource(GEO_DB_LOCATION);
```
3. Upload privileged resource to cloud bucket `erik-jevin-melba-step-2020`
4. Add resource to 

## Keystore
For privileged keys that need to be publicly accessible, use the KeystoreDao. 
