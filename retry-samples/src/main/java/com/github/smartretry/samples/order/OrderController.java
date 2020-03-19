package com.github.smartretry.samples.order;

import com.github.smartretry.samples.order.model.CreateOrderReq;
import com.github.smartretry.samples.order.model.CreateOrderResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yuni[mn960mn@163.com]
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    private AtomicInteger idCount = new AtomicInteger(0);

    @GetMapping("/order/simplestWithId")
    public CreateOrderResp simplestWithId() {
        orderService.simplestWithId(idCount.incrementAndGet());
        return new CreateOrderResp("200", "SUCCESS");
    }

    @GetMapping("/order/createOrderWithRetryFunction")
    public CreateOrderResp createOrderWithRetryFunction(@Valid CreateOrderReq req) {
        Long orderId = orderService.createOrderWithRetryFunction(req);
        return new CreateOrderResp(orderId.toString(), "SUCCESS");
    }

    @GetMapping("/order/createOrderWithRetryHandler")
    public CreateOrderResp createOrderWithRetryHandler(@Valid CreateOrderReq req) {
        Long orderId = orderService.createOrderWithRetryHandler(req);
        return new CreateOrderResp(orderId.toString(), "SUCCESS");
    }
}
