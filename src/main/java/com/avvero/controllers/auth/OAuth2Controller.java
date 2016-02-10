package com.avvero.controllers.auth;

import com.avvero.domain.User;
import com.avvero.services.social.ASocialProvider;
import com.avvero.services.social.SocialProviderFactory;
import javafx.util.Pair;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Контроллер авторизациий
 *
 * @author fxdev-belyaev-ay
 */
@RestController
public class OAuth2Controller {

    @Autowired
    private SocialProviderFactory socialProviderFactory;

    @RequestMapping(value = "/oauth2/start")
    public Pair oauth2Process(@RequestParam(value = "provider", required = false) String providerName,
                                HttpServletRequest request, Principal principal) throws Exception {
        Assert.isNull(principal, "You are authorized already");
        Assert.hasText(providerName, "Provider name must be specified");
        // Если НЕ авторизован
        ASocialProvider provider = socialProviderFactory.getSocialProvider(providerName);
        String url = provider.authStart(request);
        return new Pair("url", url);
    }

    @RequestMapping(value = "/oauth2/finish")
    public String oauth2Authorize(@RequestParam(value = "provider", required = false) String providerName,
                                  HttpServletRequest request, Principal principal) throws Exception {
        Assert.isNull(principal, "You are authorized already");
        Assert.hasText(providerName, "Provider name must be specified");
        // Если НЕ авторизован
        ASocialProvider provider = socialProviderFactory.getSocialProvider(providerName);
        TokenResult tokenResult = provider.authFinish(request);
        User user = provider.getUserInfo(tokenResult);
        // Если
        return user.getEmail();
    }

}
