package com.google.netpcapanalysis.lookup_ip;

import com.google.netpcapanalysis.dao.MaliciousIPDaoImpl;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.netpcapanalysis.mockdata.MockDataLoader;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;


public class BenchmarkMaliciousIP {
  private String CSV_FILE = "maliciousIPTest.csv"; //first 1-30 valid IP's, 31-60 malicious IP's
  ArrayList<PCAPdata> ipList;
    long avgTime;
    long totalTime;
    int flaggedTrue;
    int faflaggedFalse;

  public BenchmarkMaliciousIP(){
        MockDataLoader mockData = new MockDataLoader();
        ipList = mockData.CSVDataLoader(CSV_FILE);
        flaggedTrue=0;
        faflaggedFalse=0;
        avgTime = 0L;
        totalTime = 0L;
    }

   public void test(){
        Long start, end;
        MaliciousIPDao ipTest = new MaliciousIPDaoImpl();

        for(PCAPdata packet : ipList)
        {
            start = System.currentTimeMillis();
            packet = ipTest.isMalicious(packet); 
            end = System.currentTimeMillis();
            avgTime+=(end-start);

            if(packet.flagged == true)
            {
              flaggedTrue++;
            }
            else{
              faflaggedFalse++;
            }
        }
        totalTime = avgTime;
        avgTime = avgTime/ipList.size();
    }

    public  void results()
    { 
      System.out.println("Average lookup time: " + avgTime + "\n");
      System.out.println("Total lookup time: " + totalTime + "\n");
      System.out.println("Flagged True:"+ flaggedTrue + "  False:" + faflaggedFalse + "\n");
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