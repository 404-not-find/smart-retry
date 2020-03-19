package com.github.smartretry.spring4;

import com.github.smartretry.core.RetryFunction;
import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.RetryHandlerPostProcessor;
import com.github.smartretry.core.RetrySerializer;
import com.github.smartretry.core.RetryTaskMapper;
import com.github.smartretry.core.impl.DefaultRetryHandlerPostProcessor;
import com.github.smartretry.core.impl.DefaultRetryProcessor;
import com.github.smartretry.core.impl.DefaultRetryTaskFactory;
import com.github.smartretry.core.impl.MethodRetryHandler;
import com.github.smartretry.core.listener.RetryListener;
import com.github.smartretry.core.registry.RetryHandlerRegistry;
import com.github.smartretry.core.util.RetryHandlerUtils;
import com.github.smartretry.spring4.admin.model.JobStatusEnum;
import com.github.smartretry.spring4.job.RetryJob;
import com.github.smartretry.spring4.job.RetryJobFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.spi.JobFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.OrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 对所有com.github.smartretry.core.RetryHandler和带有@RetryFunction注解的方法注册为Quartz Job
 *
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
public class RetryAnnotationBeanPostProcessor implements BeanPostProcessor, SmartInitializingSingleton, BeanFactoryAware, EnvironmentAware, DisposableBean {

    private DefaultListableBeanFactory defaultListableBeanFactory;

    private Set<Class<?>> nonAnnotatedClasses = new HashSet<>();

    private RetryTaskMapper retryTaskMapper;

    private RetrySerializer retrySerializer;

    private RetryHandlerPostProcessor<Object, Object> retryHandlerPostProcessor;

    private List<RetryBeanDefinitionBuilderCustomizer> retryBeanDefinitionBuilderCustomizers;

    private AtomicInteger jobNameIndex = new AtomicInteger(0);

    private JobFactory jobFactory = new RetryJobFactory();

    private Executor taskExecutor = Executors.newCachedThreadPool();

    private Boolean beforeTask;

    /**
     * job是否自动启动。如果配置的是false，就需要手工启动
     */
    private boolean jobAutoStartup;

    /**
     * 延迟多少秒之后，再启动job
     */
    private int jobStartupDelay;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;

        this.retryTaskMapper = defaultListableBeanFactory.getBean(RetryTaskMapper.class);

        retryBeanDefinitionBuilderCustomizers = new ArrayList<>(defaultListableBeanFactory.getBeansOfType(RetryBeanDefinitionBuilderCustomizer.class).values());
        retryBeanDefinitionBuilderCustomizers.sort(OrderComparator.INSTANCE);

        if (defaultListableBeanFactory.containsBean(BeanConstants.DEFAULT_RETRY_TASKEXECUTOR)) {
            taskExecutor = defaultListableBeanFactory.getBean(BeanConstants.DEFAULT_RETRY_TASKEXECUTOR, Executor.class);
        }

        if (beforeTask != null) {
            setRetryHandlerPostProcessor();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        beforeTask = Boolean.parseBoolean(environment.getProperty(EnvironmentConstants.RETRY_BEFORETASK, "true"));
        jobAutoStartup = Boolean.parseBoolean(environment.getProperty(EnvironmentConstants.RETRY_JOB_AUTOSTARTUP, "true"));
        jobStartupDelay = Integer.parseInt(environment.getProperty(EnvironmentConstants.RETRY_JOB_STARTUPDELAY, "30"));
        if (retryHandlerPostProcessor == null && retryTaskMapper != null) {
            setRetryHandlerPostProcessor();
        }
    }

    private void setRetryHandlerPostProcessor() {
        retrySerializer = getRetrySerializerFromBeanFactory(defaultListableBeanFactory);
        if (retrySerializer == null) {
            retryHandlerPostProcessor = new DefaultRetryHandlerPostProcessor(retryTaskMapper, beforeTask);
        } else {
            retryHandlerPostProcessor = new DefaultRetryHandlerPostProcessor(new DefaultRetryTaskFactory(retrySerializer), retryTaskMapper, beforeTask);
        }
    }

