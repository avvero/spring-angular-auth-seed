package com.avvero.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fxdev-belyaev-ay on 09.02.2016.
 */
@RestController
@RequestMapping("/data")
public class DataController {

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "[key:value]";
    }

}
