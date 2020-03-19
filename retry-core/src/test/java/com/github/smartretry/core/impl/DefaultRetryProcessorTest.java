package com.github.smartretry.core.impl;

import com.github.smartretry.core.RetrySerializer;
import com.github.smartretry.core.RetryTask;
import com.github.smartretry.core.RetryTaskMapper;
import com.github.smartretry.core.support.GenericRetryHandler;
import com.github.smartretry.core.util.ServiceLoaderUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceLoaderUtils.class})
public class DefaultRetryProcessorTest {

    private DefaultRetryProcessor defaultRetryProcessor;

    private RetryTaskMapper retryTaskMapper;

    private GenericRetryHandler retryHandler;

    @Before
    public void setup() {
        this.retryTaskMapper = PowerMockito.mock(RetryTaskMapper.class);
        this.retryHandler = PowerMockito.mock(GenericRetryHandler.class);

        RetrySerializer retrySerializer = PowerMockito.mock(RetrySerializer.class);
        this.defaultRetryProcessor = new DefaultRetryProcessor(retryHandler, retryTaskMapper, retrySerializer);

        when(retryHandler.identity()).thenReturn("user.order");
        when(retryHandler.maxRetryCount()).thenReturn(5);
        when(retryHandler.initialDelay()).thenReturn(300);
        when(retryHandler.handle(anyObject())).thenThrow(new RuntimeException("mock exception"));

        PowerMockito.mockStatic(ServiceLoaderUtils.class);
    }

    @Test
    public void testDoRetryWithIgnoreException() {
        when(retryHandler.ignoreException()).thenReturn(true);
        when(retryTaskMapper.queryNeedRetryTaskList(retryHandler.identity(), retryHandler.maxRetryCount(), retryHandler.initialDelay())).thenReturn(newRetryTaskList());
        defaultRetryProcessor.doRetry();
    }

    @Test(expected = RuntimeException.class)
    public void testDoRetryWithNoIgnoreException() {
        when(retryHandler.ignoreException()).thenReturn(false);
        when(retryTaskMapper.queryNeedRetryTaskList(retryHandler.identity(), retryHandler.maxRetryCount(), retryHandler.initialDelay())).thenReturn(newRetryTaskList());
        defaultRetryProcessor.doRetry();
    }

    private List<RetryTask> newRetryTaskList() {
        List<RetryTask> tasks = new ArrayList<>();
        RetryTask task1 = new RetryTask();
        task1.setTaskId(1L);
        task1.setRetryCount(5);
        task1.setCreateDate(LocalDateTime.now());
        task1.setStatus(1);
        task1.setIdentity("user.order");

        RetryTask task2 = new RetryTask();
        task2.setTaskId(1L);
        task2.setRetryCount(5);
        task2.setCreateDate(LocalDateTime.now());
        task2.setStatus(1);
        task2.setIdentity("user.order");

        tasks.add(task1);
        tasks.add(task2);
        return tasks;
    }
} 
