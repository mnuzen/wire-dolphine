package com.google.sps.lookup_ip;

import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.sps.mockdata.MockDataLoader;
import com.google.sps.datastore.PCAPdata;

public class TestMaliciousIP {
  private String CSV_FILE = "maliciousIPTest.csv"; //first 1-30 valid IP's, 31-60 malicious IP's
  ArrayList<PCAPdata> IPlist;
    long AvgTime;
    long TotalTime;
    int flagged_true;
    int flagged_false;

  public TestMaliciousIP(){
        MockDataLoader mockData = new MockDataLoader();
        IPlist = mockData.CSVDataLoader(CSV_FILE);
        flagged_true=0;
        flagged_false=0;
        AvgTime = 0L;
        TotalTime = 0L;
    }

   public void test(){
        Long start, end;
        MaliciousIPDao ipTest = new MaliciousIPDao();

        for(PCAPdata packet : IPlist)
        {
            start = System.currentTimeMillis();
            packet = ipTest.isMalicious(packet); 
            end = System.currentTimeMillis();
            AvgTime+=(end-start);

            if(packet.flagged == true)
            {
              flagged_true++;
            }
            else{
              flagged_false++;
            }
        }
        TotalTime = AvgTime;
        AvgTime = AvgTime/IPlist.size();
    }

    public  void results()
    { 
      System.out.println("Average lookup time: " + AvgTime + "\n");
      System.out.println("Total lookup time: " + TotalTime + "\n");
      System.out.println("Flagged True:"+ flagged_true + "  False:" + flagged_false + "\n");
    }
}

/* Results time in milliseconds  about 10 requests a second
[INFO] GCLOUD: Average lookup time: 105 
[INFO] GCLOUD:
[INFO] GCLOUD: Total lookup time: 6309
[INFO] GCLOUD:
[INFO] GCLOUD: Flagged True:30  False:30

[INFO] GCLOUD: Average lookup time: 100
[INFO] GCLOUD:
[INFO] GCLOUD: Total lookup time: 6018
[INFO] GCLOUD:
[INFO] GCLOUD: Flagged True:30  False:30

[INFO] GCLOUD: Average lookup time: 95
[INFO] GCLOUD:
[INFO] GCLOUD: Total lookup time: 5744
[INFO] GCLOUD:
[INFO] GCLOUD: Flagged True:30  False:30
*/