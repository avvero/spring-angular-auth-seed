package com.avvero.services.social;

import com.avvero.domain.User;
import com.avvero.services.social.client.vk.OAuth2FlowVkBuilder;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Провайдер социального сервиса vk
 *
 * @author fxdev-belyaev-ay
 */
@Service
public class VkSocialProvider extends ASocialProvider {

    @Autowired
    private Environment env;

    @Override
    public OAuth2CodeGrantFlow getOAuth2CodeGrantFlow() {
        ClientIdentifier clientIdentifier = new ClientIdentifier(
                env.getRequiredProperty("social.vk.oauth.clientId"),
                env.getRequiredProperty("social.vk.oauth.clientSecret"));
        OAuth2CodeGrantFlow flow = OAuth2FlowVkBuilder.getVkAuthorizationBuilder(clientIdentifier,
                env.getRequiredProperty("social.vk.oauth.redirectURI"))
                .scope(env.getRequiredProperty("social.vk.oauth.scope"))
                .property(OAuth2CodeGrantFlow.Phase.AUTHORIZATION, "display", env.getRequiredProperty("social.vk.oauth.display")).build();
        return flow;
    }

    @Override
    public String getUserInfoURL() {
        return env.getRequiredProperty("social.vk.oauth.userInfoURL");
    }

    /**
     * Парсим JSON
     *
     * @param json
     * @return
     */
    protected User parseUserInfo(TokenResult tokenResult, JSONObject json) {
        if (json.has("error")) {
            json.getJSONObject("error").getString("error_code");
            json.getJSONObject("error").getString("error_msg");
            throw new AccessDeniedException("Неправильный логин или пароль");
        } else {
            JSONObject info = json.getJSONArray("response").getJSONObject(0);
            User user = new User();
            user.setId(String.valueOf(info.getInt("id")));
            user.setFirstName(getIfHas(info, "first_name"));
            user.setLastName(getIfHas(info, "last_name"));
            user.setEmail((String) tokenResult.getAllProperties().get("email"));
            user.setPicture(getIfHas(info, "photo_200"));
            user.setLink(getIfHas(info, "link"));
            return user;
        }
    }

    private String getIfHas(JSONObject json, String name) {
        return json.has(name) ? json.getString(name) : null;
    }
}
