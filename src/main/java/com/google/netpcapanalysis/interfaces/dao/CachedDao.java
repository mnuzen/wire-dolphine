package com.google.netpcapanalysis.interfaces.dao;

public interface CachedDao<T> {

  public void putCache(String key, T data);
  public T getCache(String key);
}
