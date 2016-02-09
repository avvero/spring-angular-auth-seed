/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.avvero.services.social.client.vk;

import com.avvero.services.social.client.AuthCodeGrantImpl;
import jersey.repackaged.com.google.common.collect.Maps;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Class that provides methods to build {@link org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow} pre-configured for usage
 * with Facebook provider.
 *
 * @author Miroslav Fuksa (miroslav.fuksa at oracle.com)
 * @since 2.3
 */
public class OAuth2FlowVkBuilder {

    /**
     * Get a builder that can be directly used to perform Authorization Code Grant flow defined by
     * Facebook documentation.
     *
     * @param clientIdentifier Client identifier.
     * @param redirectUri      Redirect URI
     * @return Builder instance.
     */
    public static OAuth2CodeGrantFlow.Builder getVkAuthorizationBuilder(ClientIdentifier clientIdentifier,
                                                                        String redirectUri) {

        final AuthCodeGrantImpl.Builder builder = new AuthCodeGrantImpl.Builder();
        builder.accessTokenUri("https://oauth.vk.com/access_token");
        builder.authorizationUri("https://oauth.vk.com/authorize");
        builder.redirectUri(redirectUri);
        builder.clientIdentifier(clientIdentifier);
        Client client = ClientBuilder.newClient();
        client.register(VkTokenMessageBodyReader.class);
        builder.client(client);

        return builder;
    }


    @Consumes("application/json")
    static class VkTokenMessageBodyReader implements MessageBodyReader<TokenResult> {

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type.equals(TokenResult.class);
        }

        @Override
        public TokenResult readFrom(Class<TokenResult> type, Type genericType, Annotation[] annotations,
                                    MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                                    InputStream entityStream) throws IOException, WebApplicationException {

            Map<String, Object> map = Maps.newHashMap();
            String str = ReaderWriter.readFromAsString(entityStream, mediaType);
            JSONObject json = new JSONObject(str);
            map.put("access_token", json.getString("access_token"));
            map.put("expires_in", json.get("expires_in"));
            map.put("user_id", json.get("expires_in"));
            map.put("email", json.getString("email"));
            return new TokenResult(map);
        }
    }
}
