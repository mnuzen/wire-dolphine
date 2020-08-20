package com.google.netpcapanalysis.interfaces.caching;

import java.io.Serializable;

public interface Cache<K, V extends Serializable> {

  /**
   * Puts item in cache
   *
   * @param key non-null
   * @param data non-null
   */
  public void put(K key, V data);

  /**
   * Gets item in cache
   *
   * @param key non-null
   * @return null if does not exist
   */
  public V get(K key);

  public void garbageCollect();

  public long getSize();

  public void clear();

  /**
   * Enables counting of hits()/misses()
   * @return on/off counting of hits/misses
   */
  public boolean statisticsEnabled();

  /**
   *
   * @return cache hits
   */
  public long hits();

  /**
   *
   * @return cache misses
   */
  public long misses();
}
