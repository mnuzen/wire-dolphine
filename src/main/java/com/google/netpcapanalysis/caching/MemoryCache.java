package com.google.netpcapanalysis.caching;

import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;

public class MemoryCache<T extends Serializable> implements Cache<T> {

  private final com.github.benmanes.caffeine.cache.Cache<String, T> cache;

  public MemoryCache(com.github.benmanes.caffeine.cache.Cache<String, T> cache) {
    this.cache = cache;
  }

  @Override
  public void putCache(String key, T data) {
    cache.put(key, data);
  }

  @Override
  public T getCache(String key) {
    return cache.getIfPresent(key);
  }
}
