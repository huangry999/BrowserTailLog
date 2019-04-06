package com.log.uiapi.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext context() {
        return context;
    }

    public static <T> T get(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getProperty(String key, Class<T> clazz) {
        return context.getEnvironment().getProperty(key, clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
