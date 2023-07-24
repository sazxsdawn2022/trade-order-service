package com.ksyun.trade.redisutils;

public class TwoLevelCache<K, V> implements Cache<K, V> {

    private Cache<K, V> firstLevelCache;
    private Cache<K, V> secondLevelCache;

    public TwoLevelCache(Cache<K, V> firstLevelCache, Cache<K, V> secondLevelCache) {
        this.firstLevelCache = firstLevelCache;
        this.secondLevelCache = secondLevelCache;
    }

    @Override
    public V get(K key) {
        V value = firstLevelCache.get(key); // 先尝试从一级缓存获取值
//        System.out.println("从一级缓存中获取值：" + value);
        if (value == null) {
            value = secondLevelCache.get(key); // 再二级缓存
//            System.out.println("从二级缓存中获取值：" + value);
            if (value != null) {
                firstLevelCache.put(key, value); // 然后再设置到以及缓存中
//                System.out.println("把从二级缓存中获取的值设置到一级缓存中");
            }
        }
//        System.out.println("two中get方法返回value：" + value);
        return value;
    }

    @Override
    public void put(K key, V value) {
        firstLevelCache.put(key, value); // 存到一级缓存
        secondLevelCache.put(key, value); // 存到二级缓存，RedisUtils中的set设置了过期时间为60秒
    }
}
