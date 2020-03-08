package com.roacg.service.tc.config.db.jpa;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

@EnableJpaRepositories
@Configuration
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(false);
        Map<String, Object> jpaPropertyMap = vendorAdapter.getJpaPropertyMap();

        //自动更新
        jpaPropertyMap.put(AvailableSettings.HBM2DDL_AUTO, Action.UPDATE);
        //是否显示SQL
        jpaPropertyMap.put(AvailableSettings.SHOW_SQL, false);
        //命名策略
        jpaPropertyMap.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class);
        //数据库方言
        jpaPropertyMap.put(AvailableSettings.DIALECT, org.hibernate.dialect.MySQL5Dialect.class);
//        AvailableSettings.IMPLICIT_NAMING_STRATEGY

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.roacg.service.tc.**.model");
        factory.setDataSource(dataSource);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }
}
