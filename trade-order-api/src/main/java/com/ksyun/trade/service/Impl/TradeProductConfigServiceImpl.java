package com.ksyun.trade.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ksyun.trade.mapper.TradeProductConfigMapper;
import com.ksyun.trade.pojo.TradeProductConfig;
import com.ksyun.trade.service.TradeProductConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TradeProductConfigServiceImpl extends ServiceImpl<TradeProductConfigMapper, TradeProductConfig> implements TradeProductConfigService {

    @Resource
    private TradeProductConfigMapper tradeProductConfigMapper;

    @Override
    public TradeProductConfig selectByOrderId(Integer orderId) {
        return tradeProductConfigMapper.selectByOrderId(orderId);
    }
    // 按需加
}