    private RetrySerializer getRetrySerializerFromBeanFactory(BeanFactory beanFactory) {
        try {
            return beanFactory.getBean(RetrySerializer.class);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopInfrastructureBean) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            Object targetObject = AopProxyUtils.getSingletonTarget(bean);
            if (RetryHandler.class.isAssignableFrom(targetClass)) {
                RetryHandlerUtils.validateRetryHandler(targetClass);
                log.info("发现RetryHandler的实例：{}，准备注册", targetClass);
                registerJobBean((RetryHandler) targetObject);
                return bean;
            }
            ReflectionUtils.MethodFilter methodFilter = method -> method.getAnnotation(RetryFunction.class) != null;
            Set<Method> methods = MethodIntrospector.selectMethods(targetClass, methodFilter);
            methods.forEach(method -> processRetryFunction(targetObject, method));
        }
        return bean;
    }

    protected void processRetryFunction(Object bean, Method method) {
        log.info("发现@RetryFunction的实例：{}，准备注册", method.toString());
        Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
        RetryHandlerUtils.validateRetryFunction(method);

        RetryFunction retryFunction = method.getAnnotation(RetryFunction.class);
        Supplier<RetryListener> retryListenerSupplier = () -> {
            RetryListener retryListener = null;
            String retryListenerName = retryFunction.retryListener();
            if (StringUtils.isNotBlank(retryListenerName)) {
                retryListener = defaultListableBeanFactory.getBean(retryListenerName, RetryListener.class);
            }
            return retryListener;
        };
        registerJobBean(new MethodRetryHandler(bean, invocableMethod, retryFunction, retryListenerSupplier));
    }

    private void registerJobBean(RetryHandler retryHandler) {
        if (retryHandler.identity().length() > 50) {
            throw new IllegalArgumentException("identity=" + retryHandler.identity() + " is too long, it must be less than 50");
        }

        RetryHandler retryHandlerProxy = retryHandlerPostProcessor.doPost(retryHandler);
        RetryHandlerRegistry.registry(retryHandlerProxy);

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobConstant.JOB_INSTANCE_KEY, new RetryJob(new DefaultRetryProcessor(retryHandler, retryTaskMapper, retrySerializer)));

        int index = jobNameIndex.incrementAndGet();

        String group = JobConstant.JOB_GROUP_KEY + "_" + index;
        String name = JobConstant.JOB_NAME_KEY + "_" + index;
        String triggerName = JobConstant.JOB_TRIGGER_NAME_KEY + "_" + index;

        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(RetryJob.class);
        jobDetailFactoryBean.setName(name);
        jobDetailFactoryBean.setGroup(group);
        jobDetailFactoryBean.setJobDataMap(jobDataMap);
        jobDetailFactoryBean.afterPropertiesSet();

        Object jobTrigger;
        String jobPeriod;

        if (StringUtils.isNotBlank(retryHandler.cron())) {
            jobPeriod = retryHandler.cron();

            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            cronTriggerFactoryBean.setCronExpression(retryHandler.cron());
            cronTriggerFactoryBean.setName(triggerName);
            cronTriggerFactoryBean.setGroup(group);
            cronTriggerFactoryBean.setJobDataMap(jobDataMap);
            cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());

            try {
                cronTriggerFactoryBean.afterPropertiesSet();
            } catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }

            jobTrigger = cronTriggerFactoryBean.getObject();
        } else {
            jobPeriod = Integer.toString(retryHandler.interval());

            SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
            simpleTriggerFactoryBean.setRepeatInterval(retryHandler.interval() * 1000L);
            simpleTriggerFactoryBean.setName(triggerName);
            simpleTriggerFactoryBean.setGroup(group);
            simpleTriggerFactoryBean.setJobDataMap(jobDataMap);
            simpleTriggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
            simpleTriggerFactoryBean.afterPropertiesSet();

            jobTrigger = simpleTriggerFactoryBean.getObject();
        }

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(RetrySchedulerFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("taskExecutor", taskExecutor);
        beanDefinitionBuilder.addPropertyValue("triggers", jobTrigger);
        beanDefinitionBuilder.addPropertyValue("autoStartup", jobAutoStartup);
        beanDefinitionBuilder.addPropertyValue("startupDelay", jobStartupDelay);
        beanDefinitionBuilder.addPropertyValue("jobFactory", jobFactory);
        beanDefinitionBuilder.addPropertyValue("jobGroup", group);
        beanDefinitionBuilder.addPropertyValue("jobName", retryHandlerProxy.name());
        beanDefinitionBuilder.addPropertyValue("jobIdentity", retryHandlerProxy.identity());
        beanDefinitionBuilder.addPropertyValue("jobPeriod", jobPeriod);
        if (!jobAutoStartup) {
            beanDefinitionBuilder.addPropertyValue("jobStatusEnum", JobStatusEnum.PREPARE);
        }

        //执行用户自定义的后置处理逻辑
        retryBeanDefinitionBuilderCustomizers.forEach(c -> c.customize(retryHandler.identity(), beanDefinitionBuilder));

        // 注册Bean
        String jobBeanName = "JOB." + index + "." + retryHandler.identity();
        defaultListableBeanFactory.registerBeanDefinition(jobBeanName, beanDefinitionBuilder.getBeanDefinition());
    }

    @Override
    public void afterSingletonsInstantiated() {
        nonAnnotatedClasses.clear();
    }

    @Override
    public void destroy() {
        if (taskExecutor instanceof ExecutorService) {
            ((ExecutorService) taskExecutor).shutdown();
        }
    }
}