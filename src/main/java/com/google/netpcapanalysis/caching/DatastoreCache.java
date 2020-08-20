package com.google.netpcapanalysis.caching;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.netpcapanalysis.caching.policy.EvictionPolicy;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;
import java.lang.reflect.Type;

public class DatastoreCache<K, V extends Serializable> implements Cache<K, V> {

  private static final String EXPIRATION_PROP = "expiration";
  private static final String CACHED_PROP = "cached";

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final Type jsonType;
  private final Gson gson;
  private final String objectName;

  private final EvictionPolicy<K, V> policy;

  private boolean statistics;
  private long hits;
  private long misses;

  /**
   * @param objectName entity name
   * @param policy     eviction policy
   * @param statistics boolean to enable statistics recording
   */
  public DatastoreCache(String objectName, EvictionPolicy<K, V> policy, boolean statistics) {
    this.objectName = objectName;
    this.policy = policy;
    this.statistics = statistics;
    this.jsonType = new TypeToken<V>() {}.getType();
    this.gson = new Gson();
  }

  @Override
  public void put(K key, V value) {
    if (policy.checkGarbageCollect(this)) {
      garbageCollect();
    }

    DSCacheObject<V> data = new DSCacheObject<>(key.toString(), value, System.currentTimeMillis());
    data = policy.onPut(this, data);

    putDSObject(data);
  }

  @Override
  public V get(K key) {
    if (key == null) {
      throw new IllegalArgumentException("null value provided");
    }
    DSCacheObject<V> res = getDSObject(key);
    res = policy.onGet(this, res);

    if (res == null) {
      if (statistics) {
        misses++;
      }
      return null;
    }

    hits++;
    return res.value;
  }

  @Override
  public long getSize() {
    Entity globalStat = datastore.prepare(new Query("__Stat_Total__")).asSingleEntity();
    return (Long) globalStat.getProperty("count");
  }

  /**
   * Automatically run roughly every `maxItem` inserts. Or can be manually called at the end of a
   * series of operations.
   */
  @Override
  public void garbageCollect() {
    Query query = new Query(objectName).addSort(EXPIRATION_PROP, SortDirection.ASCENDING)
        .setKeysOnly();
    Iterable<Entity> pq;

    if (policy.checkSizeConstraint(this)) {
      int limit = (int) policy.enforceSizeConstraint(this);
      pq = datastore.prepare(query)
          .asIterable(FetchOptions.Builder.withLimit(limit));
    } else {
      pq = datastore.prepare(query).asIterable();
    }

    // bulk delete extras
    Transaction txn = datastore.beginTransaction();
    try {
      for (Entity entity : pq) {
        if (policy.checkCacheObjectEvict(this, entityToCacheObject(entity))) {
          datastore.delete(entity.getKey());
        }
      }
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  @Override
  public boolean statisticsEnabled() {
    return statistics;
  }

  @Override
  public long hits() {
    return hits;
  }

  @Override
  public long misses() {
    return misses;
  }

  public void putDSObject(DSCacheObject<V> data) {
    if (data.key == null || data.value == null) {
      throw new IllegalArgumentException("null value provided");
    }

    Entity entity = cacheObjectToEntity(data);
    datastore.put(entity);
  }

  public DSCacheObject<V> getDSObject(K key) {
    try {
      Entity entity = datastore.get(KeyFactory.createKey(objectName, key.toString()));
      return entityToCacheObject(entity);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  public void enableStatistics(boolean b) {
    this.statistics = b;
  }

  private Entity cacheObjectToEntity(DSCacheObject<V> data) {
    Entity entity = new Entity(objectName, data.key);
    entity.setProperty(CACHED_PROP, gson.toJson(data.value));
    entity.setProperty(EXPIRATION_PROP, data.expiration);

    return entity;
  }

  private DSCacheObject<V> entityToCacheObject(Entity entity) {
    String jsonEncoded = (String) entity.getProperty(CACHED_PROP);

    String id = (String) entity.getProperty("id");
    long expiration = (long) entity.getProperty(EXPIRATION_PROP);
    V data = gson.fromJson(jsonEncoded, jsonType);

    return new DSCacheObject<>(id, data, expiration);
  }
}
