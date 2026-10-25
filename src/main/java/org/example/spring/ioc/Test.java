package org.example.spring.ioc;

import org.example.spring.annotation.AfterReturning;
import org.example.spring.annotation.Aspect;
import org.example.spring.annotation.Before;
import org.example.spring.component.SmartableAspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<SmartableAspect> smartableAspectClass = SmartableAspect.class;
        if (smartableAspectClass.isAnnotationPresent(Aspect.class)) {
            Method[] declaredMethods = smartableAspectClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                System.out.println("Method.getName(): " + declaredMethod.getName());
                if (declaredMethod.isAnnotationPresent(Before.class)) {
                    Before beforeAnnotation = declaredMethod.getAnnotation(Before.class);
                    System.out.println("Before: " + beforeAnnotation.value());
                    declaredMethod.invoke(smartableAspectClass.newInstance(), null);
                } else if (declaredMethod.isAnnotationPresent(AfterReturning.class)) {
                    AfterReturning afterReturningAnnotation = declaredMethod.getAnnotation(AfterReturning.class);
                    System.out.println("AfterReturning: " + afterReturningAnnotation.value());
                    declaredMethod.invoke(smartableAspectClass.newInstance(), null);
                }
            }
        }
    }
}
