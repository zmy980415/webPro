package com.example.demo;

import com.example.demo.entity.User;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;

@Controller
public class DemoController {
    @Mapping("/hello")
    public String hello(@Param(defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

    @Mapping("/user")
    public User user(){
        return new User("name","12");
    }
}