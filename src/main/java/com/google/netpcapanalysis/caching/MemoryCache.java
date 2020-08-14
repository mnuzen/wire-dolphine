package com.google.netpcapanalysis.caching;

import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;

public class MemoryCache<K, V extends Serializable> implements Cache<K, V> {

  private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

  public MemoryCache(com.github.benmanes.caffeine.cache.Cache<K, V> cache) {
    this.cache = cache;
  }

  @Override
  public void putCache(K key, V data) {
    cache.put(key, data);
  }

  @Override
  public V get(K key) {
    return cache.getIfPresent(key);
  }
}
