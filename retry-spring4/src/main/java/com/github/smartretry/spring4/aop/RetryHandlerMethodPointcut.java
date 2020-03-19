package com.github.smartretry.spring4.aop;

import com.github.smartretry.core.util.RetryHandlerUtils;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.StaticMethodMatcher;

import java.lang.reflect.Method;

/**
 * 对所有方法上面有RetryFunction注解的进行代理
 *
 * @author yuni[mn960mn@163.com]
 */
public class RetryHandlerMethodPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new StaticMethodMatcher() {

            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return RetryHandlerUtils.isRetryFunctionMethod(method);
            }
        };
    }
}
