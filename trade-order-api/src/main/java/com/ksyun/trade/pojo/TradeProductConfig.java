package com.ksyun.trade.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ksc_trade_product_config")
public class TradeProductConfig {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String itemNo;

    private String itemName;

    private String unit;

    private Integer value;

    private Integer orderId;
}
