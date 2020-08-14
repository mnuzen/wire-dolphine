package com.google.netpcapanalysis.interfaces.caching;

import java.io.Serializable;

public interface Cache<K, V extends Serializable> {

  public void putCache(K key, V data);
  public V get(K key);
}
