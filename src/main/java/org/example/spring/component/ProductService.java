package org.example.spring.component;

import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.Scope;
import org.example.spring.processor.InitializingBean;

@Component
@Scope(value = "prototype")
public class ProductService implements InitializingBean {

    @Autowired
    private ProductDao productDao;

    public void sayHello() {
        productDao.sayHello();
        System.out.println("ProductService Hello...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("ProductService 初始化方法，可以写业务代码...");
    }
}
