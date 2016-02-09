package com.avvero.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fxdev-belyaev-ay on 08.02.2016.
 */
@RestController
public class HomeController {

    @RequestMapping("/hello")
    public String home() {
        return "Hello World!";
    }

}
