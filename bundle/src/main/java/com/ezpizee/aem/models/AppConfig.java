package com.ezpizee.aem.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Map;

public class AppConfig
{
    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_CLIENT_SECRET = "client_secret";
    public static final String KEY_PHRASE = "phrase";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_APP_NAME = "app_name";
    public static final String KEY_ENV = "env";
    public static final String KEY_PUBLIC_KEY = "public_key";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String[] PROPS = new String[]{KEY_CLIENT_ID,KEY_CLIENT_SECRET,KEY_PHRASE,KEY_USERNAME,KEY_APP_NAME,KEY_ENV,KEY_PUBLIC_KEY};
    private final Map<String, String> data;

    public AppConfig(Resource resource) {
        data = new HashMap<>();
        if (resource != null) {
            loadData(resource.getValueMap());
        }
    }

    public AppConfig(ValueMap props) {
        data = new HashMap<>();
        loadData(props);
    }

    public AppConfig(Map<String, String> props) {
        data = new HashMap<>();
        for(String prop : PROPS) {
            if (props.containsKey(prop)) {
                data.put(prop, props.get(prop));
            }
        }
    }

    public String getClientId() {return this.data.getOrDefault(KEY_CLIENT_ID, StringUtils.EMPTY);}
    public String getClientSecret() {return this.data.getOrDefault(KEY_CLIENT_SECRET, StringUtils.EMPTY);}
    public String getPhrase() {return this.data.getOrDefault(KEY_PHRASE, StringUtils.EMPTY);}
    public String getUserName() {return this.data.getOrDefault(KEY_USERNAME, StringUtils.EMPTY);}
    public String getAppName() {return this.data.getOrDefault(KEY_APP_NAME, StringUtils.EMPTY);}
    public String getEnv() {return this.data.getOrDefault(KEY_ENV, StringUtils.EMPTY);}
    public String getPublicKey() {return this.data.getOrDefault(KEY_PUBLIC_KEY, StringUtils.EMPTY);}

    public void setAccessToken(String accessToken) {this.data.put(KEY_ACCESS_TOKEN, accessToken);}
    public String getAccessToken(){return this.data.getOrDefault(KEY_ACCESS_TOKEN, StringUtils.EMPTY);}

    public boolean isValid() {
        if (data.isEmpty()) {return false;}
        for (String prop : PROPS) {
            if (!data.containsKey(prop) || StringUtils.isEmpty(data.get(prop))) {
                return false;
            }
        }
        return true;
    }

    public String toString() { return data.toString(); }

    public Map<String, String> toMap() { return data; }

    private void loadData(ValueMap props) {
        for(String prop : PROPS) {
            if (props.containsKey(prop)) {
                data.put(prop, props.get(prop, StringUtils.EMPTY));
            }
        }
    }
}
