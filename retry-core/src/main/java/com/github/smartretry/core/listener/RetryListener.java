package com.github.smartretry.core.listener;

import com.github.smartretry.core.RetryContext;
import com.github.smartretry.core.RetryHandler;

/**
 * 重试监听器，可以在任务重试、任务完成、任务失败等时机进行回调
 *
 * 3个回调方法都是默认方法，默认空实现，实现者可根据需要进行方法的实现
 *
 * 注意：listener里面的方法执行报错，不会重试
 *
 * @author yuni[mn960mn@163.com]
 *
 * @see RetryHandler
 */
public interface RetryListener {

    /**
     * 每次重试时触发(执行后触发)
     */
    default void onRetry(RetryContext retryContext) {

    }

    /**
     * 任务完成时触发
     */
    default void onComplete(RetryContext retryContext) {

    }

    /**
     * 失败时触发（超过最大重试次数、或者不再重试了）
     */
    default void onError(RetryContext retryContext) {

    }
}
