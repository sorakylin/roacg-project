package com.roacg.core.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;

public class SecurityAccessPathExtractor implements CommandLineRunner {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    public void run(String... args) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> map = this.handlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> set = map.keySet();
        for (RequestMappingInfo info : set) {

            HandlerMethod handlerMethod = map.get(info);

            // springmvc的url地址，不包含项目名
            PatternsRequestCondition patternsCondition = info.getPatternsCondition();

            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();

            Set<String> patterns = patternsCondition.getPatterns();

            System.out.println(methods);
            System.out.println(patterns);

        }
        System.out.println(111);
    }


}
