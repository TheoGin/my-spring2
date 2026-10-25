package org.example.spring.component;

public class SmartableAspect {
    public static void showBeginLog() {
        System.out.println("前置通知");
    }

    public static void showSuccessEndLog() {
        System.out.println("返回通知");
    }
}
