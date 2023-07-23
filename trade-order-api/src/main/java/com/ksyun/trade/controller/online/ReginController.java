package com.ksyun.trade.controller.online;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.trade.rest.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(value = "/online", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class ReginController {
    @Resource
    private RestTemplate restTemplate;

    @RequestMapping("/queryRegionName")
    public ResponseEntity query(@RequestParam(value = "regionId") Integer regionId, HttpServletRequest request) throws IOException {

        //构建RestTemplate
        String targetUrl = "http://campus.meta.ksyun.com:8090/online/region/name/" +  regionId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, String.class);
//        {\"code\":200,\"msg\":\"ok\",\"requestId\":\"3323dcf4-7f30-4f9a-8d85-a93cccb0c352\",\"descr\":null,\"data\":\"南京\"}
        //取出data
        String body = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        RestResult restResult = mapper.readValue(body, RestResult.class);
        Object data = restResult.getData();

        //返回
        return ResponseEntity.ok(data);
    }
}
