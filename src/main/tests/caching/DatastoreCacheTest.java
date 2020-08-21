package caching;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.CacheBuilder.Policy;
import org.junit.After;
import org.junit.Before;

public class DatastoreCacheTest extends CacheTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  @Override
  public void setup() {
    helper.setUp();
    initCache();
  }

  @Override
  public void initCache() {
    cache =
        new CacheBuilder<Integer, CacheTestingClass>()
            .setCacheName("dstest")
            .setCacheType(CacheType.DATASTORE)
            .setKVClass(Integer.class, CacheTestingClass.class)
            .setPolicy(Policy.MAXIMUM_SIZE)
            .setPolicyArgument(100)
            .enableStatistics(true)
            .build();
  }

  @After
  @Override
  public void tearDown() {
    super.tearDown();
    helper.tearDown();
  }

}
