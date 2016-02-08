package com.avvero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Created by fxdev-belyaev-ay on 08.02.2016.
 */
@EnableAutoConfiguration
public class AppMain {

    public static void main(String args[]) throws Throwable {
        SpringApplication.run(AppMain.class, args);
    }

}
