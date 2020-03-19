package com.github.smartretry.spring4;

import com.github.smartretry.spring4.autoconfigure.RetryImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuni[mn960mn@163.com]
 *
 * @see RetryImportSelector
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RetryImportSelector.class)
public @interface EnableRetrying {

    /**
     * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
     * to standard Java interface-based proxies. The default is {@code true}.
     */
    boolean proxyTargetClass() default true;
}
