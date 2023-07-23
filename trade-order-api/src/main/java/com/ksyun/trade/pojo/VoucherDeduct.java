package com.ksyun.trade.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("ksc_voucher_deduct")
public class VoucherDeduct {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer orderId;

    private String voucherNo;

    private BigDecimal amount;

    private BigDecimal beforeDeductAmount;

    private BigDecimal afterDeductAmount;

    private Date createTime;

    private Date updateTime;
}
