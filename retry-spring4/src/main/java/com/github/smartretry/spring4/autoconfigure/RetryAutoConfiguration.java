package com.github.smartretry.spring4.autoconfigure;

import com.github.smartretry.core.RetryTaskMapper;
import com.github.smartretry.spring4.BeanConstants;
import com.github.smartretry.spring4.JdbcRetryTaskMapper;
import com.github.smartretry.spring4.RetryAnnotationBeanPostProcessor;
import com.github.smartretry.spring4.aop.RetryHandlerClassInterceptor;
import com.github.smartretry.spring4.aop.RetryHandlerClassPointcut;
import com.github.smartretry.spring4.aop.RetryHandlerMethodInterceptor;
import com.github.smartretry.spring4.aop.RetryHandlerMethodPointcut;
import com.github.smartretry.spring4.support.RetryConditional;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryAutoConfiguration {

    @Bean
    @RetryConditional(missingBeanType = RetryTaskMapper.class)
    public RetryTaskMapper defaultRetryTaskMapper(BeanFactory beanFactory) {
        //优先取自定义的DataSource，否则从容器中获取一个DataSource
        DataSource dataSource;
        if (beanFactory.containsBean(BeanConstants.DEFAULT_DATASOURCE)) {
            dataSource = beanFactory.getBean(BeanConstants.DEFAULT_DATASOURCE, DataSource.class);
        } else {
            dataSource = beanFactory.getBean(DataSource.class);
        }
        return new JdbcRetryTaskMapper(dataSource);
    }

    @Bean
    public RetryAnnotationBeanPostProcessor retryAnnotationBeanPostProcessor() {
        return new RetryAnnotationBeanPostProcessor();
    }

    @Bean
    public DefaultPointcutAdvisor matchMethodPointcutAdvisor() {
        return new DefaultPointcutAdvisor(new RetryHandlerMethodPointcut(), new RetryHandlerMethodInterceptor());
    }

    @Bean
    public DefaultPointcutAdvisor matchClassPointcutAdvisor() {
        return new DefaultPointcutAdvisor(new RetryHandlerClassPointcut(), new RetryHandlerClassInterceptor());
    }
}
