package com.ksyun.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ksyun.trade.mapper.TradeProductConfigMapper;
import com.ksyun.trade.pojo.TradeProductConfig;

import javax.annotation.Resource;

public interface TradeProductConfigService extends IService<TradeProductConfig> {

    TradeProductConfig selectByOrderId(Integer tradeId);
}
