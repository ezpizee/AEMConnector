package com.ezpizee.aem.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Map;

import static com.ezpizee.aem.Constants.KEY_CLIENT_ID;
import static com.ezpizee.aem.Constants.KEY_CLIENT_SECRET;
import static com.ezpizee.aem.Constants.KEY_APP_NAME;
import static com.ezpizee.aem.Constants.KEY_ENV;
import static com.ezpizee.aem.Constants.KEY_ACCESS_TOKEN;
import static com.ezpizee.aem.Constants.DEFAULT_ENVIRONMENT;

public class AppConfig
{
    private static final String[] PROPS = new String[]{KEY_CLIENT_ID,KEY_CLIENT_SECRET,KEY_APP_NAME,KEY_ENV};
    private static Map<String, String> data;

    public AppConfig(Resource resource) {
        if (!isValid()) {
            data = new HashMap<>();
            if (resource != null) {
                loadData(resource.getValueMap());
            }
        }
    }

    public AppConfig(ValueMap props) {
        if (isValid()) {
            data = new HashMap<>();
            loadData(props);
        }
    }

    public AppConfig(Map<String, String> props) {
        if (isValid()) {
            data = new HashMap<>();
            for(String prop : PROPS) {
                if (props.containsKey(prop)) {
                    data.put(prop, props.get(prop));
                }
            }
        }
    }

    public void setAccessToken(String token) {data.put(KEY_ACCESS_TOKEN, token);}
    public String getAccessToken() {return data.getOrDefault(KEY_ACCESS_TOKEN, StringUtils.EMPTY);}

    public String getClientId() {return this.data.getOrDefault(KEY_CLIENT_ID, StringUtils.EMPTY);}
    public String getClientSecret() {return this.data.getOrDefault(KEY_CLIENT_SECRET, StringUtils.EMPTY);}
    public String getAppName() {return this.data.getOrDefault(KEY_APP_NAME, StringUtils.EMPTY);}
    public String getEnv() {return this.data.getOrDefault(KEY_ENV, DEFAULT_ENVIRONMENT);}

    public boolean isValid() {
        if (data == null || data.isEmpty()) {return false;}
        for (String prop : PROPS) {
            if (!data.containsKey(prop) || StringUtils.isEmpty(data.get(prop))) {
                return false;
            }
        }
        return true;
    }

    public String toString() { return data.toString(); }

    private void loadData(ValueMap props) {
        for(String prop : PROPS) {
            if (props.containsKey(prop)) {
                data.put(prop, props.get(prop, StringUtils.EMPTY));
            }
        }
    }
}
