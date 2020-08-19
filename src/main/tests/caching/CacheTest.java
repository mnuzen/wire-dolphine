package caching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.CacheBuilder.Policy;
import com.google.netpcapanalysis.caching.DatastoreCache;
import com.google.netpcapanalysis.caching.MemoryCache;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import org.junit.Before;
import org.junit.Test;

public class CacheTest {

  public Cache<Integer, Integer> cache;

  @Before
  public void setup() {
    cache =
        new CacheBuilder<Integer, Integer>()
            .setCacheName("dstest")
            .setCacheType(CacheType.MEMORY)
            .setPolicy(Policy.MAXIMUM_SIZE)
            .setPolicyArgument(100)
            .build();

  }

  @Test
  public void testSize() {
    for (int i = 0; i < 100; i++) {
      cache.put(i, i);
    }

   assertEquals(100, cache.getSize());
  }

  @Test
  public void testMaxItems() {
    for (int i = 0; i < 1001; i++) {
      cache.put(i, i);
      cache.get(i);
    }

    MemoryCache<Integer, Integer> mc = (MemoryCache<Integer, Integer>) cache;
    mc.garbageCollect();
    assertNull(cache.get(0));
  }
}
