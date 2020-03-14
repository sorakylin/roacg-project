package com.roacg.service.tc.config.db.jpa;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

@EnableJpaRepositories(basePackages = {"com.roacg.service.tc.**.repository"})
@EnableTransactionManagement
@Configuration
public class JpaConfig {

    // 配置jpa厂商适配器
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        // 设置数据库类型（可使用org.springframework.orm.jpa.vendor包下的Database枚举类）
        jpaVendorAdapter.setDatabase(Database.MYSQL);

        return jpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();


        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.roacg.service.tc.**.model");
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(jpaVendorAdapter);

        Map<String, Object> jpaPropertyMap = factory.getJpaPropertyMap();
        //自动更新
        jpaPropertyMap.put(AvailableSettings.HBM2DDL_AUTO, Action.UPDATE);
        //是否显示SQL
        jpaPropertyMap.put(AvailableSettings.SHOW_SQL, true);
        /**
         * 命名策略
         * org.hibernate.cfg.ImprovedNamingStrategy 下划线，会导致@Column 的name属性失效，
         */
        jpaPropertyMap.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class);
        jpaPropertyMap.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, SpringImplicitNamingStrategy.class);
        //数据库方言
        jpaPropertyMap.put(AvailableSettings.DIALECT, org.hibernate.dialect.MySQL5Dialect.class);

        return factory;
    }

    // 配置jpa事务管理器
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

}
