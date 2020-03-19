package com.github.smartretry.core.impl;

import com.github.smartretry.core.RetryContext;
import com.github.smartretry.core.RetryTask;
import com.github.smartretry.core.RetryTaskMapper;
import com.github.smartretry.core.listener.QuietRetryListener;
import com.github.smartretry.core.listener.RetryListener;
import com.github.smartretry.core.support.GenericRetryHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yuni[mn960mn@163.com]
 */
abstract class ExecuteRetryHandler implements GenericRetryHandler {

    protected GenericRetryHandler genericRetryHandler;

    protected RetryTaskMapper retryTaskMapper;

    private RetryListener delegateRetryListener;

    private Class<?> inputArgsType;

    public ExecuteRetryHandler(GenericRetryHandler genericRetryHandler, RetryTaskMapper retryTaskMapper) {
        this.genericRetryHandler = genericRetryHandler;
        this.retryTaskMapper = retryTaskMapper;
        this.inputArgsType = genericRetryHandler.getInputArgsType();
    }

    @Override
    public String name() {
        return genericRetryHandler.name();
    }

    @Override
    public String identity() {
        return genericRetryHandler.identity();
    }

    @Override
    public String cron() {
        return genericRetryHandler.cron();
    }

    @Override
    public int interval() {
        return genericRetryHandler.interval();
    }

    @Override
    public int maxRetryCount() {
        return genericRetryHandler.maxRetryCount();
    }

    public Class<?> getInputArgsType() {
        return inputArgsType;
    }

    protected synchronized RetryListener getRetryListener() {
        if (delegateRetryListener == null) {
            RetryListener retryListener = genericRetryHandler.retryListener();
            if (retryListener == null) {
                delegateRetryListener = new RetryListener() {
                };
            } else {
                delegateRetryListener = new QuietRetryListener(retryListener);
            }
        }
        return delegateRetryListener;
    }

    protected void onRetry(RetryContext retryContext) {
        getRetryListener().onRetry(retryContext);
    }

    protected void onError(RetryContext retryContext) {
        getRetryListener().onError(retryContext);
    }

    protected void onComplete(RetryContext retryContext) {
        getRetryListener().onComplete(retryContext);
    }

    protected int completeTask(RetryTask retryTask) {
        retryTask.setStatus(RetryTask.STATUS_SUCCESS);
        return retryTaskMapper.update(retryTask);
    }

    protected int failureTask(RetryTask retryTask, RetryContext retryContext) {
        retryTask.setStatus(RetryTask.STATUS_EXCEPTION);
        retryTask.setRemark(StringUtils.left(retryContext.getException().getMessage(), 1000));
        return retryTaskMapper.update(retryTask);
    }

    protected int update(RetryTask retryTask, RetryContext retryContext) {
        retryTask.setRetryCount(retryContext.getRetryCount());
        if (retryContext.getException() != null) {
            retryTask.setRemark(StringUtils.left(retryContext.getException().getMessage(), 1000));
        }
        return retryTaskMapper.update(retryTask);
    }

    protected int updateRemark(RetryTask retryTask, Throwable e) {
        retryTask.setRemark(StringUtils.left(e.getMessage(), 1000));
        return retryTaskMapper.update(retryTask);
    }
}
