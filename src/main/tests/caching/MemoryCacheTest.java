package caching;

import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.CacheBuilder.Policy;
import org.junit.Before;

public class MemoryCacheTest extends CacheTest {

  @Override
  public void initCache() {
    cache =
        new CacheBuilder<Integer, CacheTestingClass>()
            .setCacheName("dstest")
            .setCacheType(CacheType.MEMORY)
            .setPolicy(Policy.MAXIMUM_SIZE)
            .setPolicyArgument(100)
            .build();
  }
}
