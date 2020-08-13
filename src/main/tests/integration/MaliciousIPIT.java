package intergration;

import com.google.netpcapanalysis.dao.MaliciousIPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.models.Flagged;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class MaliciousIPIT {


    private static PCAPdata data;
    private static String flag;

    private static MaliciousIPDao MaliciousIPDao;
    private static PCAPDao ipCache;

    private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


    @Before
    public void setup() {
        helper.setUp();
        MaliciousIPDao = new MaliciousIPDaoImpl();
        ipCache = new PCAPDaoImpl();
        flag = Flagged.TRUE;
        data = new PCAPdata("myip","210.48.204.118","blank",4,"blank","blank","blank",true);
    }

      @After
    public void tearDown() {
        helper.tearDown();
    }

     @Test
    public void cacheUpload() {
        Assert.assertEquals(testCacheUpload(data),true);
    }

     @Test
    public void apiLookup() {
        Assert.assertEquals(testAPILookup(data, flag),true);
    }

 private Boolean testCacheUpload(PCAPdata data)
  {   
    data = MaliciousIPDao.isMalicious(data);//Places PCAPdata into the datastore cache if not already there

    String searchDB = ipCache.searchMaliciousDB(data.destination); //looks in datastore cache for data

    if(searchDB == Flagged.UNKNOWN){
      return false;
    }
    else{
      System.out.println("Object retrieved from cache");
      return true;
    }
  }

  private Boolean testAPILookup(PCAPdata data, String flag)
  {   
    data = MaliciousIPDao.isMalicious(data);
    String searchDB = ipCache.searchMaliciousDB(data.destination);
    System.out.println("Expected IP Flagged:" + flag);
    System.out.println("Lookup IP Flagged:" + searchDB);
    if(searchDB == flag){
      return true;
    }
    else{
      return false;
    }
  }
  

}