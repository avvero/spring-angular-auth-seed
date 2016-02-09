package com.avvero.controllers.auth;

import com.avvero.domain.User;
import com.avvero.services.social.ASocialProvider;
import com.avvero.services.social.SocialProviderFactory;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Контроллер авторизациий
 *
 * @author fxdev-belyaev-ay
 */
@Controller
@Scope("request")
public class OAuth2Controller {

    @Autowired
    private SocialProviderFactory socialProviderFactory;

    @RequestMapping(value = "/oauth2/process")
    public String oauth2Process(@RequestParam(value = "provider", required = false) String providerName,
                                HttpServletRequest request, Principal principal) throws Exception {
        if (principal != null) return "forward:/profile";
        // Если НЕ авторизован
        ASocialProvider provider = socialProviderFactory.getSocialProvider(providerName);
        String url = provider.authStart(request);
        return "redirect:" + url;
    }

    @Secured("IS_AUTHENTICATED_ANONYMOUSLY")
    @RequestMapping(value = "/oauth2/authorize")
    public String oauth2Authorize(@RequestParam(value = "provider", required = false) String providerName,
                                  HttpServletRequest request, Principal principal) throws Exception {
        if (principal != null) return "forward:/profile";
        // Если НЕ авторизован
        ASocialProvider provider = socialProviderFactory.getSocialProvider(providerName);
        TokenResult tokenResult = provider.authFinish(request);
        User user = provider.getUserInfo(tokenResult);
        // Если
        return user.getEmail();
    }

}
