package com.github.smartretry.core;

/**
 * 抛出此异常，系统不会进行重试。直接当失败处理
 *
 * @author yuni[mn960mn@163.com]
 */
public class NoRetryException extends RuntimeException {

    public NoRetryException(String message) {
        super(message);
    }

    public NoRetryException(String message, Throwable cause) {
        super(message, cause);
    }
}