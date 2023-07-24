package com.ksyun.trade.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ksyun.trade.mapper.VoucherDeductMapper;
import com.ksyun.trade.pojo.VoucherDeduct;
import com.ksyun.trade.service.VoucherDeductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VoucherDeductServiceImpl extends ServiceImpl<VoucherDeductMapper, VoucherDeduct> implements VoucherDeductService {
    //按需加
    @Resource VoucherDeductMapper voucherDeductMapper;


    @Override
    public List<VoucherDeduct> selectByOrderIdAndVoucherNo(Integer orderId, String voucherNo) {
        return voucherDeductMapper.selectByOrderIdAndVoucherNo(orderId, voucherNo);
    }
}
