package com.google.netpcapanalysis.intergration_tests;

import com.google.netpcapanalysis.dao.MaliciousIPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;

import com.google.netpcapanalysis.models.Flagged;

public final class MaliciousTest{
  private static final String tab = "    "; // add to each test
  private static final String tabb = "        ";// add to each test statement
  /*
  [INFO] GCLOUD: Running Malicious Tests:
  [INFO] GCLOUD:     Testing cache upload:
  [INFO] GCLOUD:         Object retrieved from cache
  [INFO] GCLOUD:         Test: Passed
  */
  private int testsPassed = 0;

  private MaliciousIPDao cacheDB = new MaliciousIPDaoImpl();
  private PCAPDao ipCache = new PCAPDaoImpl();

  public MaliciousTest() {
  }

  public Boolean testCacheUpload(PCAPdata data)
  {   
    data = cacheDB.isMalicious(data);//Places PCAPdata into the datastore cache if not already there

    String searchDB = ipCache.searchMaliciousDB(data.destination); //looks in datastore cache for data

    if(searchDB == Flagged.UNKNOWN){
      return false;
    }
    else{
      System.out.println(tabb + "Object retrieved from cache");
      return true;
    }
  }

  public Boolean testAPILookup(PCAPdata data, String flag)
  {   
    data = cacheDB.isMalicious(data);
    String searchDB = ipCache.searchMaliciousDB(data.destination);
    System.out.println(tabb + "Expected IP Flagged:" + flag);
    System.out.println(tabb + "Lookup IP Flagged:" + searchDB);
    if(searchDB == flag){
      return true;
    }
    else{
      return false;
    }
  }

  //Tests if PCAPdata data is uploaded into cache and whether API returns expected results for String flag
  public int run(PCAPdata data, String flag){
    System.out.println("Running Malicious Tests:");

    ////////Test cache upload\\\\\\\\
    System.out.println(tab + "Testing cache upload:");
    if(testCacheUpload(data) == true)
    {
        System.out.println(tabb + "Test: Passed");
        testsPassed++;
    }
    else{
      System.out.println(tabb + "Test: Failed");
    }

    ////////Test API Lookup\\\\\\\\
      System.out.println(tab + "Testing API Lookup:");
    if(testAPILookup(data, flag) == true)
    {
        System.out.println(tabb + "Test: Passed");
        testsPassed++;
    }
    else{
      System.out.println(tabb + "Test: Failed");
    }

    return testsPassed;
  }
}
