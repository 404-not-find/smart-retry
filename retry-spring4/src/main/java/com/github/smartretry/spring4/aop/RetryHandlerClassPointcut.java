package com.github.smartretry.spring4.aop;

import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.util.RetryHandlerUtils;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.StaticMethodMatcher;

import java.lang.reflect.Method;

/**
 * 对所有RetryHandler接口的实例进行代理
 *
 * @author yuni[mn960mn@163.com]
 */
public class RetryHandlerClassPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return RetryHandler.class::isAssignableFrom;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new StaticMethodMatcher() {

            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return RetryHandlerUtils.isRetryHandlerMethod(targetClass, method);
            }
        };
    }
}
