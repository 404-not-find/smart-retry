package com.github.smartretry.core.listener;

import com.github.smartretry.core.RetryContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 一个RetryListener的包装器，防止用户自定义的RetryListener执行报错阻塞流程
 *
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
public class QuietRetryListener implements RetryListener {

    private RetryListener delegate;

    public QuietRetryListener() {
        this(new RetryListener() {
        });
    }

    public QuietRetryListener(RetryListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onRetry(RetryContext retryContext) {
        try {
            delegate.onRetry(retryContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onComplete(RetryContext retryContext) {
        try {
            delegate.onComplete(retryContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onError(RetryContext retryContext) {
        try {
            delegate.onError(retryContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
