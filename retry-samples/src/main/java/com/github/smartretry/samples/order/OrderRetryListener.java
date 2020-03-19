package com.github.smartretry.samples.order;

import com.github.smartretry.core.RetryContext;
import com.github.smartretry.core.listener.RetryListener;
import com.github.smartretry.samples.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
@Service("orderRetryListener")
public class OrderRetryListener implements RetryListener {

    @Autowired
    private OrderBusiness orderBusiness;

    @Override
    public void onRetry(RetryContext retryContext) {
        log.info("@RetryFunction收到重试回调，retryCount={}, args={}", retryContext.getRetryCount(), retryContext.getArgs());
    }

    @Override
    public void onComplete(RetryContext retryContext) {
        log.info("@RetryFunction重试任务已完成，retryCount={}, args={}, result={}", retryContext.getRetryCount(), retryContext.getArgs(), retryContext.getResult());
    }

    @Override
    public void onError(RetryContext retryContext) {
        log.info("@RetryFunction重试任务失败，retryCount={}, args={}, error={}", retryContext.getRetryCount(), retryContext.getArgs(), retryContext.getException().getMessage());
        orderBusiness.setOrderFail((Order) retryContext.getArgs());
    }
}
