package com.google.netpcapanalysis.caching.policy;

import com.google.netpcapanalysis.caching.DSCacheObject;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;

public class MaximumItemPolicy<K, V extends Serializable> extends EvictionPolicy<K, V> {

  private final long max;

  public MaximumItemPolicy(long max) {
    this.max = max;
  }

  @Override
  public DSCacheObject<V> onGet(Cache<K, V> cache, DSCacheObject<V> data) {
    return data;
  }

  @Override
  public DSCacheObject<V> onPut(Cache<K, V> cache, DSCacheObject<V> data) {
    return data;
  }

  @Override
  public boolean checkCacheObjectEvict(Cache<K, V> cache, DSCacheObject<V> data) {
    return true;
  }

  @Override
  public boolean checkGarbageCollect(Cache<K, V> cache) {
    // kinda hacky solution but since counting # of items in cache is expensive, we want to call
    // it as infrequently as possible, so we want to garbage collect approximately every `maxItems`
    // inserts into datastore which gets us to an average of `maxItems`*1.5 items in the datastore,
    // and preserving at minimum `maxItems`

    // this means that theoretically even if there are multiple datastore cache workers running
    // on the same model, maxItems limit will approximately be followed
    return (int) (Math.random() * max) == 0;
  }

  @Override
  public boolean checkSizeConstraint(Cache<K, V> cache) {
    return true;
  }

  @Override
  public long enforceSizeConstraint(Cache<K, V> cache) {
    long reduction = cache.getSize() - max;
    return reduction < 0 ? 0 : reduction;
  }
}
