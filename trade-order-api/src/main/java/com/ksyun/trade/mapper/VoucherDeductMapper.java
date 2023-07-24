package com.ksyun.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ksyun.trade.pojo.VoucherDeduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VoucherDeductMapper extends BaseMapper<VoucherDeduct> {
    //需要其他的方法可扩展
    List<VoucherDeduct> selectByOrderIdAndVoucherNo(Integer orderId, String voucherNo);
}
