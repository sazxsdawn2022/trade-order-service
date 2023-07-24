package com.ksyun.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ksyun.trade.pojo.VoucherDeduct;

import java.util.List;

public interface VoucherDeductService extends IService<VoucherDeduct> {
    List<VoucherDeduct> selectByOrderIdAndVoucherNo(Integer orderId, String voucherNo);


}
