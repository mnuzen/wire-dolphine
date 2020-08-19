package caching;

import static org.junit.Assert.assertNull;

import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.DatastoreCache;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import org.junit.Test;

public class DatastoreCacheTest {

  @Test
  public void testMaxItems() {
    Cache<Integer, Integer> cache =
        new CacheBuilder<Integer, Integer>()
        .setCacheName("dstest")
        .setCacheType(CacheType.DATASTORE)
        .setPolicyArgument(100)
        .build();

    for (int i = 0; i < 1001; i++) {
      cache.put(i, i);
      cache.get(i);
    }

    DatastoreCache<Integer, Integer> mc = (DatastoreCache<Integer, Integer>) cache;
    mc.garbageCollect();
    assertNull(cache.get(0));
  }
}
