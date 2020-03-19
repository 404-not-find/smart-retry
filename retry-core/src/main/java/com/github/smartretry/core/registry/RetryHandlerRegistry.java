package com.github.smartretry.core.registry;

import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.RetryHandlerPostProcessor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryHandlerRegistry {

    private static final Map<String, RetryHandler> retryHandlerMap = new ConcurrentHashMap<>();

    public static void registry(RetryHandler retryHandler) {
        if (retryHandlerMap.containsKey(retryHandler.identity())) {
            throw new IllegalArgumentException("RetryHandler identity=" + retryHandler.identity() + " already exists");
        }

        retryHandlerMap.put(retryHandler.identity(), retryHandler);
    }

    public static void registry(RetryHandler retryHandler, RetryHandlerPostProcessor<Object, Object> processor) {
        if (retryHandlerMap.containsKey(retryHandler.identity())) {
            throw new IllegalArgumentException("RetryHandler identity=" + retryHandler.identity() + " already exists");
        }

        retryHandlerMap.put(retryHandler.identity(), processor.doPost(retryHandler));
    }

    public static Optional<RetryHandler> get(String identity) {
        return Optional.ofNullable(retryHandlerMap.get(identity));
    }
}