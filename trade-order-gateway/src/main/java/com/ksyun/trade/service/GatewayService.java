package com.ksyun.trade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.req.trace.ReqTraceConsts;
import com.ksyun.req.trace.RequestTraceContextSlf4jMDCHolder;
import com.ksyun.trade.dto.TradeResultDTO;
import com.ksyun.trade.dto.VoucherDeductDTO;
import com.ksyun.trade.rest.RestResult;
import com.sun.webkit.network.URLs;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;

import static sun.plugin2.util.PojoUtil.toJson;

@Service
public class GatewayService {

    @Value("${actions}")
    private String urls;

    private String[] URLs;

    private RestTemplate restTemplate = new RestTemplate();

    //订单详情和机房名称共用这个方法
    public Object loadLalancing(Object param, HttpServletRequest request) {

        String requestId = RequestTraceContextSlf4jMDCHolder.getRequestId();
        System.out.println("gateway service requestId = " + requestId);

        String paramName = "";

        // 1. 模拟路由 (轮询) 获取接口
        URLs = urls.split(",");
        String desHost =  round();
        String requestURI = request.getRequestURI();  //  /online/queryRegionName
        //获取url参数名
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            paramName = parameterNames.nextElement();
//            System.out.println("parameterNames.nextElement() = " + s);
        }

        //组装成targetUrl
        String targetUrl = "";
        if ("".equals(paramName)) {
            targetUrl = desHost + requestURI;
        } else {
            targetUrl = desHost + requestURI + "?" + paramName + "=" + param;
        }


        // 2. 请求转发

        //构建RestTemplate
        HttpHeaders headers = new HttpHeaders();
        //获得upstream带到api模块用
        String upstream = subUpstream(desHost);
//        System.out.println("upstream gateway中的= " + upstream);
        //添加upstream
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("upstream", upstream);

        //区分开来，如果是/online/queryOrderInfo请求则带上upstream发POST请求，否则不带upstream发GET请求
        if(requestURI.contains("queryOrderInfo")){
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add(ReqTraceConsts.REQUEST_ID, requestId);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.POST, requestEntity, String.class);
            return responseEntity.getBody();

        } else {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(ReqTraceConsts.REQUEST_ID, requestId);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, String.class);
            Object body = responseEntity.getBody();
            return body;
        }
    }



    //优惠劵的
    public Object voucherDeduct(VoucherDeductDTO voucherDeductDTO, HttpServletRequest request) throws JsonProcessingException {

        String requestId = RequestTraceContextSlf4jMDCHolder.getRequestId();
        System.out.println("gateway service requestId = " + requestId);

        //把请求转发到api，然后在api端
        //先从Mysql的订单表中查orderId对应表单的priceValue，然后减去优惠券的amount后，插入到ksc_voucher_deduct中

        // 1. 模拟路由 (轮询) 获取接口
        URLs = urls.split(",");
        String desHost =  round();
        String requestURI = request.getRequestURI();  //  /online/queryRegionName
        //组装成targetUrl
        String targetUrl = desHost + requestURI;


        // 2. 请求转发
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(voucherDeductDTO);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("voucherDeductDTO", jsonString);
        //构建RestTemplate
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(ReqTraceConsts.REQUEST_ID, requestId);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();

    }



    //获得upstream
    public String subUpstream(String hostStr){
        int startIndex = hostStr.indexOf("//") + 2;
        int endIndex = hostStr.indexOf(":", startIndex);
        String subStr = hostStr.substring(startIndex, endIndex);
        return subStr;
    }





    //提供三个简单的负载均衡的算法（随机，hash，轮询）这里用随机
    //随机算法
    public String random() {
        Random random = new Random();
        int index = random.nextInt(URLs.length);
        return URLs[index];
    }

    //hash
    public String hash(HttpServletRequest request) {
        String ip = getIpAddr(request);
        int index = Math.abs(ip.hashCode() % URLs.length);
        return URLs[index];
    }

    private int roundIndex = 0;

    //轮询
    public String round() {
        String url = URLs[roundIndex];
        roundIndex = (roundIndex + 1) % URLs.length;
        return url;
    }


    //获取请求的ip地址
    private static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}
