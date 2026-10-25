package org.example.spring.ioc;

import org.apache.commons.lang.StringUtils;
import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.ComponentScan;
import org.example.spring.annotation.Scope;
import org.example.spring.processor.BeanPostProcessor;
import org.example.spring.processor.InitializingBean;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private ArrayList<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;

        initBeanDefinitionMapByScanPackage();
        // System.out.println("bean definition map: " + beanDefinitionMap);

        Enumeration<String> keys = beanDefinitionMap.keys();
        while (keys.hasMoreElements()) {
            String beanName = keys.nextElement();
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                singletonObjects.put(beanName, createBean(beanName, beanDefinition));
            }
        }

    }

    private void initBeanDefinitionMapByScanPackage() {
        ComponentScan componentScanAnnotation = (ComponentScan)this.configClass.getDeclaredAnnotation(ComponentScan.class);
        String scanPackage = componentScanAnnotation.value();
        System.out.println("scanPackage = " + scanPackage); // scanPackage = org.example.spring.component
        scanPackage = scanPackage.replace(".", "/");

        ClassLoader classLoader = this.configClass.getClassLoader();
        URL url = classLoader.getResource(scanPackage);
        File file = new File(url.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                // System.out.println("f = " + f.getName());
                if (f.getName().endsWith(".class")) {
                    String className = f.getName().replace(".class", "");
                    scanPackage = scanPackage.replace("/", ".");
                    String classFullPath = scanPackage + "." + className;

                    BeanDefinition beanDefinition = new BeanDefinition();
                    try {
                        Class<?> clazz = classLoader.loadClass(classFullPath);
                        beanDefinition.setClazz(clazz);

                        if (clazz.isAnnotationPresent(Component.class)) {
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanId = componentAnnotation.value();
                            if (!"".equals(beanId)) {
                                className = beanId;
                            } else {
                                className = StringUtils.uncapitalize(className);
                            }

                            // System.out.println("clazz.isAssignableFrom(BeanPostProcessor.class): " + clazz.isAssignableFrom(BeanPostProcessor.class)); // 为啥是 false?
                            // if (clazz.isAssignableFrom(BeanPostProcessor.class)) { // 错误写法
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) { // `A.isAssignableFrom(B)` 含义：B 是不是 A 的子类 / 实现类，或者 B == A
                                beanPostProcessorList.add((BeanPostProcessor) createBean(className, beanDefinition));
                                continue;
                            }

                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                String scope = scopeAnnotation.value();
                                if (!"".equals(scope)) {
                                    beanDefinition.setScope(scope);
                                }
                            } else {
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(className, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Autowired autowiredAnnotation = declaredField.getAnnotation(Autowired.class);
                    if (autowiredAnnotation.required()) {
                        declaredField.setAccessible(true);
                        declaredField.set(instance, getBean(declaredField.getName()));
                    }
                }
            }

            System.out.println("================create instance: " + instance + "================");

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                Object current = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
                if (current != null) {
                    instance = current;
                }
            }

            if (instance instanceof InitializingBean) {
                try {
                    ((InitializingBean) instance).afterPropertiesSet();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                Object current = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
                if (current != null) {
                    instance = current;
                }
            }

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // return null;
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                return singletonObjects.get(beanName);
            } else {
                return createBean(beanName, beanDefinition);
            }
        }
        throw new NullPointerException("not found bean: " + beanName);
    }
}
