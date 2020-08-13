package com.google.netpcapanalysis.interfaces.caching;

import java.io.Serializable;

public interface Cache<T extends Serializable> {

  public void putCache(String key, T data);
  public T getCache(String key);
}
