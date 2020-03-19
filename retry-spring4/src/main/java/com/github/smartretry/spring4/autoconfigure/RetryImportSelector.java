package com.github.smartretry.spring4.autoconfigure;

import com.github.smartretry.spring4.EnvironmentConstants;
import com.github.smartretry.spring4.EnableRetrying;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * 根据配置来决定是否进行自动配置
 *
 * @author yuni[mn960mn@163.com]
 */
public class RetryImportSelector implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (retryEnabled()) {
            AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);

            Map<String, Object> annotationData = importingClassMetadata.getAnnotationAttributes(EnableRetrying.class.getName());
            Object proxyTargetClass = annotationData.get("proxyTargetClass");
            if (Boolean.parseBoolean(proxyTargetClass.toString())) {
                AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
            }

            registry.registerBeanDefinition(RetryAutoConfiguration.class.getName(), new RootBeanDefinition(RetryAutoConfiguration.class));

            if (retryWebEnabled()) {
                registry.registerBeanDefinition(RetryWebAutoConfiguration.class.getName(), new RootBeanDefinition(RetryWebAutoConfiguration.class));
            }
        }
    }

    private boolean retryEnabled() {
        return Boolean.parseBoolean(environment.getProperty(EnvironmentConstants.RETRY_ENABLED, "true"));
    }

    private boolean retryWebEnabled() {
        return Boolean.parseBoolean(environment.getProperty(EnvironmentConstants.RETRY_WEB_ENABLED, "true"));
    }
}
