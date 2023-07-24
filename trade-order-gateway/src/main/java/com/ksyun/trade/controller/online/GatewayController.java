package com.ksyun.trade.controller.online;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ksyun.req.trace.RequestTraceContextSlf4jMDCHolder;
import com.ksyun.trade.dto.VoucherDeductDTO;
import com.ksyun.trade.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class GatewayController {
    @Autowired
    private GatewayService gatewayService;

    /**
     * 查询订单详情 (GET)
     */
    @RequestMapping(value = "/online/queryOrderInfo", produces = "application/json")
    public Object queryOrderInfo(@RequestParam(value = "id") Integer id, HttpServletRequest request) {

        Map<String, String> traceHeaders = RequestTraceContextSlf4jMDCHolder.getTraceHeaders();
        String requestId = RequestTraceContextSlf4jMDCHolder.getRequestId();
        System.out.println("gateway controller requestId = " + requestId);
        System.out.println("gateway controller traceHeaders = " + traceHeaders);

        return gatewayService.loadLalancing(id, request);
    }

    /**
     * 根据机房Id查询机房名称 (GET)
     */
    @RequestMapping(value = "/online/queryRegionName", produces = "application/json")
    public Object queryRegionName(@RequestParam(value = "regionId") Integer regionId, HttpServletRequest request) {
        return gatewayService.loadLalancing(regionId, request);
    }

    /**
     * 订单优惠券抵扣 (POST json)
     */
    @RequestMapping(value = "/online/voucher/deduct", produces = "application/json")
    public Object deduct(@RequestBody VoucherDeductDTO param, HttpServletRequest request) throws JsonProcessingException {
        return gatewayService.voucherDeduct(param, request);
    }

    /**
     * 基于Redis实现漏桶限流算法，并在API调用上体现
     */
//    @RequestMapping(value = "/online/listUpstreamInfo", produces = "application/json")
//    public Object listUpstreamInfo() {
//        return null;
//    }

}
