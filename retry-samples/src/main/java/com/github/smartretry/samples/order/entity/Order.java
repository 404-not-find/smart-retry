package com.github.smartretry.samples.order.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author yuni[mn960mn@163.com]
 */
@Setter
@Getter
@ToString
public class Order {

    private Long orderId;

    private Long userId;

    private String businessId;

    private BigDecimal price;

    /**
     * 100：初始化
     * 110：处理中
     * 300：完成
     * 500：失败
     */
    private int status;

    /**
     * 0：未支付
     * 1：支付成功
     * 2：支付失败
     */
    private int payStatus;

    private LocalDateTime createDate;

    private LocalDateTime editDate;
}
