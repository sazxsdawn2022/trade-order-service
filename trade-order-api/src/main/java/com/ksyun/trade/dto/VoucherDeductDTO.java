package com.ksyun.trade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherDeductDTO {

    private Integer orderId;
    private String voucherNo;
    private BigDecimal amount;
    private BigDecimal beforeDeductAmount;
    private BigDecimal afterDeductAmount;
}
