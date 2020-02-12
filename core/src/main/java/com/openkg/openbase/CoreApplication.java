package com.openkg.openbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class CoreApplication implements WebMvcConfigurer {
    private final static Logger LOGGER = LoggerFactory.getLogger(CoreApplication.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        //registry.addResourceHandler("/**").addResourceLocations("/resources/static/");
        LOGGER.info(String.format("自定义静态资源目录映射 : %s", "/resources/static/"));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        LOGGER.info(String.format("设置主页 : %s", "/resources/static/"));
        registry.addViewController("/").setViewName("forward:" + "/index.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        WebMvcConfigurer.super.addViewControllers(registry);
    }

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
