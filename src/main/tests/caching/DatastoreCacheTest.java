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
    cache =
        new CacheBuilder<Integer, Integer>()
            .setCacheName("dstest")
            .setCacheType(CacheType.DATASTORE)
            .setPolicy(Policy.MAXIMUM_SIZE)
            .setPolicyArgument(100)
            .build();

  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

}
