package com.ksyun.trade.controller.online;

import com.ksyun.trade.redisutils.RedisUtils;
import com.ksyun.trade.rest.RestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class listUpstreamController {

    // Initialize Jedis
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;


    @Value("${spring.redis.password}")
    private String redisPassword;

    // 在接口方法中添加限流处理逻辑
    @RequestMapping (value = "/online/listUpstreamInfo", produces = "application/json")
    public RestResult<List<String>> listUpstreamInfo() {
        return listUpstreamInfo1();
    }

    // 限流
    private static final String LEAKY_BUCKET_LUA_SCRIPT = "local bucket_key = KEYS[1]\n"
            + "local capacity = 5\n"
            + "local rate = 1\n"
            + "local current_water = tonumber(redis.call('hget', bucket_key, 'water')) or 0\n"
            + "local last_update_time = tonumber(redis.call('hget', bucket_key, 'lastUpdateTime')) or 0\n"
            + "local now = tonumber(ARGV[1])\n"
            + "local elapsed_time = math.max(now - last_update_time, 0)\n"
            + "local leak_water = math.floor(elapsed_time * rate)\n"
            + "current_water = math.max(current_water - leak_water, 0)\n"
            + "if current_water + 1 <= capacity then\n"
            + "    redis.call('hset', bucket_key, 'water', current_water + 1)\n"
            + "    redis.call('hset', bucket_key, 'lastUpdateTime', now)\n"
            + "    redis.call('expire', bucket_key, elapsed_time + 1)\n"
            + "    return 1\n"
            + "else\n"
            + "    return 0\n"
            + "end";

    public RestResult listUpstreamInfo1 () {
        String bucketKey = "leaky_bucket:upstream_info"; // 漏桶的键

        Jedis jedis = new Jedis(redisHost, redisPort);
        if (!StringUtils.isEmpty(redisPassword)) {
            jedis.auth(redisPassword);
        }

        // Execute the Lua script
        Long result = (Long) jedis.eval(LEAKY_BUCKET_LUA_SCRIPT, Collections.singletonList(bucketKey),
                Collections.singletonList(String.valueOf(System.currentTimeMillis() / 1000)));

        if (result == 1) {
            // 漏桶未满，允许请求通过
            return RestResult.success().msg("Request allowed");
        } else {
            // 漏桶已满，拒绝请求
            return RestResult.failure().msg("对不起, 系统压力过大, 请稍后再试!");
        }
    }
}
