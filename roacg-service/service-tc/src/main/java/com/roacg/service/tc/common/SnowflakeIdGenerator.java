package com.roacg.service.tc.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.roacg.core.utils.SnowflakeIdWorker;
import lombok.SneakyThrows;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Properties;

/**
 * 自定义的 Hibernate ID 生成器
 * 雪花算法
 *
 * @see SnowflakeIdWorker
 */
public class SnowflakeIdGenerator implements IdentifierGenerator, Configurable {

    public static final String NAME = "snowflake-id";
    public static final String CLASS_NAME = "com.roacg.service.tc.common.SnowflakeIdGenerator";

    //一般都用这个默认组来生成雪花ID
    private String group = "default";

    //业务线ID 和 机器ID 应该通过配置注入
    private static long bizId;
    private static long workerId;

    public SnowflakeIdGenerator() {
    }

    //缓存不同的组的不同序列
    private static final Cache<String, SnowflakeIdWorker> ID_CACHE = CacheBuilder.newBuilder().build();

    public static void registerMetaData(long bizId, long workerId) {
        SnowflakeIdGenerator.bizId = bizId;
        SnowflakeIdGenerator.workerId = workerId;
    }

    @SneakyThrows
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return ID_CACHE.get(group, () -> new SnowflakeIdWorker(bizId, workerId)).nextId();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        String group = params.getProperty("group");
        if (StringUtils.hasText(group)) {
            this.group = group;
        }
    }
}