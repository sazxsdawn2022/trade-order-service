package com.ksyun.trade.controller.online;

import com.ksyun.trade.rest.RestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;



@RestController
public class listUpstreamController {

    // Initialize Jedis
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;


    @Value("${spring.redis.password}")
    private String redisPassword;

    private static final String API_PATH = "/online/listUpstreamInfo";
    private RedisRateLimiter rateLimiter;


    // 在接口方法中添加限流处理逻辑
    @RequestMapping (value = "/online/listUpstreamInfo", produces = "application/json")
    public RestResult<Object> listUpstreamInfo() {

        //连接redis
        Jedis jedis = new Jedis(redisHost, redisPort);
        if (!StringUtils.isEmpty(redisPassword)) {
            jedis.auth(redisPassword);
        }

        rateLimiter = new RedisRateLimiter(jedis, API_PATH);

        if (!rateLimiter.acquire()) {
            return RestResult.failure().msg("对不起, 系统压力过大, 请稍后再试!");
        }

        return RestResult.success().data(new String[]{"campus.query1.ksyun.com", "campus.query2.ksyun.com"});
    }

}

