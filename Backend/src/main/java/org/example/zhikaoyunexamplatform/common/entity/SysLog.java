package org.example.zhikaoyunexamplatform.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志实体
 */
@Data
public class SysLog {

    private Long        id;
    private Long        userId;
    private String      username;
    private String      operation;
    private String      method;
    private String      url;
    private String      params;
    private Integer     result;
    private String      errorMsg;
    private String      ip;
    private String      userAgent;
    private Integer     responseTime;
    private LocalDateTime createTime;
}
