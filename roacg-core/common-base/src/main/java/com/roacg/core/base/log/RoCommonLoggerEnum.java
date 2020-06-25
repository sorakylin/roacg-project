package com.roacg.core.base.log;

public enum RoCommonLoggerEnum {

    //预加载时打印的日志
    AT_STARTUP_PRELOAD("AT_STARTUP_PRELOAD", "startup-pre.log"),
    //安全相关的日志 例如认证、鉴权等
    SECURITY("SECURITY", "ro-security.log"),
    //请求日志
    REQUEST("REQUEST", "ro-request.log");


    private String loggerName;

    private String logFilePath;

    RoCommonLoggerEnum(String loggerName, String logFilePath) {
        this.loggerName = loggerName;
        this.logFilePath = logFilePath;
    }


    public String getLoggerName() {
        return loggerName;
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}
