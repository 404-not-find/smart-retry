package com.github.smartretry.core.impl;

import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.RetryHandlerPostProcessor;
import com.github.smartretry.core.RetryTaskFactory;
import com.github.smartretry.core.RetryTaskMapper;
import com.github.smartretry.core.support.DefaultRetryHandler;
import com.github.smartretry.core.support.GenericRetryHandler;

/**
 * @author yuni[mn960mn@163.com]
 */
public class DefaultRetryHandlerPostProcessor implements RetryHandlerPostProcessor<Object, Object> {

    private RetryTaskFactory retryTaskFactory;

    private RetryTaskMapper retryTaskMapper;

    private boolean beforeTask;

    public DefaultRetryHandlerPostProcessor(RetryTaskMapper retryTaskMapper, boolean beforeTask) {
        this(new DefaultRetryTaskFactory(), retryTaskMapper, beforeTask);
    }

    public DefaultRetryHandlerPostProcessor(RetryTaskFactory retryTaskFactory, RetryTaskMapper retryTaskMapper, boolean beforeTask) {
        this.retryTaskFactory = retryTaskFactory;
        this.retryTaskMapper = retryTaskMapper;
        this.beforeTask = beforeTask;
    }

    @Override
    public RetryHandler<Object, Object> doPost(RetryHandler<Object, Object> retryHandler) {
        if (retryHandler instanceof GenericRetryHandler) {
            return new ImmediatelyRetryHandler((GenericRetryHandler) retryHandler, retryTaskFactory, retryTaskMapper, beforeTask);
        }
        return new ImmediatelyRetryHandler(new DefaultRetryHandler(retryHandler), retryTaskFactory, retryTaskMapper, beforeTask);
    }
}
