package com.ksyun.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ksyun.trade.pojo.TradeProductConfig;
import org.springframework.stereotype.Repository;


public interface TradeProductConfigMapper extends BaseMapper<TradeProductConfig> {
    //需要其他的方法可扩展
    TradeProductConfig selectByOrderId(Integer orderId);
}
