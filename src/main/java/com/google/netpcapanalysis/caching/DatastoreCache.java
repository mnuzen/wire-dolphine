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
import com.google.netpcapanalysis.interfaces.caching.Cache;
import java.io.Serializable;
import java.lang.reflect.Type;

public class DatastoreCache<K ,V extends Serializable> implements Cache<K, V> {

  private static class DSCacheObject<T> {
    public String id;
    public T data;
    public long expiration;

    public DSCacheObject(String id, T data, long expiration) {
      this.id = id;
      this.data = data;
      this.expiration = expiration;
    }
  }

  private static final String EXPIRATION_PROP = "expiration";
  private static final String CACHED_PROP = "cached";
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final Type jsonType = new TypeToken<V>(){}.getType();
  private final Gson gson = new Gson();
  private final String objectName;
  private final int cacheDuration;
  private final int maxItems;

  public DatastoreCache(String objectName, int cacheDuration, int maxItems) {
    this.cacheDuration = cacheDuration;
    this.objectName = objectName;
    this.maxItems = maxItems;
  }

  @Override
  public void putCache(K key, V data) {
    if (Math.random() * cacheDuration  < 1) {
      // kinda hacky solution but since counting # of items in cache is expensive, we want to call
      // it as infrequently as possible, so we want to garbage collect approximately every `maxItems`
      // inserts into datastore which gets us to an average of `maxItems`*1.5 items in the datastore,
      // and preserving at minimum `maxItems`

      // this means that theoretically even if there are multiple datastore cache workers running
      // on the same model, maxItems limit will approximately be followed
      this.garbageCollect();
    }
    Entity entity = new Entity(objectName, key.toString());
    entity.setProperty(CACHED_PROP, data);
    entity.setProperty(EXPIRATION_PROP, System.currentTimeMillis() + cacheDuration);
    datastore.put(entity);
  }

  @Override
  public V get(K key) {
    DSCacheObject<V> res = getFromDatastore(key.toString());

    if (res == null || res.expiration < System.currentTimeMillis()) {
      return null;
    }

    return res.data;
  }

  public DSCacheObject<V> getFromDatastore(String key) {
    try {
      Entity entity = datastore.get(KeyFactory.createKey(objectName, key));
      String jsonEncoded = (String) entity.getProperty(CACHED_PROP);

      String id = (String) entity.getProperty("id");
      long expiration = (long) entity.getProperty(EXPIRATION_PROP);
      V data = gson.fromJson(jsonEncoded, jsonType);

      return new DSCacheObject<>(id, data, expiration);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  public void garbageCollect() {
    Entity globalStat = datastore.prepare(new Query("__Stat_Total__")).asSingleEntity();
    Long totalEntities = (Long) globalStat.getProperty("count");

    if (totalEntities != null && totalEntities > maxItems) {
      int removeNum = (int) (totalEntities - maxItems);
      Query query = new Query(objectName).addSort(EXPIRATION_PROP, SortDirection.ASCENDING).setKeysOnly();
      Iterable<Entity> pq = datastore.prepare(query).asIterable(FetchOptions.Builder.withLimit(removeNum));

      // bulk delete extras
      Transaction txn = datastore.beginTransaction();
      try {
        for (Entity entity: pq) {
          datastore.delete(entity.getKey());
        }
        txn.commit();
      } finally {
        if (txn.isActive()) {
          txn.rollback();
        }
      }
    }
  }
}
