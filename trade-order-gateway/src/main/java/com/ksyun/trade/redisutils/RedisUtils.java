package com.ksyun.trade.redisutils;

import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;  //com/ksyun/trade/bootstrap/RedisConfig.java

    // String 类型操作
    public void set(String key, Object value) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
//        System.out.println("key=value :" + key + "=" + value );
        ops.set(key, value);
        redisTemplate.expire(key, 30, TimeUnit.SECONDS); //过期时间30 * 1000单位毫秒 //默认是永不过期
    }

    public Object get(String key) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Hash 类型操作
    public void hSet(String key, String hashKey, Object hashValue) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        ops.put(key, hashKey, hashValue);
    }
    public void hmset(String key, Map<String, String> hash) {
        redisTemplate.opsForHash().putAll(key, hash);
    }

    public Object hGet(String key, String hashKey) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        return ops.get(key, hashKey);
    }

    public Map<String, String> hGetAll(String key) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.entries(key);
    }

    public void hDelete(String key, String hashKey) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        ops.delete(key, hashKey);
    }

    // List 类型操作
    public void lPush(String key, Object value) {
        ListOperations<String, Object> ops = redisTemplate.opsForList();
        ops.leftPush(key, value);
    }

    public Object lPop(String key) {
        ListOperations<String, Object> ops = redisTemplate.opsForList();
        return ops.leftPop(key);
    }

    public List<Object> lRange(String key, long start, long end) {
        ListOperations<String, Object> ops = redisTemplate.opsForList();
        return ops.range(key, start, end);
    }

    // Set 类型操作
    public void sAdd(String key, Object... values) {
        SetOperations<String, Object> ops = redisTemplate.opsForSet();
        ops.add(key, values);
    }

    public Set<Object> sMembers(String key) {
        SetOperations<String, Object> ops = redisTemplate.opsForSet();
        return ops.members(key);
    }

    public void sRemove(String key, Object... values) {
        SetOperations<String, Object> ops = redisTemplate.opsForSet();
        ops.remove(key, values);
    }

    // ZSet 类型操作
    public void zAdd(String key, Object value, double score) {
        ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
        ops.add(key, value, score);
    }

    public Set<Object> zRangeByScore(String key, double minScore, double maxScore) {
        ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
        return ops.rangeByScore(key, minScore, maxScore);
    }

    public void zRemove(String key, Object value) {
        ZSetOperations<String, Object> ops = redisTemplate.opsForZSet();
        ops.remove(key, value);
    }
}