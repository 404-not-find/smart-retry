package com.github.smartretry.samples.order;

import com.github.smartretry.core.RetryContext;
import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.listener.RetryListener;
import com.github.smartretry.samples.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
@Service("orderPaymentBusiness")
public class OrderPaymentBusiness implements RetryHandler<Order, Long> {

    private RandomErrorAction ratioWith30 = new RandomErrorAction(70);

    @Autowired
    private OrderBusiness orderBusiness;

    @Override
    public String identity() {
        return "demo.order.payment";
    }

    @Override
    public Long handle(Order order) {
        //30%的概率报错
        ratioWith30.doAction();
        orderBusiness.updateOrderPayStatus(order);
        return order.getOrderId();
    }

    @Override
    public int maxRetryCount() {
        return 4;
    }

    @Override
    public RetryListener retryListener() {
        return new RetryListener() {
            @Override
            public void onRetry(RetryContext retryContext) {
                log.info("RetryHandler收到重试回调，retryCount={}, args={}", retryContext.getRetryCount(), retryContext.getArgs());
            }

            @Override
            public void onComplete(RetryContext retryContext) {
                log.info("RetryHandler重试任务已完成，retryCount={}, args={}, result={}", retryContext.getRetryCount(), retryContext.getArgs(), retryContext.getResult());
            }

            @Override
            public void onError(RetryContext retryContext) {
                log.info("RetryHandler重试任务失败，retryCount={}, args={}, error={}", retryContext.getRetryCount(), retryContext.getArgs(), retryContext.getException().getMessage());
                orderBusiness.setOrderFail((Order) retryContext.getArgs());
            }
        };
    }
}
