package com.github.smartretry.spring4;

/**
 * bean名称
 *
 * @author yuni[mn960mn@163.com]
 */
public class BeanConstants {

    /**
     * 自定义retry数据源的bean名称
     */
    public static final String DEFAULT_DATASOURCE = "defaultRetryHandlerDataSource";

    /**
     * 自定义retry quartz job线程池的bean名称
     */
    public static final String DEFAULT_RETRY_TASKEXECUTOR = "defaultRetryTaskExecutor";
}