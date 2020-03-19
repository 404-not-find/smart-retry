package com.github.smartretry.spring4.support;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryConfigurationCondition implements ConfigurationCondition {

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> map = metadata.getAnnotationAttributes(RetryConditional.class.getName());
        Class<?> missingBeanType = (Class<?>) map.get("missingBeanType");
        if (missingBeanType == null || missingBeanType.equals(Void.class)) {
            return false;
        }
        try {
            context.getBeanFactory().getBean(missingBeanType);
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        }
        return false;
    }
}
