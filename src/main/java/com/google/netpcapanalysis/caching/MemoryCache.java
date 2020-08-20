package com.google.netpcapanalysis.caching;

import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;

public class MemoryCache<K, V extends Serializable> implements Cache<K, V> {

  private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;
  private boolean stats;

  public MemoryCache(com.github.benmanes.caffeine.cache.Cache<K, V> cache, boolean stats) {
    this.cache = cache;
    this.stats = stats;
  }

  @Override
  public void put(K key, V data) {
    if (key == null || data == null) throw new IllegalArgumentException("null value provided");
    cache.put(key, data);
  }

  @Override
  public V get(K key) {
    if (key == null) throw new IllegalArgumentException("null value provided");
    return cache.getIfPresent(key);
  }

  @Override
  public void garbageCollect() {
    cache.cleanUp();
  }

  @Override
  public long getSize() {
    return cache.estimatedSize();
  }

  @Override
  public void clear() {
    cache.invalidateAll();
  }

  @Override
  public boolean statisticsEnabled() {
    return stats;
  }

  @Override
  public long hits() {
    return cache.stats().hitCount();
  }

  @Override
  public long misses() {
    return cache.stats().missCount();
  }
}
