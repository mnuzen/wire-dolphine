package com.google.netpcapanalysis.caching;

public class DSCacheObject<T> {
  public String key;
  public T value;
  public long expiration;

  public DSCacheObject(String key, T value, long expiration) {
    this.key = key;
    this.value = value;
    this.expiration = expiration;
  }
}
