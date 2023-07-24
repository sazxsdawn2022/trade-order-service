package com.ksyun.trade.controller.online;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.trade.dto.UserDTO;
import com.ksyun.trade.dto.VoucherDeductDTO;
import com.ksyun.trade.pojo.TradeOrder;
import com.ksyun.trade.pojo.VoucherDeduct;
import com.ksyun.trade.rest.RestResult;
import com.ksyun.trade.service.TradeOrderService;
import com.ksyun.trade.service.VoucherDeductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/online", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class VoucherDeductController {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TradeOrderService tradeOrderService;

    @Resource
    private VoucherDeductService voucherDeductService;

    @RequestMapping("/voucher/deduct")
    public RestResult voucherDeduct(HttpServletRequest request) throws IOException {

        //获取gateway传来的upstream参数
        String voucherDeductDTOStr = request.getParameter("voucherDeductDTO");

        //获得userDTO
        //把json字符串映射成对象
        ObjectMapper objectMapper = new ObjectMapper();
        VoucherDeductDTO voucherDeductDTO = objectMapper.readValue(voucherDeductDTOStr, VoucherDeductDTO.class);

        Integer orderId = voucherDeductDTO.getOrderId();
        TradeOrder tradeOrder = tradeOrderService.getById(orderId);
        BigDecimal beforeDeductAmount = tradeOrder.getPriceValue();
        BigDecimal afterDeductAmount = beforeDeductAmount.subtract(voucherDeductDTO.getAmount());

        //从ksc_voucher_deduct表查一下，这个订单有没有用过这张表，用过的话不能重复用
        String voucherNo = voucherDeductDTO.getVoucherNo();
        List<VoucherDeduct> voucherDeducts = voucherDeductService.selectByOrderIdAndVoucherNo(orderId, voucherNo);
        if (!voucherDeducts.isEmpty()){
            return RestResult.failure().msg("优惠劵不能重复使用");
        }

        VoucherDeduct voucherDeduct = new VoucherDeduct();
        voucherDeduct.setOrderId(orderId);
        voucherDeduct.setVoucherNo(voucherNo);
        voucherDeduct.setAmount(voucherDeductDTO.getAmount());
        voucherDeduct.setBeforeDeductAmount(beforeDeductAmount);
        voucherDeduct.setAfterDeductAmount(afterDeductAmount);
        voucherDeduct.setCreateTime(new Date());
        voucherDeduct.setUpdateTime(new Date());

        voucherDeductService.save(voucherDeduct);

        return RestResult.success();
    }

}
