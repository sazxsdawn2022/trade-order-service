package com.ksyun.trade.redisutils;


public class RedisCache<K, V> implements Cache<K, V> {
//    @Resource
//    private RedisUtils redisUtils;

//    private static RedisCache redisCache;

//    @PostConstruct
//    public void init() {
//        redisCache = this;
//    }

//    private static RedisUtils redisUtils;
//
//    public static void setApplicationContext(ApplicationContext applicationContext) {
//        redisUtils = applicationContext.getBean(RedisUtils.class);
//    }


    private RedisUtils redisUtils;
    public RedisCache(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public V get(K key) {
        return (V) redisUtils.get((String) key);
    }

    @Override
    public void put(K key, V value) {
        redisUtils.set((String) key,value);
    }
}
