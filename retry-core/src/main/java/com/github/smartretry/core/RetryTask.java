package com.github.smartretry.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author yuni[mn960mn@163.com]
 */
@Setter
@Getter
@ToString
public class RetryTask {

    /**
     * 初始化
     */
    public static final int STATUS_INIT = 1;

    /**
     * 执行完成
     */
    public static final int STATUS_SUCCESS = 2;

    /**
     * 异常
     */
    public static final int STATUS_EXCEPTION = 3;

    private Long taskId;

    private String identity;

    private String params;

    private int status;

    private int retryCount;

    private String remark;

    private LocalDateTime createDate;

    private LocalDateTime editDate;
}
