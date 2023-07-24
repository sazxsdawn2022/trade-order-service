package com.ksyun.trade.redisutils;

import java.util.HashMap;
import java.util.Map;

public class MemoryCache<K, V> implements Cache<K, V> {

    private Map<K, V> cacheMap = new HashMap<>();
    private int maxSize;
    private long maxAge;

    public MemoryCache(int maxSize, long maxAge) {
        this.maxSize = maxSize;
        this.maxAge = maxAge;
    }

    @Override
    public V get(K key) {
        CacheEntry<V> entry = (CacheEntry<V>) cacheMap.get(key);
//        System.out.println("entry = " + entry);
        if (entry != null && !entry.isExpired()) {
            return entry.getValue();
        } else {
            cacheMap.remove(key);
            return null;
        }
    }

    @Override
    public void put(K key, V value) {
        if (cacheMap.size() >= maxSize) {
            removeExpiredEntries();
            if (cacheMap.size() >= maxSize) {
                cacheMap.remove(cacheMap.keySet().iterator().next());
            }
        }
        cacheMap.put(key, (V) new CacheEntry<V>(value));
    }

    private void removeExpiredEntries() {
        for (Map.Entry<K, V> entry : cacheMap.entrySet()) {
            CacheEntry<V> cacheEntry = (CacheEntry<V>) entry.getValue();
            if (cacheEntry.isExpired()) {
                cacheMap.remove(entry.getKey());
            }
        }
    }

    private class CacheEntry<T> {
        private T value;
        private long expirationTime;

        public CacheEntry(T value) {
//            System.out.println("value = " + value);
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + maxAge; //毫秒
        }

        public T getValue() {
            return value;
        }

        public boolean isExpired() {
//            System.out.println("(判断过期) 当前时间" + System.currentTimeMillis() + "  过期时间" + expirationTime);
            return System.currentTimeMillis() > expirationTime;
        }

        @Override
        public String toString() {
            return "CacheEntry{" +
                    "value=" + value +
                    ", expirationTime=" + expirationTime +
                    '}';
        }
    }
}
