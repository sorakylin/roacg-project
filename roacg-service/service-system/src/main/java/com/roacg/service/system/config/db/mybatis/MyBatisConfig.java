package com.roacg.service.system.config.db.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = {"com.roacg.service.system.**.dao"})
public class MyBatisConfig {

    @Value("${mybatis.mapper-locations}")
    public String mapperLocations;
//
//
//    /**
//     * 设置默认的枚举处理器
//     *
//     * @return
//     */
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return (config) -> config.getTypeHandlerRegistry().setDefaultEnumTypeHandler(AutoEnumTypeHandler.class);
//    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        // 设置配置文件及mapper文件地址
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        factory.setConfigLocation(resolver.getResource(configLocation));
        factory.setMapperLocations(resolver.getResources(mapperLocations));
        factory.setTypeHandlersPackage(CodeEnumTypeHandler.class.getPackageName());

        return factory.getObject();
    }
}
