package com.google.netpcapanalysis.interfaces.dao;

public class MemoryCachedDao<T> implements CachedDao<T>{



  @Override
  public void putCache(String key, T data) {

  }

  @Override
  public T getCache(String key) {
    return null;
  }
}
