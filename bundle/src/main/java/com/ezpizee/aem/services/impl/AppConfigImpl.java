package com.ezpizee.aem.services.impl;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.*;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.ezpizee.aem.Constants.*;
import static com.ezpizee.aem.Constants.KEY_ENV;

@Service
@Component
public class AppConfigImpl implements AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    private static final String KEY_SESSION_ID = "Session-Id";
    private static final String KEY_TOKEN_ID = "token_uuid";
    private static final String KEY_TOKEN_PARAM_NAME = "token_param_name";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EXPIRE_IN = "expire_in";
    private static final String[] PROPS = new String[]{KEY_CLIENT_ID,KEY_CLIENT_SECRET,KEY_APP_NAME,KEY_ENV};
    private static Map<String, String> data;
    private static Token t;

    @Reference
    private ResourceResolverFactory resolverFactory;

    public String toString() { return data != null ? data.toString() : "{}"; }

    public void load(HttpSession session) {
        if (!isValid()) {
            ResourceResolver resolver = AdminServiceUtil.getResourceResolver(resolverFactory);
            data = new HashMap<>();
            Resource resource = resolver.getResource(Constants.APP_CONFIG_PATH);
            if (resource != null) {
                loadData(resource.getValueMap());
            }
            if (resolver.isLive()) {
                resolver.close();
            }
        }
        loadAccessToken(session);
    }

    public void load(JsonObject props) {
        if (!isValid()) {
            data = new HashMap<>();
            for(String prop : PROPS) {
                if (props.has(prop)) {
                    data.put(prop, props.get(prop).getAsString());
                }
            }
        }
    }

    public boolean hasBearerToken() {return t != null && StringUtils.isNotEmpty(t.getBearerToken());}

    public String getBearerToken() {return t != null ? t.getBearerToken() : StringUtils.EMPTY;}
    public String getClientId() {return data.getOrDefault(KEY_CLIENT_ID, StringUtils.EMPTY);}
    public String getClientSecret() {return data.getOrDefault(KEY_CLIENT_SECRET, StringUtils.EMPTY);}
    public String getAppName() {return data.getOrDefault(KEY_APP_NAME, StringUtils.EMPTY);}
    public String getEnv() {return data.getOrDefault(KEY_ENV, DEFAULT_ENVIRONMENT);}
    public Map<String, String> getData() { return data; }

    public boolean isValid() {
        if (data == null || data.isEmpty()) {return false;}
        for (String prop : PROPS) {
            if (!data.containsKey(prop) || StringUtils.isEmpty(data.get(prop))) {
                return false;
            }
        }
        return true;
    }

    public void storeConfig() {
        if (isValid()) {
            ResourceResolver resolver = AdminServiceUtil.getResourceResolver(resolverFactory);
            NodeUtil.addIfNotAlreadyExist(resolver, Constants.ETC_COMMERCE_PATH);
            NodeUtil.save(resolver, Constants.APP_CONFIG_PATH, data);
            if (resolver.isLive()) {
                resolver.close();
            }
        }
    }

    public void keepAccessTokenInSession(JsonObject token, HttpSession session) { keepAccessTokenInSession(KEY_ACCESS_TOKEN, token, session); }

    public void keepAccessTokenInSession(String key, JsonObject token, HttpSession session) {
        t = new Token(token);
        session.setAttribute(key, t.toString());
    }

    private void loadData(ValueMap props) {
        for(String prop : PROPS) {
            if (props.containsKey(prop)) {
                data.put(prop, props.get(prop, StringUtils.EMPTY));
            }
        }
    }

    public void loadAccessToken(HttpSession session) { loadAccessToken(KEY_ACCESS_TOKEN, session); }

    public void loadAccessToken(String key, HttpSession session) {
        if (isValid() && t == null) {
            Object token = session.getAttribute(key);
            if (token != null) {
                t = new Token(token.toString());
                if (t.isExpired()) {
                    t.destroy();
                    t = null;
                }
            }
            if (t == null) {
                String endpoint = HostName.getAPIServer(this.getEnv()) + Endpoints.token();
                Client client = new Client(this);
                Response response = client.getAccessToken(endpoint);
                if (response.isNotError()) {
                    keepAccessTokenInSession(response.getDataAsJsonObject(), session);
                }
            }
        }
    }

    private class Token {

        private JsonObject jsonObject;
        private String sessionId;
        private String tokenId;
        private String tokenParamName;
        private String bearerToken;
        private String userId;
        private long expireIn;

        Token(String token) { loadData(DataUtil.toJsonObject(token)); }

        Token (JsonObject token) { loadData(token); }

        void loadData(JsonObject token) {
            jsonObject = token;
            sessionId = token.has(KEY_SESSION_ID) ? token.get(KEY_SESSION_ID).getAsString() : StringUtils.EMPTY;
            tokenId = token.has(KEY_TOKEN_ID) ? token.get(KEY_TOKEN_ID).getAsString() : StringUtils.EMPTY;
            tokenParamName = token.has(KEY_TOKEN_PARAM_NAME) ? token.get(KEY_TOKEN_PARAM_NAME).getAsString() : StringUtils.EMPTY;
            userId = token.has(KEY_USER_ID) ? token.get(KEY_USER_ID).getAsString() : StringUtils.EMPTY;
            expireIn = DateFormatUtil.now() + (token.has(KEY_EXPIRE_IN) ? token.get(KEY_EXPIRE_IN).getAsInt() : 0);
            bearerToken = token.has(tokenParamName) ? token.get(tokenParamName).getAsString() : StringUtils.EMPTY;
        }

        String getSessionId() {return sessionId;}
        String getTokenId() {return tokenId;}
        String getTokenParamName() {return tokenParamName;}
        String getBearerToken() {return bearerToken;}
        String getUserId() {return userId;}
        long getExpireIn() {return expireIn;}

        boolean isExpired() {
            long now = DateFormatUtil.now();
            return now > expireIn;
        }

        void destroy() {
            jsonObject = null;
            sessionId = null;
            tokenId = null;
            tokenParamName = null;
            userId = null;
            expireIn = 0;
            bearerToken = null;
        }

        public String toString() { return jsonObject.toString(); }
    }
}
