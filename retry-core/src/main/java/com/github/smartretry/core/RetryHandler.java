package com.github.smartretry.core;

import com.github.smartretry.core.listener.RetryListener;

/**
 * @author yuni[mn960mn@163.com]
 */
@FunctionalInterface
public interface RetryHandler<T, R> {

    /**
     * 默认重试间隔时长5分钟
     */
    int DEFAULT_RETRY_INTERVAL = 300;

    /**
     * 默认最多重试次数5次
     */
    int DEFAULT_RETRY_MAX_COUNT = 5;

    /**
     * 默认延迟5分钟
     */
    int DEFAULT_INITIALDELAY = 300;

    boolean DEFAULT_IGNOREEXCEPTION = true;

    /**
     * 任务名称
     *
     * @return
     */
    default String name() {
        return null;
    }

    /**
     * 唯一标识，不能重复
     *
     * @return
     */
    default String identity() {
        return this.getClass().getName();
    }

    /**
     * 任务处理
     *
     * @param arg 参数，参数类型需要满足如下条件
     *            方法的参数不能是Object等无法被JSON序列化和反序列化的类型、方法的参数不能是Collection，List等带泛型的类型
     * @return
     */
    R handle(T arg);

    /**
     * 任务监听器。可以在任务重试、任务完成、任务失败时进行回调
     *
     * @return
     */
    default RetryListener retryListener() {
        return null;
    }

    /**
     * 重试cron表达式，为空则使用interval()进行重试
     *
     * @return
     */
    default String cron() {
        return null;
    }

    /**
     * 重试间隔时长。单位：秒
     *
     * @return
     */
    default int interval() {
        return DEFAULT_RETRY_INTERVAL;
    }

    /**
     * 最多重试次数
     *
     * @return
     */
    default int maxRetryCount() {
        return DEFAULT_RETRY_MAX_COUNT;
    }

    /**
     * 延迟时长。单位：秒
     * 任务失败之后，多久开始重试
     *
     * @return
     */
    default int initialDelay() {
        return DEFAULT_INITIALDELAY;
    }

    /**
     * 重试的时候，是否忽略错误继续执行
     *
     * @return
     */
    default boolean ignoreException() {
        return DEFAULT_IGNOREEXCEPTION;
    }
}
