package com.github.smartretry.core.impl;

import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.RetrySerializer;
import com.github.smartretry.core.RetryTask;
import com.github.smartretry.core.RetryTaskFactory;
import com.github.smartretry.core.util.ServiceLoaderUtils;

import java.time.LocalDateTime;

/**
 * @author yuni[mn960mn@163.com]
 * @see ImmediatelyRetryHandler
 */
public class DefaultRetryTaskFactory implements RetryTaskFactory {

    private RetrySerializer retrySerializer;

    public DefaultRetryTaskFactory() {
        this(ServiceLoaderUtils.loadService(RetrySerializer.class));
    }

    public DefaultRetryTaskFactory(RetrySerializer retrySerializer) {
        this.retrySerializer = retrySerializer;
    }

    @Override
    public RetryTask create(RetryHandler<?, ?> retryHandler, Object params) {
        RetryTask task = new RetryTask();
        task.setIdentity(retryHandler.identity());
        if (params != null) {
            task.setParams(retrySerializer.serialize(params));
        }
        task.setStatus(RetryTask.STATUS_INIT);
        task.setRetryCount(0);
        task.setCreateDate(LocalDateTime.now());
        return task;
    }
}
