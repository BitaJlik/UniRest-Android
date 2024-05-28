package com.unirest.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")
public class DataCacheHandler {
    private static DataCacheHandler instance;
    private static final long DEFAULT_EXPIRY_TIME = 10_000;

    private final Map<String, CacheObject> cacheMap;
    private final HashSet<String> logKeys;

    private DataCacheHandler(boolean logInfo) {
        this.cacheMap = new HashMap<>();
        if (logInfo) {
            this.logKeys = new HashSet<>();
        } else {
            this.logKeys = null;
        }
    }

    public static synchronized DataCacheHandler getInstance() {
        if (instance == null) {
            instance = new DataCacheHandler(true);
        }
        return instance;
    }

    public void put(String key, Object value) {
        put(key, value, DEFAULT_EXPIRY_TIME);
    }

    public void put(String key, Object value, long timeToLive) {
        long expiry = System.currentTimeMillis() + timeToLive;
        this.cacheMap.put(key, new CacheObject(value, expiry));
    }

    public Object get(String key) {
        CacheObject cacheObject = this.cacheMap.get(key);
        if (cacheObject != null) {
            if (System.currentTimeMillis() < cacheObject.getExpiryTime()) {
                return cacheObject.getValue();
            } else {
                this.cacheMap.remove(key);
            }
        }
        return null;
    }

    public int cleanup() {
        int totalRemoved = 0;
        Iterator<Map.Entry<String, CacheObject>> iterator = this.cacheMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CacheObject> next = iterator.next();
            if (isKeyExpired(next.getKey())) {
                iterator.remove();
                totalRemoved++;
            }
        }
        return totalRemoved;
    }

    public boolean isKeyUsed(String key) {
        if (this.logKeys != null) {
            return this.logKeys.contains(key);
        }
        return false;
    }

    public boolean isKeyInCache(String key) {
        return this.cacheMap.containsKey(key);
    }

    public boolean isKeyExpired(String key) {
        if (isKeyInCache(key)) {
            return false;
        }
        CacheObject cacheObject = this.cacheMap.get(key);
        if (cacheObject != null) {
            return cacheObject.getExpiryTime() < System.currentTimeMillis();
        }
        return false;
    }

    private static class CacheObject {
        private final Object value;
        private final long expiryTime;

        private CacheObject(Object value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public Object getValue() {
            return value;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }

}