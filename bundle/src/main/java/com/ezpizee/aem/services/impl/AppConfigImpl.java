package com.ezpizee.aem.services.impl;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AdminService;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.*;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String[] PROPS = new String[]{KEY_CLIENT_ID,KEY_CLIENT_SECRET,KEY_APP_NAME,KEY_ENV};
    private static Map<String, String> data;
    private static Token t;

    @Reference
    private AdminService adminService;

    public String toString() { return data != null ? data.toString() : "{}"; }

    public void load() {
        t = new Token();
        if (!isValid()) {
            data = new HashMap<>();
            ResourceResolver resolver = adminService.getResourceResolver(EZPIZEE_SERVICE);
            Resource resource = resolver.getResource(Constants.APP_CONFIG_PATH);
            if (resource != null) {
                loadConfigDataFromCRX(resource.getValueMap());
            }
            if (resolver.isLive()) {
                resolver.close();
            }
        }
    }

    public void load(String key, HttpSession session) {
        logger.debug("key: {}", key);
        load();
        loadAccessToken(key, session);
    }

    public void load(JsonObject props) {
        t = new Token();
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
    public Token getToken() {return t == null ? new Token() : t;}

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
            ResourceResolver resolver = adminService.getResourceResolver(EZPIZEE_SERVICE);
            NodeUtil.addIfNotAlreadyExist(resolver, Constants.ETC_COMMERCE_PATH);
            NodeUtil.save(resolver, Constants.APP_CONFIG_PATH, data);
            if (resolver.isLive()) {
                resolver.close();
            }
        }
    }

    public void keepAccessTokenInSession(String key, JsonObject token, HttpSession session) {
        if (StringUtils.isNotEmpty(key)) {
            t = new Token(token);
            session.setAttribute(key, t.toString());
        }
    }

    public void loadAccessToken(String key, HttpSession session) {
        if (StringUtils.isNotEmpty(key)) {
            loadToken(key, session);
            if (isValid() && !hasBearerToken()) {
                String endpoint = HostName.getAPIServer(this.getEnv()) + Endpoints.token();
                Client client = new Client(this);
                Response response = client.getAccessToken(endpoint);
                if (response.isNotError()) {
                    keepAccessTokenInSession(key, response.getDataAsJsonObject(), session);
                }
            }
        }
    }

    public void clearAccessTokenSession(String key, HttpSession session) {
        if (StringUtils.isNotEmpty(key)) {
            Object token = session.getAttribute(key);
            if (token != null) {
                session.removeAttribute(key);
            }
        }
    }

    public void refreshToken(String key, HttpSession session) {
        if (StringUtils.isNotEmpty(key)) {
            loadToken(key, session);
            if (t != null && t.expireInFiveMinutes()) {
                String endpoint = HostName.getAPIServer(this.getEnv()) + Endpoints.refreshToken()
                    .replace("{token_uuid}", t.getTokenId())
                    .replace("{user_id}", t.getUserId());
                Client client = new Client(this);
                client.setRequiredAccessToken(false);
                Response response = client.post(endpoint);
                if (response.isNotError()) {
                    keepAccessTokenInSession(key, response.getDataAsJsonObject(), session);
                }
            }
        }
    }

    public void logout(String key, HttpSession session) {
        if (StringUtils.isNotEmpty(key)) {
            t = new Token();
            data = new HashMap<>();
            clearAccessTokenSession(key, session);
            this.load(KEY_ACCESS_TOKEN, session);
        }
    }

    private void loadToken(String key, HttpSession session) {
        if (StringUtils.isNotEmpty(key) && isValid() && !hasBearerToken()) {
            Object token = session.getAttribute(key);
            if (token != null) {
                t = new Token(token.toString());
                if (t.isExpired()) {
                    t.destroy();
                    t = new Token();
                }
            }
            else {
                t = new Token();
            }
        }
    }

    private void loadConfigDataFromCRX(ValueMap props) {
        for(String prop : PROPS) {
            if (props.containsKey(prop)) {
                data.put(prop, props.get(prop, StringUtils.EMPTY));
            }
        }
    }
}
