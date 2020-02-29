package com.roacg.core.base.spring;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * 使 Spring的 @ConfigurationProperties 注解支持解析 yaml 格式的配置文件
 * <code>
 *
 * @ConfigurationProperties("asd")
 * @PropertySource(value = {"classpath:asd.yml"},, factory = YamlPropertySourceFactory.class)
 * public class Claszz{
 * <p>
 * }
 *
 * </code>
 * Create by skypyb on 2020/02/29
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {

        String sourceName = Optional.ofNullable(name).orElse(resource.getResource().getFilename());

        if (!resource.getResource().exists()) {
            return new PropertiesPropertySource(sourceName, new Properties());
        }

        //直接用Spring的yaml解析器
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();
        Properties ymlProperties = factory.getObject();

        return new PropertiesPropertySource(sourceName, ymlProperties);
    }
}