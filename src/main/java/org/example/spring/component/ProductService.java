package org.example.spring.component;

import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.Scope;

@Component
@Scope(value = "prototype")
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public void sayHello() {
        productDao.sayHello();
        System.out.println("ProductService Hello...");
    }
}
