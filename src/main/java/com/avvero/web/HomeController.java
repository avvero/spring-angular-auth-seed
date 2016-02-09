package com.avvero.web;

import com.avvero.domain.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by fxdev-belyaev-ay on 08.02.2016.
 */
@RestController
public class HomeController {

    @RequestMapping("/profile")
    public User getProfile(Principal principal) {
        User user = new User();
        return user;
    }

}
