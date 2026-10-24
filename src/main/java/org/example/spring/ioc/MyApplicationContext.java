package org.example.spring.ioc;

import org.apache.commons.lang.StringUtils;
import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.ComponentScan;
import org.example.spring.annotation.Scope;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;

        initBeanDefinitionMapByScanPackage();
        // System.out.println("bean definition map: " + beanDefinitionMap);

        Enumeration<String> keys = beanDefinitionMap.keys();
        while (keys.hasMoreElements()) {
            String beanName = keys.nextElement();
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                singletonObjects.put(beanName, createBean(beanDefinition));
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
                System.out.println("f = " + f.getName());
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

    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.newInstance();
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
                return createBean(beanDefinition);
            }
        }
        throw new NullPointerException("not found bean: " + beanName);
    }
}
