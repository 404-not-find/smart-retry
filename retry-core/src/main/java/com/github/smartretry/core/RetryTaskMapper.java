package com.github.smartretry.core;

import java.util.List;

/**
 * @author yuni[mn960mn@163.com]
 */
public interface RetryTaskMapper {

    int insert(RetryTask retryTask);

    int update(RetryTask retryTask);

    List<RetryTask> queryNeedRetryTaskList(String identity, int retryCount, int initialDelay);
}
