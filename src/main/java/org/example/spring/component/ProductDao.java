package org.example.spring.component;

import org.example.spring.annotation.Component;

@Component(value = "productDao")
public class ProductDao {
    public void sayHello() {
        System.out.println("ProductDao Hello...");
    }
}
