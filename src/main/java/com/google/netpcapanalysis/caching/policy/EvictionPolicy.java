package com.google.netpcapanalysis.caching.policy;

import com.google.netpcapanalysis.caching.DSCacheObject;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;

public abstract class EvictionPolicy<K, V extends Serializable> {

  /**
   * Hooks into get before returning
   *
   * @param cache
   * @param data
   * @return
   */
  public abstract DSCacheObject<V> onGet(Cache<K, V> cache, DSCacheObject<V> data);

  /**
   * Hooks into put before insertion
   * @param cache
   * @param data
   * @return
   */
  public abstract DSCacheObject<V> onPut(Cache<K, V> cache, DSCacheObject<V> data);

  /**
   * Check whether to evict object DURING garbage collect
   * @param cache
   * @param data
   * @return
   */
  public abstract boolean checkCacheObjectEvict(Cache<K, V> cache, DSCacheObject<V> data);

  /**
   * Check whether to garbage collect
   *
   * @param cache
   * @return
   */
  public abstract boolean checkGarbageCollect(Cache<K, V> cache);

  /**
   * Check whether a size constraint exists
   * @param cache
   * @return
   */
  public abstract boolean checkSizeConstraint(Cache<K, V> cache);

  /**
   * Reduce cache by size of return value
   * @param cache
   * @return
   */
  public abstract long enforceSizeConstraint(Cache<K, V> cache);
}
