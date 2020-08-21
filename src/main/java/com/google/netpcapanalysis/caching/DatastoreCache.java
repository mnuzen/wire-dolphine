package com.google.netpcapanalysis.caching;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.netpcapanalysis.caching.policy.EvictionPolicy;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class DatastoreCache<K, V extends Serializable> implements Cache<K, V> {

  private static final String KEY_PROP = "key";
  private static final String EXPIRATION_PROP = "expiration";
  private static final String CACHED_PROP = "cached";

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final Gson gson;
  private final String objectName;
  private Class<K> keyClass;
  private Class<V> valClass;

  private final EvictionPolicy<K, V> policy;

  private boolean statistics;
  private long hits;
  private long misses;

  /**
   * @param objectName entity name
   * @param policy     eviction policy
   * @param statistics boolean to enable statistics recording
   */
  public DatastoreCache(String objectName, EvictionPolicy<K, V> policy, Class<K> keyClass,
      Class<V> valClass, boolean statistics) {
    this.objectName = objectName;
    this.policy = policy;
    this.statistics = statistics;

    this.gson = new Gson();
    this.keyClass = keyClass;
    this.valClass = valClass;
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
    Query query = new Query(objectName).setKeysOnly();
    return (long) datastore.prepare(query).countEntities();
  }

  /**
   * Automatically run roughly every `maxItem` inserts. Or can be manually called at the end of a
   * series of operations.
   */
  @Override
  public void garbageCollect() {
    Query query = new Query(objectName).addSort(EXPIRATION_PROP, SortDirection.ASCENDING);
    Iterable<Entity> pq;

    if (policy.checkSizeConstraint(this)) {
      int limit = (int) policy.enforceSizeConstraint(this);
      pq = datastore.prepare(query)
          .asIterable(FetchOptions.Builder.withLimit(limit));
    } else {
      pq = datastore.prepare(query).asIterable();
    }

    for (Entity entity : pq) {
      if (policy.checkCacheObjectEvict(this, entityToCacheObject(entity))) {
        datastore.delete(entity.getKey());
      }
    }
  }

  @Override
  public void clear() {
    Query query = new Query(objectName);
    Iterable<Entity> pq = datastore.prepare(query).asIterable();

    for (Entity entity : pq) {
      datastore.delete(entity.getKey());
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
      Query query = new Query(objectName).addSort(EXPIRATION_PROP, SortDirection.DESCENDING);
      Filter keyFilter = new FilterPredicate(KEY_PROP, FilterOperator.EQUAL, key.toString());
      query.setFilter(keyFilter);
      List<Entity> res = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));

      return entityToCacheObject(res.get(0));
    } catch (Exception e) {
      return null;
    }
  }

  public void enableStatistics(boolean b) {
    this.statistics = b;
  }

  private Entity cacheObjectToEntity(DSCacheObject<V> data) {
    Entity entity = new Entity(objectName);
    entity.setProperty(KEY_PROP, data.key);
    entity.setProperty(CACHED_PROP, gson.toJson(data.value));
    entity.setProperty(EXPIRATION_PROP, data.expiration);

    return entity;
  }

  private DSCacheObject<V> entityToCacheObject(Entity entity) {
    try {
      String jsonEncoded = (String) entity.getProperty(CACHED_PROP);

      String id = (String) entity.getProperty(KEY_PROP);
      long expiration = (long) entity.getProperty(EXPIRATION_PROP);
      V data = gson.fromJson(jsonEncoded, valClass);

      return new DSCacheObject<>(id, data, expiration);
    } catch (NullPointerException e) {
      return null;
    }
  }
}
