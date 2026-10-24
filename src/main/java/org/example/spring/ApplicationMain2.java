package org.example.spring;

import org.example.spring.component.ProductService;
import org.example.spring.ioc.MyApplicationContext;
import org.example.spring.ioc.MySpringConfig;

public class ApplicationMain2 {
    public static void main(String[] args) {
        MyApplicationContext ioc = new MyApplicationContext(MySpringConfig.class);

        // test Autowired
        ProductService productService = (ProductService) ioc.getBean("productService");
        productService.sayHello();

        /* // test scope
        System.out.println(ioc.getBean("productDao"));
        System.out.println(ioc.getBean("productDao"));
        System.out.println("---------------------");
        System.out.println(ioc.getBean("productService"));
        System.out.println(ioc.getBean("productService"));
        System.out.println(ioc.getBean("productService")); */
    }
}