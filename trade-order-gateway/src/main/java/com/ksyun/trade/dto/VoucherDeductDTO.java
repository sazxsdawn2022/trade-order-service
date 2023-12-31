package com.ksyun.trade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VoucherDeductDTO {

    private Integer orderId;
    private String voucherNo;
    private BigDecimal amount;
    private BigDecimal beforeDeductAmount;
    private BigDecimal afterDeductAmount;

}
