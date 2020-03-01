package com.roacg.service.system.config.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value = {"com.roacg.service.system.rouser.dao"})
public class MyBatisConfig {

}
