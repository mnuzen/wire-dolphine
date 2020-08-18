# 2020 STEP Pod Capstone Project

This repo contains our pod's 2020 STEP Capstone project.

Authors: handeland@google.com, jevingu@google.com, mnuzen@google.com 
Reviewers: arunkaly@, promanov@

## Setup: 
1. `gsutil cp -r gs://erik-jevin-melba-step-2020 resources/`

## To add sample PCAP files: 

1. Download `file1.pcap` and `file2.pcap` from GDrive's PCAP Files folder.
2. Put in repo under `resources/files/file1.pcap` and `resources/files/file2.pcap`, respectively. 

## To add IP information for PCAP parser testing:

1. Download `file2.txt` from GDrive's PCAP Files folder.
2. Put in repo under `resources/files/file2.txt`.

## To use geolocation: 

1. Download `GeoLite2-City.mmdb` from GDrive.
2. Put in repo under `resources/GeoLite2-City.mmdb`.

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
3. Upload privileged resource to cloud bucket `erik-jevin-melba-step-2020`
4. Add resource to 

## Keystore
For privileged keys that need to be publicly accessible, use the KeystoreDao. 

## Datastore
The Entity Properties for the two data objects stored:

1. File Attributes

    | Key | ID | File_Name | PCAP_Entity | My_IP | Upload_Date |
    |-----|----|-----------|-------------|-------|------------|
    |     |    | String    | String      | String| Long       |

2. PCAP File

    | Key | ID | Sources | Desination | Protocol | Size |
    |-----|----|---------|------------|----------|------|
    |     |    | String  | String     | String   | Long |


