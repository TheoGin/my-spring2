package org.example.spring.component;

import org.example.spring.annotation.AfterReturning;
import org.example.spring.annotation.Aspect;
import org.example.spring.annotation.Before;
import org.example.spring.annotation.Component;

@Component
@Aspect
public class SmartableAspect {
    @Before(value = "execution(* org.example.spring.component.Smartable.getSum(..))")
    public static void showBeginLog() {
        System.out.println("前置通知");
    }

    @AfterReturning(value = "execution(* org.example.spring.component.Smartable.getSum(..))")
    public static void showSuccessEndLog() {
        System.out.println("返回通知");
    }
}
