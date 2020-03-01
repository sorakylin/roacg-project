package com.roacg.core.base.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

final public class RoLoggerFactory {

    private static final Table<RoCommonLoggerEnum, String, Logger> LOGGER_CACHE = HashBasedTable.create();


    public static Logger getCommonLogger(RoCommonLoggerEnum loggerEnum, String identifier) {

        Assert.notNull(loggerEnum, "Parameter (RoCommonLoggerEnum loggerEnum) cannot be null!");
        Assert.notNull(identifier, "Parameter (String identifier) cannot be null!");

        if (LOGGER_CACHE.contains(loggerEnum, identifier)) {
            return LOGGER_CACHE.get(loggerEnum, identifier);
        }


        //得到环境
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        //控制台输出
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(loggerContext);

        //设置日志输出模板
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} | common-log | [%thread] %-5level %logger{36} >> %msg%n");
        encoder.setCharset(Charsets.UTF_8);
        encoder.setContext(loggerContext);
        encoder.start();


        consoleAppender.setEncoder(encoder);

        consoleAppender.start();

        ch.qos.logback.classic.Logger log = loggerContext.getLogger(String.format("%s[%s]", loggerEnum.getLoggerName(),identifier));

        log.setAdditive(false);
        log.setLevel(Level.INFO);
        log.addAppender(consoleAppender);
//
//        StatusPrinter.print(loggerContext);

        log.info("Log build success.");
        LOGGER_CACHE.put(loggerEnum, identifier, log);
        return log;
    }

    /*
    public Logger getCommonLogger(){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        RollingFileAppender rfAppender = new RollingFileAppender();
        rfAppender.setContext(loggerContext);
        rfAppender.setFile("testFile.log");
        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);

        // rolling policies need to know their parent
        // it's one of the rare cases, where a sub-component knows about its parent
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern("testFile.%i.log.zip");
        rollingPolicy.start();

        SizeBasedTriggeringPolicy triggeringPolicy = new ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy();
        triggeringPolicy.setMaxFileSize("5MB");
        triggeringPolicy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%-4relative [%thread] %-5level %logger{35} - %msg%n");
        encoder.start();

        rfAppender.setEncoder(encoder);
        rfAppender.setRollingPolicy(rollingPolicy);
        rfAppender.setTriggeringPolicy(triggeringPolicy);

        rfAppender.start();

        // attach the rolling file appender to the logger of your choice
        Logger logbackLogger = loggerContext.getLogger("Main");
        logbackLogger.addAppender(rfAppender);

        // OPTIONAL: print logback internal status messages
        StatusPrinter.print(loggerContext);

        // log something
        logbackLogger.debug("hello");
    }
    */
}
