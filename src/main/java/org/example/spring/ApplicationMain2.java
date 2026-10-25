package org.example.spring;

import org.example.spring.component.ProductService;
import org.example.spring.component.Smartable;
import org.example.spring.ioc.MyApplicationContext;
import org.example.spring.ioc.MySpringConfig;

public class ApplicationMain2 {
    public static void main(String[] args) {
        MyApplicationContext ioc = new MyApplicationContext(MySpringConfig.class);

        // test aop
        Smartable smartable = (Smartable)ioc.getBean("smartDog");
        smartable.getSum(10, 2);
        smartable.getSub(10, 2);

        /* // test InitializingBean 初始化方法
        ProductService productService = (ProductService) ioc.getBean("productService"); */
        /* // test Autowired
        ProductService productService = (ProductService) ioc.getBean("productService");
        productService.sayHello(); */

        /* // test scope
        System.out.println(ioc.getBean("productDao"));
        System.out.println(ioc.getBean("productDao"));
        System.out.println("---------------------");
        System.out.println(ioc.getBean("productService"));
        System.out.println(ioc.getBean("productService"));
        System.out.println(ioc.getBean("productService")); */
    }
}