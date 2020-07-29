package com.ezpizee.aem.services.impl;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.Endpoints;
import com.ezpizee.aem.utils.HostName;
import com.ezpizee.aem.utils.Token;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

@Service
@Component
public class AccessTokenImpl implements AccessToken {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Token token;

    @Reference
    private AppConfig appConfig;

    public String get() {return token != null ? token.getBearerToken() : StringUtils.EMPTY;}

    public void refresh(String key, HttpSession httpSession) {
        if (appConfig != null && appConfig.isValid() && token != null && token.timeToRefresh()) {
            String endpoint = HostName.getAPIServer(appConfig.getEnv()) + Endpoints.refreshToken(token.getTokenId(), token.getUserId());
            Client client = new Client(appConfig);
            Response response = client.post(endpoint);
            keepToken(response, key, httpSession);
        }
    }

    public void load(String key, HttpSession httpSession) {
        if (appConfig != null && appConfig.isValid() && StringUtils.isNotEmpty(key)) {
            Object val = httpSession.getAttribute(key);
            if (val != null) {
                token = new Token(val.toString());
            }
            if (token == null || token.isExpired()) {
                Client client = new Client(appConfig);
                Response response = client.getAccessToken(HostName.getAPIServer(appConfig.getEnv()) + Endpoints.token());
                keepToken(response, key, httpSession);
            }
        }
    }

    public void destroy() {token = null;}

    public void keepToken(Response response, String key, HttpSession httpSession) {
        if (response.isNotError()) {
            token = new Token(response.getDataAsJsonObject());
            httpSession.setAttribute(key, response.getDataAsJsonObject());
        }
        else {
            logger.debug(response.toString());
        }
    }
}

