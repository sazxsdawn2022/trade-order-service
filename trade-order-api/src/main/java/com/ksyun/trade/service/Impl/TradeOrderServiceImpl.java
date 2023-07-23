package com.ksyun.trade.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ksyun.trade.mapper.TradeOrderMapper;
import com.ksyun.trade.pojo.TradeOrder;
import com.ksyun.trade.service.TradeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TradeOrderServiceImpl extends ServiceImpl<TradeOrderMapper, TradeOrder> implements TradeOrderService {

//    public Object query(Integer id) {
//        //
//        return null;
//    }

}