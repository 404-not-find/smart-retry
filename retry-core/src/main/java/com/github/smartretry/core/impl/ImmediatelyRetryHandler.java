package com.github.smartretry.core.impl;

import com.github.smartretry.core.NoRetryException;
import com.github.smartretry.core.RetryContext;
import com.github.smartretry.core.RetryTask;
import com.github.smartretry.core.RetryTaskFactory;
import com.github.smartretry.core.RetryTaskMapper;
import com.github.smartretry.core.support.GenericRetryHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 用于在一个重试任务首次执行时触发。执行任务时，先把参数序列化并保存到数据库
 *
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
class ImmediatelyRetryHandler extends ExecuteRetryHandler {

    private RetryTaskFactory retryTaskFactory;

    private boolean beforeTask;

    public ImmediatelyRetryHandler(GenericRetryHandler genericRetryHandler, RetryTaskFactory retryTaskFactory, RetryTaskMapper retryTaskMapper, boolean beforeTask) {
        super(genericRetryHandler, retryTaskMapper);
        this.retryTaskFactory = retryTaskFactory;
        this.beforeTask = beforeTask;
    }

    @Override
    public Object handle(Object arg) {
        RetryContext retryContext = new RetryContext(genericRetryHandler, arg);
        Object result;
        RetryTask retryTask;
        if (beforeTask) {
            retryTask = retryTaskFactory.create(genericRetryHandler, arg);
            retryTaskMapper.insert(retryTask);
            try {
                result = genericRetryHandler.handle(arg);
                retryContext.setResult(result);
                completeTask(retryTask);
                onRetry(retryContext);
                onComplete(retryContext);
            } catch (NoRetryException e) {
                retryContext.setException(e);
                failureTask(retryTask, retryContext);

                onRetry(retryContext);
                onError(retryContext);
                throw e;
            } catch (RuntimeException e) {
                retryContext.setException(e);

                if (retryContext.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                    //只有最大可重试次数为0，才会执行到这里
                    failureTask(retryTask, retryContext);

                    onRetry(retryContext);
                    onError(retryContext);
                } else {
                    updateRemark(retryTask, e);
                    onRetry(retryContext);
                }

                throw e;
            }
            return result;
        } else {
            try {
                result = genericRetryHandler.handle(arg);
                retryContext.setResult(result);
                onRetry(retryContext);
                onComplete(retryContext);
            } catch (NoRetryException e) {
                retryContext.setException(e);

                onRetry(retryContext);
                onError(retryContext);

                throw e;
            } catch (RuntimeException e) {
                retryContext.setException(e);
                if (retryContext.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                    //只有最大可重试次数为0，才会执行到这里
                    onRetry(retryContext);
                    onError(retryContext);
                } else {
                    //等待重试
                    retryTask = retryTaskFactory.create(genericRetryHandler, arg);
                    retryTask.setRemark(StringUtils.left(e.getMessage(), 1000));
                    retryTaskMapper.insert(retryTask);
                    onRetry(retryContext);
                }

                throw e;
            }
        }
        return result;
    }
}
