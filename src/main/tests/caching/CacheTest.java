package caching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.netpcapanalysis.caching.MemoryCache;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;
import java.util.Objects;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class CacheTest {

  public static class CacheTestingClass implements Serializable {

    int x;
    int y;
    long z;
    double a;
    float b;
    String name;

    public CacheTestingClass(int i) {
      this(i, i, i, i, i, "" + i);
    }

    public CacheTestingClass(int x, int y, long z, double a, float b, String name) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.a = a;
      this.b = b;
      this.name = name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      CacheTestingClass that = (CacheTestingClass) o;
      return x == that.x &&
          y == that.y &&
          z == that.z &&
          Double.compare(that.a, a) == 0 &&
          Float.compare(that.b, b) == 0 &&
          Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, z, a, b, name);
    }
  }

  public Cache<Integer, CacheTestingClass> cache;

  @Before
  public void setup() {
    initCache();
  }

  @After
  public void tearDown() {
    cache.clear();
  }

  public abstract void initCache();

  @Test
  public void testPutGet() {
    for (int i = 0; i < 100; i++) {
      cache.put(i, new CacheTestingClass(2 * i));
    }

    for (int i = 0; i < 100; i++) {
      assertEquals(new CacheTestingClass(2 * i), cache.get(i));
    }
  }

  @Test
  public void testSize() {
    for (int i = 0; i < 100; i++) {
      cache.put(i, new CacheTestingClass(i));
    }

    assertEquals(100, cache.getSize());
  }

  @Test
  public void testClear() {
    for (int i = 0; i < 100; i++) {
      cache.put(i, new CacheTestingClass(i));
    }

    cache.clear();
    assertEquals(0, cache.getSize());
  }

  @Test
  public void testMaxItems() {
    for (int i = 0; i < 101; i++) {
      cache.put(i, new CacheTestingClass(i));
      cache.get(i);
    }

    cache.garbageCollect();
    assertNull(cache.get(0));
  }

  @Test
  public void testHits() {
    for (int i = 0; i < 100; i++) {
      cache.put(i, new CacheTestingClass(i));
      cache.get(i);
    }

    assertEquals(100, cache.hits());
  }

  @Test
  public void testMisses() {
    for (int i = 0; i < 100; i++) {
      cache.get(i);
    }

    assertEquals(100, cache.misses());
  }


  @Test
  public void testHitMiss() {
    for (int i = 0; i < 100; i++) {
      cache.get(i);
    }
    for (int i = 0; i < 100; i++) {
      cache.put(i, new CacheTestingClass(2 * i));
    }
    cache.garbageCollect();
    for (int i = 50; i < 150; i++) {
      cache.get(i);
    }

    assertEquals(150, cache.misses());
    assertEquals(50, cache.hits());
  }
}
