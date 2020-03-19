package com.github.smartretry.samples.order;

import com.github.smartretry.core.RetryFunction;
import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.samples.order.entity.Order;
import com.github.smartretry.samples.order.model.CreateOrderReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
@Service
public class OrderService {

    private String businessId1 = "T10024";

    private String businessId2 = "H30000";

    @Autowired
    @Qualifier("orderPaymentBusiness")
    private RetryHandler<Order, Long> orderPaymentBusiness;

    @Autowired
    private OrderBusiness orderBusiness;

    private RandomErrorAction ratioWith40 = new RandomErrorAction(40);

    /**
     * 有参数的最简单的一个例子，每6分钟重试一次，直到成功
     */
    @RetryFunction(name = "最简单的RetryFunction重试示例", identity = "demo.simplest", cron = "0 3/6 * * * ? *")
    public void simplestWithId(int id) {
        log.info("simplestWithId[{}]执行开始", id);
        ratioWith40.doAction();
        log.info("simplestWithId[{}]执行完成", id);
    }

    public Long createOrderWithRetryFunction(CreateOrderReq req) {
        log.info("@RetryFunction开始执行createOrder, {}", req);
        Order order = orderBusiness.insertOrder(req, businessId1);
        orderBusiness.payOrderAndUpdateStatus(order);
        return order.getOrderId();
    }

    public Long createOrderWithRetryHandler(CreateOrderReq req) {
        log.info("RetryHandler开始执行createOrder, {}", req);
        Order order = orderBusiness.insertOrder(req, businessId2);
        return orderPaymentBusiness.handle(order);
    }
}
