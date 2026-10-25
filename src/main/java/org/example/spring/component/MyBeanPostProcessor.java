package org.example.spring.component;

import org.example.spring.annotation.Component;
import org.example.spring.processor.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor{
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("MyBeanPostProcessor postProcessBeforeInitialization beanName: " + beanName + ", " + bean.getClass());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("MyBeanPostProcessor postProcessAfterInitialization beanName: " + beanName + ", " + bean.getClass());

        if ("smartDog".equals(beanName)) {
            Object proxyInstance = Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    bean.getClass().getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                            Object result = null;
                            try {
                                if ("getSum".equals(method.getName())) {
                                    SmartableAspect.showBeginLog();
                                    result = method.invoke(bean, args);
                                    SmartableAspect.showSuccessEndLog();
                                } else {
                                    result = method.invoke(bean, args);
                                }
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalArgumentException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            } finally {
                            }

                            return result;
                        }
                    }
            );
            return proxyInstance;
        }

        return bean;
    }
}
