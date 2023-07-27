package com.ksyun.trade.controller.online;

import redis.clients.jedis.Jedis;

public class RedisRateLimiter {
    private static final String REDIS_KEY_PREFIX = "rate_limiter:";
    private static final int DEFAULT_LIMIT = 5; // 默认QPS
    private static final int DEFAULT_CAPACITY = 5; // 默认桶容量
    private static final String SCRIPT = "local key = KEYS[1]\n" +
            "local limit = tonumber(ARGV[1])\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "local current = tonumber(redis.call('get', key) or '0')\n" +
            "if current + 1 > limit then\n" +
            "  return 0\n" +
            "else\n" +
            "  redis.call('INCRBY', key, 1)\n" +
            "  redis.call('expire', key, capacity)\n" +
            "  return 1\n" +
            "end";

    private Jedis jedis; // Redis连接
    private int limit; // QPS限制
    private int capacity; // 桶容量
    private String key; // Redis中存储漏桶当前水量的键值

    public RedisRateLimiter(Jedis jedis, String apiPath) {
        this(jedis, apiPath, DEFAULT_LIMIT, DEFAULT_CAPACITY);
    }

    //定制传参自定义漏桶容量和qps
    public RedisRateLimiter(Jedis jedis, String apiPath, int limit, int capacity) {
        this.jedis = jedis;
        this.limit = limit;
        this.capacity = capacity;
        this.key = REDIS_KEY_PREFIX + apiPath;
    }

    public boolean acquire() {
        Long result = (Long) jedis.eval(SCRIPT, 1, key, String.valueOf(limit), String.valueOf(capacity));
        return result == 1;
    }
}
