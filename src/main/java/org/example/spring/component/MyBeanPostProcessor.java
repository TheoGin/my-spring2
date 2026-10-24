package org.example.spring.component;

import org.example.spring.processor.BeanPostProcessor;
public class MyBeanPostProcessor implements BeanPostProcessor{
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("MyBeanPostProcessor postProcessBeforeInitialization beanName: " + beanName + ", " + bean.getClass());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("MyBeanPostProcessor postProcessAfterInitialization beanName: " + beanName + ", " + bean.getClass());
        return bean;
    }
}
