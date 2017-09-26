package com.example;

import classloader.IHelloService;

/**
 * Created by liujun on 17/9/25.
 */

public class HelloService implements IHelloService {
    @Override
    public void sayHello() {
        System.out.println("HelloService sayHello");
    }
}
