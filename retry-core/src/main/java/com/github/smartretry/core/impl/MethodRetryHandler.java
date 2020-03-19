package com.github.smartretry.core.impl;

import com.github.smartretry.core.RetryFunction;
import com.github.smartretry.core.listener.RetryListener;
import com.github.smartretry.core.support.GenericRetryHandler;
import com.github.smartretry.core.util.RetryHandlerUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * @author yuni[mn960mn@163.com]
 */
public class MethodRetryHandler implements GenericRetryHandler {

    private Object targetObject;

    private Method method;

    private RetryFunction retryed;

    private Supplier<RetryListener> retryListenerSupplier;

    public MethodRetryHandler(Object targetObject, Method method, RetryFunction retryed, Supplier<RetryListener> retryListenerSupplier) {
        this.targetObject = targetObject;
        this.method = method;
        this.retryed = retryed;
        this.retryListenerSupplier = retryListenerSupplier;
    }

    @Override
    public String name() {
        return retryed.name();
    }

    @Override
    public String identity() {
        String identity = retryed.identity();
        return StringUtils.isBlank(identity) ? RetryHandlerUtils.getMethodIdentity(method) : identity;
    }

    @Override
    public Object handle(Object arg) {
        try {
            return method.getParameterCount() > 0 ? method.invoke(targetObject, arg) : method.invoke(targetObject);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getCause().getMessage(), e.getCause());
        }
    }

    @Override
    public RetryListener retryListener() {
        return retryListenerSupplier.get();
    }

    @Override
    public String cron() {
        return retryed.cron();
    }

    @Override
    public int interval() {
        return retryed.interval();
    }

    @Override
    public int maxRetryCount() {
        return retryed.maxRetryCount();
    }

    @Override
    public int initialDelay() {
        return retryed.initialDelay();
    }

    @Override
    public boolean ignoreException() {
        return retryed.ignoreException();
    }

    @Override
    public Class<?> getInputArgsType() {
        return method.getParameterCount() == 1 ? method.getParameterTypes()[0] : null;
    }
}