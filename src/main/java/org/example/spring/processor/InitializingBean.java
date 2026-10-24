package org.example.spring.processor;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
