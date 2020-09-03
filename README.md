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

On random dataset:
- Averaged 14572 ms for 1000000 requests, 68623.19 rps on i7-9750H single core.
- Cached averaged 17256 ms for 1000000 requests, 57951.98 rps on i7-9750H single core

On uneven dataset (10K uniques) : 
- Averaged 14262 ms for 1000000 requests, 70114.75 rps, 68623.19 rps on i7-9750H single core.
- Cached 100% unique: averaged 13403 ms for 1000000 requests, 74612.02 rps on i7-9750H single core.

Uneven dataset (1k uniques): 
- Cached averaged 12312 ms for 1000000 requests, 81221.57 rps

Uneven dataset (100 uniques):
- Cached averaged 447 ms for 1000000 requests, 2238805.97 rps

On GCP Shell: 
- averaged 121 ms for 1000 requests, 8241.76 rps
- averaged cached 222 ms w/ datashell for 1000 requests, 4504.50 rps

## ReverseDNS

on 150mbps internet / i7-9750h single thread
- averaged cached 135 ms for 1000 requests, 7425.74 rps

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
Datastore retrieval Times:
| Packets | Time in s |
|---------|-----------|
| 15,000  | ~0.5      |
| 30,000  | ~0.9      |
| 60,000  | ~1.8      |
| 120,000 | ~4.2      |

The Entity Properties for the two data objects stored:

1. File Attributes

    | Key | ID | File_Name | PCAP_Entity | My_IP | Upload_Date |
    |-----|----|-----------|-------------|-------|------------|
    |     |    | String    | String      | String| Date       |

2. PCAP File

    | Key | ID | Sources | Desination | Protocol | Size |
    |-----|----|---------|------------|----------|------|
    |     |    | String  | String     | String   | Long |

## Demo
![4x-compressed](https://user-images.githubusercontent.com/16601367/92042550-eb461400-ed26-11ea-8b7a-c6741a70ad11.gif)
[View on Youtube](https://youtu.be/0yPIX50UWB8)

