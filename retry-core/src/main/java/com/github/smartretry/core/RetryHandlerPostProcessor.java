package com.github.smartretry.core;

/**
 * RetryHandler的后置处理器
 *
 * @author yuni[mn960mn@163.com]
 */
@FunctionalInterface
public interface RetryHandlerPostProcessor<T, R> {

    RetryHandler<T, R> doPost(RetryHandler<T, R> retryHandler);
}