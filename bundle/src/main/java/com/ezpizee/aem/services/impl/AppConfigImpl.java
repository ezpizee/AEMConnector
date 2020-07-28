package com.ezpizee.aem.services.impl;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AdminService;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.*;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.ezpizee.aem.Constants.*;
import static com.ezpizee.aem.Constants.KEY_ENV;

@Service
@Component(
    metatype = true,
    label = "Ezpizee App Configuration Service",
    description = "This config can be used to execute Ezpizee App"
)
public class AppConfigImpl implements AppConfig {

    private static final String[] PROPS = new String[]{KEY_CLIENT_ID,KEY_CLIENT_SECRET,KEY_APP_NAME,KEY_ENV};

    @Property(value = "local", label = "Environment", description = "Ezpizee environment")
    private static final String PROP_ENV = KEY_ENV;
    private String env;
    @Property(value = "", label = "Client ID", description = "Ezpizee Client ID")
    private static final String PROP_CLIENT_ID = KEY_CLIENT_ID;
    private String clientId;
    @Property(value = "", label = "Client Secret", description = "Ezpizee Client Secret")
    private static final String PROP_CLIENT_SECRET = KEY_CLIENT_SECRET;
    private String clientSecret;
    @Property(value = "", label = "App Name", description = "Name of your Ezpizee App installing on this environment")
    private static final String PROP_APP_NAME = KEY_APP_NAME;
    private String appName;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static Map<String, String> data;
    private static Token t;
    private boolean appDataInConfigProperties;

    @Reference
    private AdminService adminService;

    @Reference
    private SlingSettingsService sss;

    @Activate
    protected void activate(final Map<String, Object> props) {
        appDataInConfigProperties = false;
        env = PropertiesUtil.toString(props.get(PROP_ENV), RunModesUtil.env(sss));
        clientId = PropertiesUtil.toString(props.get(PROP_CLIENT_ID), StringUtils.EMPTY);
        clientSecret = PropertiesUtil.toString(props.get(PROP_CLIENT_SECRET), StringUtils.EMPTY);
        appName = PropertiesUtil.toString(props.get(PROP_APP_NAME), StringUtils.EMPTY);
        if (StringUtils.isNotEmpty(env) && StringUtils.isNotEmpty(clientId) && StringUtils.isNotEmpty(clientSecret) && StringUtils.isNotEmpty(appName)) {
            data = new HashMap<>();
            data.put(PROP_ENV, env);
            data.put(PROP_CLIENT_ID, clientId);
            data.put(PROP_CLIENT_SECRET, clientSecret);
            data.put(PROP_APP_NAME, appName);
            appDataInConfigProperties = true;
        }
    }

    @Deactivate
    protected void deactivate() {
        adminService = null;
        sss = null;
        t = null;
        env = null;
        clientId = null;
        clientSecret = null;
        appName = null;
    }

    public String toString() { return data != null ? DataUtil.map2JsonObject(data).toString() : "{}"; }

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
    public String getClientId() {return data.getOrDefault(KEY_CLIENT_ID, clientId);}
    public String getClientSecret() {return data.getOrDefault(KEY_CLIENT_SECRET, clientSecret);}
    public String getAppName() {return data.getOrDefault(KEY_APP_NAME, appName);}
    public String getEnv() {return data.getOrDefault(KEY_ENV, env);}
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
                    this.keepAccessTokenInSession(key, response.getDataAsJsonObject(), session);
                }
                else if (appDataInConfigProperties && "INVALID_API_CREDENTIAL".equals(response.getMessage())) {
                    endpoint = HostName.getAPIServer(this.getEnv()) + Endpoints.install();
                    client = new Client(this);
                    response = client.install(endpoint, this.toString());
                    if (response.isNotError() && response.hasData()) {
                        this.storeConfig();
                        this.keepAccessTokenInSession(key, response.getDataAsJsonObject(), session);
                    }
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
