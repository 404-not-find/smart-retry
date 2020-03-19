package com.github.smartretry.samples.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author yuni[mn960mn@163.com]
 */
@Setter
@Getter
@ToString
public class CreateOrderReq {

    @NotNull
    private Long userId;

    @NotNull
    @Min(value = 1)
    private BigDecimal price;
}
