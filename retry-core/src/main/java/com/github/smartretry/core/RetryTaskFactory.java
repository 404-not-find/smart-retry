package com.github.smartretry.core;

/**
 * @author yuni[mn960mn@163.com]
 */
@FunctionalInterface
public interface RetryTaskFactory {

    RetryTask create(RetryHandler<?, ?> retryHandler, Object params);
}
