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

public abstract class CacheTest {

  public Cache<Integer, Integer> cache;

  @Before
  public abstract void setup();

  @Test
  public void testPutGet() {
    for (int i = 0; i < 100; i++) {
      cache.put(i, 2 * i);
    }

    for (int i = 0; i < 100; i++) {
      assertEquals(new Integer(2 * i), cache.get(i));
    }
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
