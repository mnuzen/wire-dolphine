package com.google.netpcapanalysis.caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.netpcapanalysis.caching.policy.EvictionPolicy;
import com.google.netpcapanalysis.caching.policy.MaximumItemPolicy;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class CacheBuilder<K, V extends Serializable> {

  public enum CacheType {
    MEMORY, DATASTORE
  }

  public enum Policy {
    TIME_AFTER_WRITE,
    TIME_AFTER_ACCESS,
    MAXIMUM_SIZE
  }

  private CacheType type;
  private Policy policy;
  private int evictionPolicyArg;

  // right now used only for datastore - sets name of datastore object
  private String cacheName;

  // datastore only, used to cast gson
  private Class<K> keyClass;
  private Class<V> valClass;

  private boolean enableStatistics;

  public CacheBuilder() {}

  public CacheBuilder(String cacheName, CacheType type, Policy policy, int evictionPolicyArg, boolean enableStatistics) {
    this.type = type;
    this.cacheName = cacheName;
    this.policy = policy;
    this.evictionPolicyArg = evictionPolicyArg;
    this.enableStatistics = enableStatistics;
  }

  public CacheBuilder<K, V> setCacheName(String cacheName) {
    this.cacheName = cacheName;
    return this;
  }

  public CacheBuilder<K, V> setCacheType(CacheType type) {
    this.type = type;
    return this;
  }

  public CacheBuilder<K, V> setPolicy(Policy policy) {
    this.policy = policy;
    return this;
  }

  public CacheBuilder<K, V> setPolicyArgument(int arg) {
    this.evictionPolicyArg = arg;
    return this;
  }

  public CacheBuilder<K, V> setKVClass(Class<K> k, Class<V> v) {
    this.keyClass = k;
    this.valClass = v;
    return this;
  }

  public CacheBuilder<K, V> enableStatistics(boolean b) {
    this.enableStatistics = b;
    return this;
  }

  public Cache<K, V> build() {
    Cache<K, V> cache;
    if (type == CacheType.MEMORY) {
      cache = buildMemoryCache();
    } else if (type == CacheType.DATASTORE) {
      cache = buildDatastoreCache();
    } else {
      throw new Error("No cache type specified");
    }
    return cache;
  }

  private MemoryCache<K, V> buildMemoryCache() {
    Caffeine<Object, Object> builder = Caffeine.newBuilder();
    if (policy == Policy.MAXIMUM_SIZE) {
      builder.maximumSize(evictionPolicyArg);
    } else if (policy == Policy.TIME_AFTER_ACCESS) {
      builder.expireAfterAccess(evictionPolicyArg, TimeUnit.MILLISECONDS);
    } else if (policy == Policy.TIME_AFTER_WRITE) {
      builder.expireAfterWrite(evictionPolicyArg, TimeUnit.MILLISECONDS);
    } else {
      throw new Error("cache has no eviction policy");
    }

    if (enableStatistics) {
      builder.recordStats();
    }
    return new MemoryCache<>(builder.build(), enableStatistics);
  }

  private DatastoreCache<K, V> buildDatastoreCache() {
    if (keyClass == null || valClass == null) throw new NullPointerException("kv classes needed for datastore cache");

    return new DatastoreCache<>(
        cacheName,
        getPolicy(),
        keyClass,
        valClass,
        enableStatistics
    );
  }

  private EvictionPolicy<K, V> getPolicy() {
    switch (policy) {
      case MAXIMUM_SIZE:
        return new MaximumItemPolicy<>(evictionPolicyArg);
      case TIME_AFTER_ACCESS:
      case TIME_AFTER_WRITE:
      default:
        throw new Error("Unsupported Policy");
    }
  }
}
