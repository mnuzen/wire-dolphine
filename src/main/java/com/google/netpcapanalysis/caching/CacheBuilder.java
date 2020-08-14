package com.google.netpcapanalysis.caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class CacheBuilder<K, V extends Serializable> {

  public enum CacheType {
    MEMORY, DATASTORE
  }

  private CacheType type;

  // right now used only for datastore - sets name of datastore object
  private String cacheName;

  private int expiration; // ms
  private int maxItems;

  public CacheBuilder() {
    this.type = CacheType.MEMORY;
    this.cacheName = null;
    this.expiration = 0;
    this.maxItems = 0;
  }

  public CacheBuilder(CacheType type, String cacheName, int expiration, int maxItems) {
    this.type = type;
    this.cacheName = cacheName;
    this.expiration = expiration;
    this.maxItems = maxItems;
  }

  public CacheBuilder<K, V> setType(CacheType type) {
    this.type = type;
    return this;
  }

  public CacheBuilder<K, V> setCacheName(String cacheName) {
    this.cacheName = cacheName;
    return this;
  }

  public CacheBuilder<K, V> setExpiration(int expiration) {
    this.expiration = expiration;
    return this;
  }

  public CacheBuilder<K, V> setMaxItems(int maxItems) {
    this.maxItems = maxItems;
    return this;
  }

  public Cache<K, V> build() {
    Cache<K, V> cache;
    if (type == CacheType.MEMORY) {
      cache = buildMemoryCache();
    } else if (type == CacheType.DATASTORE) {
      cache = new DatastoreCache<>(cacheName, expiration, maxItems);
    } else {
      throw new Error("No cache type specified");
    }
    return cache;
  }

  private Cache<K, V> buildMemoryCache() {
    Caffeine<Object, Object> builder = Caffeine.newBuilder();
    if (expiration > 0) {
      builder.expireAfterWrite(expiration, TimeUnit.MILLISECONDS);
    }
    if (maxItems > 0) {
      builder.maximumSize(maxItems);
    }
    return (Cache<K, V>) builder.build();
  }
}
