package com.florian935.responder.rsocketjwt.utils;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE)
@RequiredArgsConstructor
public class BeanUtils implements ApplicationContextAware {

    static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> classType) {

        return applicationContext.getBean(classType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }
}
