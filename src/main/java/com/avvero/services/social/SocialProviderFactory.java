package com.avvero.services.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fxdev-belyaev-ay
 */
@Service
public class SocialProviderFactory {

    @Autowired
    private VkSocialProvider vkSocialProvider;

    public ASocialProvider getSocialProvider(String name) throws Exception {
        switch (name) {
            case "vk":
                return vkSocialProvider;
        }
        throw new Exception("Указанный социальный сервис не поддерживается");
    }

}
