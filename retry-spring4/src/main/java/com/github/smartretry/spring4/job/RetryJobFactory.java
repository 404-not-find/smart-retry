package com.github.smartretry.spring4.job;

import com.github.smartretry.spring4.JobConstant;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryJobFactory implements JobFactory {

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        return (Job) bundle.getJobDetail().getJobDataMap().get(JobConstant.JOB_INSTANCE_KEY);
    }
}