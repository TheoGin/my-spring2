package org.example.spring.component;

import org.example.spring.annotation.Component;

@Component
public class SmartDog implements Smartable{
    @Override
    public float getSum(float i, float j) {
        float sum = i + j;
        System.out.println("getSum() 方法内部打印sum= " + sum);
        return sum;
    }

    @Override
    public float getSub(float i, float j) {
        float sub = i - j;
        System.out.println("getSub() 方法内部打印sub= " + sub);
        return sub;
    }
}
