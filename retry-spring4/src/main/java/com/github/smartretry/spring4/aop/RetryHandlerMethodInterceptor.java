package com.github.smartretry.spring4.aop;

import com.github.smartretry.core.RetryFunction;
import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.registry.RetryHandlerRegistry;
import com.github.smartretry.core.util.RetryHandlerUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryHandlerMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) {
        RetryFunction retryFunction = invocation.getMethod().getAnnotation(RetryFunction.class);
        Object[] args = invocation.getArguments();
        String identity = retryFunction.identity();
        if (StringUtils.isBlank(identity)) {
            identity = RetryHandlerUtils.getMethodIdentity(invocation.getMethod());
        }
        Optional<RetryHandler> optional = RetryHandlerRegistry.get(identity);
        if (optional.isPresent()) {
            return optional.get().handle(ArrayUtils.isEmpty(args) ? null : args[0]);
        }
        throw new IllegalArgumentException("找不到对应的RetryHandler代理，identity=" + identity);
    }
}