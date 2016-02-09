package com.avvero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by fxdev-belyaev-ay on 08.02.2016.
 */
@ComponentScan(basePackages = "com.avvero")
@EnableAutoConfiguration
public class AppMain {

    public static void main(String args[]) throws Throwable {
        SpringApplication.run(AppMain.class, args);
    }

}
