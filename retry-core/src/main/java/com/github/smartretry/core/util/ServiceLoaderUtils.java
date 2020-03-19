package com.github.smartretry.core.util;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author yuni[mn960mn@163.com]
 */
public class ServiceLoaderUtils {

    private ServiceLoaderUtils() {
    }

    public static <T> T loadService(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        throw new IllegalArgumentException("无法在META-INF/services找到" + clazz.getName() + "的实例");
    }
}