package com.github.smartretry.core.impl;

import com.github.smartretry.core.RetryHandler;
import com.github.smartretry.core.RetryProcessor;
import com.github.smartretry.core.RetrySerializer;
import com.github.smartretry.core.RetryTask;
import com.github.smartretry.core.RetryTaskMapper;
import com.github.smartretry.core.support.DefaultRetryHandler;
import com.github.smartretry.core.support.GenericRetryHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
public class DefaultRetryProcessor implements RetryProcessor {

    private GenericRetryHandler genericRetryHandler;

    private RetryTaskMapper retryTaskMapper;

    private RetryedRetryHandler retryedRetryHandler;

    public DefaultRetryProcessor(RetryHandler<Object, Object> retryHandler, RetryTaskMapper retryTaskMapper, RetrySerializer retrySerializer) {
        if (retryHandler instanceof GenericRetryHandler) {
            genericRetryHandler = (GenericRetryHandler) retryHandler;
        } else {
            this.genericRetryHandler = new DefaultRetryHandler(retryHandler);
        }
        this.retryTaskMapper = retryTaskMapper;

        if (retrySerializer == null) {
            this.retryedRetryHandler = new RetryedRetryHandler(genericRetryHandler, retryTaskMapper);
        } else {
            this.retryedRetryHandler = new RetryedRetryHandler(genericRetryHandler, retryTaskMapper, retrySerializer);
        }
    }

    @Override
    public void doRetry() {
        log.info("开始执行Identity={}的重试，maxRetryCount={}, initialDelay={}", genericRetryHandler.identity(), genericRetryHandler.maxRetryCount(), genericRetryHandler.initialDelay());
        List<RetryTask> tasks = retryTaskMapper.queryNeedRetryTaskList(genericRetryHandler.identity(), genericRetryHandler.maxRetryCount(), genericRetryHandler.initialDelay());
        if (tasks == null) {
            return;
        }
        log.info("Identity={}当前有{}个任务准备重试", genericRetryHandler.identity(), tasks.size());
        if (genericRetryHandler.ignoreException()) {
            tasks.forEach(this::doRetryWithIgnoreException);
        } else {
            tasks.forEach(this::doRetry);
        }
    }

    private void doRetryWithIgnoreException(RetryTask retryTask) {
        log.info("开始重试Identity={}，TaskId={}的任务", retryTask.getIdentity(), retryTask.getTaskId());
        retryedRetryHandler.setRetryTask(retryTask);
        String json = retryTask.getParams();
        try {
            if (StringUtils.isBlank(json)) {
                retryedRetryHandler.handle(null);
            } else {
                retryedRetryHandler.parseArgsAndhandle(json);
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doRetry(RetryTask retryTask) {
        log.info("开始重试Identity={}，Id={}的任务", retryTask.getIdentity(), retryTask.getTaskId());
        retryedRetryHandler.setRetryTask(retryTask);
        String json = retryTask.getParams();
        if (StringUtils.isBlank(json)) {
            retryedRetryHandler.handle(null);
        } else {
            retryedRetryHandler.parseArgsAndhandle(json);
        }
    }
}
