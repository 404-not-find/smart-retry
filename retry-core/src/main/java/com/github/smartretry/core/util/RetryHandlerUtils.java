package com.github.smartretry.core.util;

import com.github.smartretry.core.IllegalRetryException;
import com.github.smartretry.core.RetryFunction;
import com.github.smartretry.core.RetryHandler;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryHandlerUtils {

    private RetryHandlerUtils() {
    }

    /**
     * Method的输入参数最多只能有一个，且不能是Object或者其他不能序列化和反序列化等类型
     *
     * @param method
     * @return
     */
    public static boolean isRetryFunctionMethod(Method method) {
        if (method.getAnnotation(RetryFunction.class) != null && method.getParameterCount() == 1) {
            return !Object.class.equals(method.getParameterTypes()[0]);
        }
        return false;
    }

    public static void validateRetryHandler(Class<?> clazz) {
        Type interfaceType = getRetryHandlerGenericInterface(clazz);
        Class<?> argsInputType = Object.class;
        if (interfaceType instanceof ParameterizedType) {
            Type type = ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
            if (type instanceof Class) {
                argsInputType = (Class<?>) ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
            } else {
                throw new IllegalRetryException("重试方法的参数类型不能是集合类等带泛型的");
            }
        }
        if (Object.class.equals(argsInputType)) {
            throw new IllegalRetryException("重试方法的参数类型[" + argsInputType + "]不能是Object或其他不能序列化和反序列化的类型");
        } else if (Collection.class.isAssignableFrom(argsInputType) || Map.class.isAssignableFrom(argsInputType)) {
            throw new IllegalRetryException("重试方法的参数类型[" + argsInputType + "]不能是集合类等带泛型的");
        }
    }

    public static void validateRetryFunction(Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalRetryException(method.toString() + ": 重试方法有且只能有一个参数");
        }
        Class<?> clazz = method.getParameterTypes()[0];
        if (Object.class.equals(clazz)) {
            throw new IllegalRetryException(method.toString() + ": 重试方法的参数类型不能是Object或其他不能序列化和反序列化的类型");
        } else if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            throw new IllegalRetryException(method.toString() + ": 重试方法的参数类型不能是集合类等带泛型的");
        }
    }

    public static boolean isRetryHandlerMethod(Class<?> targetClass, Method method) {
        if ("handle".equals(method.getName()) && method.getParameterCount() == 1 && method.isBridge() && method.isSynthetic()) {
            //RetryHandler接口有泛型，需要特殊处理
            return true;
        }
        Type interfaceType = getRetryHandlerGenericInterface(targetClass);
        if (interfaceType == null) {
            return false;
        }
        Class<?> argsInputType = Object.class;
        if (interfaceType instanceof ParameterizedType) {
            argsInputType = (Class<?>) ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
        }
        Class<?> parameterType = argsInputType;
        return "handle".equals(method.getName()) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(parameterType);
    }

    public static Type getRetryHandlerGenericInterface(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null) {
            Type[] types = current.getGenericInterfaces();
            Optional<Type> interfaceTypeOptional = Stream.of(types).filter(i -> i.getTypeName().startsWith(RetryHandler.class.getName())).findAny();
            if (interfaceTypeOptional.isPresent()) {
                return interfaceTypeOptional.get();
            }
            current = clazz.getSuperclass();
        }
        return null;
    }

    public static String getMethodIdentity(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }
}
