package com.google.netpcapanalysis.caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class CacheBuilder<T extends Serializable> {

  public enum CacheType {
    MEMORY, DATASTORE
  }

  private CacheType type;
  private String cacheName;
  private int expiration;
  private int maxItems;

  public CacheBuilder(CacheType type, String cacheName, int expiration, int maxItems) {
    this.type = type;
    this.cacheName = cacheName;
    this.expiration = expiration;
    this.maxItems = maxItems;
  }

  public CacheBuilder<T> setType(CacheType type) {
    this.type = type;
    return this;
  }

  public CacheBuilder<T> setCacheName(String cacheName) {
    this.cacheName = cacheName;
    return this;
  }

  public CacheBuilder<T> setExpiration(int expiration) {
    this.expiration = expiration;
    return this;
  }

  public CacheBuilder<T> setMaxItems(int maxItems) {
    this.maxItems = maxItems;
    return this;
  }

  public Cache<T> build() {
    Cache<T> cache;
    if (type == CacheType.MEMORY) {
      cache = new MemoryCache<>(Caffeine.newBuilder()
          .expireAfterWrite(expiration, TimeUnit.MILLISECONDS)
          .maximumSize(maxItems)
          .build());
    } else if (type == CacheType.DATASTORE) {
      cache = new DatastoreCache<>(cacheName, expiration, maxItems);
    } else {
      throw new Error("No cache type specified");
    }
    return cache;
  }
}
