package com.github.smartretry.spring4;

import com.github.smartretry.spring4.admin.model.JobStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author yuni[mn960mn@163.com]
 */
@Setter
@Getter
@Slf4j
public class RetrySchedulerFactoryBean extends SchedulerFactoryBean {

    protected String jobName;

    protected String jobIdentity;

    protected String jobPeriod;

    protected String jobGroup;

    protected JobStatusEnum jobStatusEnum = JobStatusEnum.INIT;

    protected Trigger trigger;

    @Override
    public void setTriggers(Trigger... triggers) {
        super.setTriggers(triggers);
        this.trigger = triggers[0];
    }

    @Override
    public void stop() throws SchedulingException {
        super.stop();
        jobStatusEnum = JobStatusEnum.STOPED;
    }

    /**
     * 立即启动
     */
    public void startNow() {
        try {
            this.getObject().start();
        } catch (SchedulerException e) {
            throw new SchedulingException("Could not start Quartz Scheduler", e);
        }
        jobStatusEnum = JobStatusEnum.RUNNING;
    }

    /**
     * 立即执行
     */
    public void runAsync() {
        new Thread(() -> {
            try {
                this.getObject().triggerJob(trigger.getJobKey());
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                throw new SchedulingException("Could not execute Quartz Scheduler", e);
            }
        }).start();
    }

    @Override
    protected void startScheduler(final Scheduler scheduler, final int startupDelay) throws SchedulerException {
        if (startupDelay <= 0) {
            scheduler.start();
        } else {
            Thread schedulerThread = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(startupDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                try {
                    scheduler.start();
                } catch (SchedulerException ex) {
                    log.error(ex.getMessage(), ex);
                    throw new SchedulingException("Could not start Quartz Scheduler after delay", ex);
                }
                jobStatusEnum = JobStatusEnum.RUNNING;
            });
            schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }
    }

    public LocalDateTime getNextTime() {
        GroupMatcher<TriggerKey> triggerKey = GroupMatcher.groupEndsWith(jobGroup);

        Scheduler sch = this.getScheduler();
        try {
            Set<TriggerKey> tks = sch.getTriggerKeys(triggerKey);
            if (!tks.isEmpty()) {
                return toLocalDateTime(sch.getTrigger(tks.iterator().next()).getNextFireTime());
            }
        } catch (SchedulerException e) {
            throw new SchedulingException(e.getMessage(), e);
        }
        return null;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
