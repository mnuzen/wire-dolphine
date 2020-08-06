# 2020 STEP Pod Capstone Project

This repo contains our pod's 2020 STEP Capstone project.

Authors: handeland@google.com, jevingu@google.com, mnuzen@google.com 
Reviewers: arunkaly@, promanov@


## To use geolocation: 

1. Download `GeoLite2-City.mmdb` from GDrive
2. Put in repo under `resources/GeoLite2-City.mmdb`

## MaxmindDB perf: 
Averaged 14572 ms for 1000000 requests, 68623.19 rps on i7-9750H single core.

## API limitations: 
1. [Auth0 Signal API](https://auth0.com/signals/docs/)
    - 40,000 requests per day
    - 10 requests per second
    - Cache DB used to mitigate limitations


## Adding privileged information: 
1. Add to resources folder
2. To use in code, get the resource using resource loader like so:

```$java
      URL geoDBUrl = GeolocationDaoImpl.class.getClassLoader().getResource(GEO_DB_LOCATION);
```