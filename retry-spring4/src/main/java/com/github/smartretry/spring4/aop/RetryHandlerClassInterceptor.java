package com.github.smartretry.spring4.aop;

import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.registry.RetryHandlerRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryHandlerClassInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) {
        RetryHandler retryHandler = (RetryHandler) invocation.getThis();
        Object[] args = invocation.getArguments();
        return RetryHandlerRegistry.get(retryHandler.identity()).map(rh -> rh.handle(ArrayUtils.isEmpty(args) ? null : args[0])).orElse(null);
    }
}