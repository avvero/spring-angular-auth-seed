package com.avvero.services.social;

import com.avvero.domain.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author fxdev-belyaev-ay
 */
public abstract class ASocialProvider {

    private static final Logger logger = Logger.getLogger(ASocialProvider.class);

    public static final String AUTH2_CODE_GRANT_FLOW = "AUTH2_CODE_GRANT_FLOW";

    public abstract OAuth2CodeGrantFlow getOAuth2CodeGrantFlow();

    public abstract String getUserInfoURL();

    protected abstract User parseUserInfo(TokenResult tokenResult, JSONObject json);

    public String authStart(HttpServletRequest request) {
        OAuth2CodeGrantFlow auth2CodeGrantFlow = getOAuth2CodeGrantFlow();
        request.getSession().setAttribute(AUTH2_CODE_GRANT_FLOW, auth2CodeGrantFlow);
        String url = auth2CodeGrantFlow.start();
        return url;
    }

    public TokenResult authFinish(HttpServletRequest request) {
        OAuth2CodeGrantFlow auth2CodeGrantFlow = (OAuth2CodeGrantFlow) request.getSession()
                .getAttribute(AUTH2_CODE_GRANT_FLOW);
        request.getSession().removeAttribute(AUTH2_CODE_GRANT_FLOW);
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        TokenResult tokenResult = auth2CodeGrantFlow.finish(code, state);
        return tokenResult;
    }

    public User getUserInfo(TokenResult tokenResult) throws Exception {
        String accessToken = tokenResult.getAccessToken();
        String requestString = String.format(getUserInfoURL(), accessToken);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(requestString);
            getRequest.addHeader("accept", "application/json");
            HttpResponse response;
            response = httpClient.execute(getRequest);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONObject json = new JSONObject(responseString);
            return parseUserInfo(tokenResult, json);
        } catch (IOException e) {
            logger.error("Не удалось получить информацию по клиенту социального сервиса", e);
            throw new Exception("Не удалось получить информацию");
        }
    }

}
