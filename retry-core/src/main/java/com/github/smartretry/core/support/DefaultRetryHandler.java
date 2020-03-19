package com.github.smartretry.core.support;

import com.github.smartretry.core.IllegalRetryException;
import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.listener.RetryListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author yuni[mn960mn@163.com]
 */
public class DefaultRetryHandler implements GenericRetryHandler {

    private RetryHandler delegate;

    private Class<?> inputArgsType;

    public DefaultRetryHandler(RetryHandler delegate) {
        this.delegate = delegate;
        this.inputArgsType = parseInputArgsType();
    }

    protected Class<?> parseInputArgsType() {
        Class<?> current = delegate.getClass();
        while (current != null) {
            for (Type type : current.getGenericInterfaces()) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (RetryHandler.class.equals(parameterizedType.getRawType())) {
                        Type targetType = parameterizedType.getActualTypeArguments()[0];
                        if (targetType instanceof Class) {
                            Class<?> targetClazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            if (Object.class.equals(targetClazz)) {
                                throw new IllegalRetryException(delegate.getClass().getName() + ".handle方法输入参数的泛型类型不能是Object等无法序列化和反序列化的类型");
                            } else if (Collection.class.isAssignableFrom(targetClazz) || Map.class.isAssignableFrom(targetClazz)) {
                                throw new IllegalRetryException("重试方法的参数类型[" + targetClazz + "]不能是集合类等带泛型的");
                            }
                            return targetClazz;
                        }
                        throw new IllegalRetryException("重试方法的参数类型不能是集合类等带泛型的");
                    }
                }
            }
            current = current.getSuperclass();
        }
        throw new IllegalRetryException("无法获取到" + delegate.getClass().getName() + ".handle方法输入参数的泛型类型");
    }

    @Override
    public Class<?> getInputArgsType() {
        return this.inputArgsType;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public String identity() {
        return delegate.identity();
    }

    @Override
    public Object handle(Object arg) {
        return delegate.handle(arg);
    }

    @Override
    public RetryListener retryListener() {
        return delegate.retryListener();
    }

    @Override
    public String cron() {
        return delegate.cron();
    }

    @Override
    public int interval() {
        return delegate.interval();
    }

    @Override
    public int maxRetryCount() {
        return delegate.maxRetryCount();
    }

    @Override
    public int initialDelay() {
        return delegate.initialDelay();
    }

    @Override
    public boolean ignoreException() {
        return delegate.ignoreException();
    }
}
