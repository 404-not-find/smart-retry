package com.github.smartretry.spring4.job;

import com.github.smartretry.core.RetryProcessor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryJob implements Job {

    private RetryProcessor retryProcessor;

    public RetryJob() {
    }

    public RetryJob(RetryProcessor retryProcessor) {
        this.retryProcessor = retryProcessor;
    }

    @Override
    public void execute(JobExecutionContext context) {
        retryProcessor.doRetry();
    }
}
