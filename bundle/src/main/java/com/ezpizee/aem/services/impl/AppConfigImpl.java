package com.ezpizee.aem.services.impl;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.services.AdminService;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.NodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Property(value = "", label = "Client ID", description = "Ezpizee Client ID")
    private static final String PROP_CLIENT_ID = KEY_CLIENT_ID;
    @Property(value = "", label = "Client Secret", description = "Ezpizee Client Secret")
    private static final String PROP_CLIENT_SECRET = KEY_CLIENT_SECRET;
    @Property(value = "", label = "App Name", description = "Name of your Ezpizee App installing on this environment")
    private static final String PROP_APP_NAME = KEY_APP_NAME;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, String> data;

    @Reference
    private AdminService adminService;

    @Activate
    protected void activate(final Map<String, Object> props) {
        logger.debug("{} Activated.", this.getClass().getName());
        data = new HashMap<>();
        String env = PropertiesUtil.toString(props.get(PROP_ENV), StringUtils.EMPTY);
        String clientId = PropertiesUtil.toString(props.get(PROP_CLIENT_ID), StringUtils.EMPTY);
        String clientSecret = PropertiesUtil.toString(props.get(PROP_CLIENT_SECRET), StringUtils.EMPTY);
        String appName = PropertiesUtil.toString(props.get(PROP_APP_NAME), StringUtils.EMPTY);
        if (StringUtils.isNotEmpty(env) &&
            StringUtils.isNotEmpty(clientId) &&
            StringUtils.isNotEmpty(clientSecret) &&
            StringUtils.isNotEmpty(appName)) {
            data.put(PROP_ENV, env);
            data.put(PROP_CLIENT_ID, clientId);
            data.put(PROP_CLIENT_SECRET, clientSecret);
            data.put(PROP_APP_NAME, appName);
        }
        else if (adminService != null) {
            loadConfigFromCRX();
        }
    }

    @Deactivate
    protected void deactivate() {
        logger.debug("{} Deactivated.", this.getClass().getName());
        data = null;
    }

    public void setData(Map<String, String> data) {
        if (isValid(data)) {
            for (String key : PROPS) {
                this.data.put(key, data.get(key));
            }
        }
    }

    public String getClientId() {return data.getOrDefault(KEY_CLIENT_ID, StringUtils.EMPTY);}
    public String getClientSecret() {return data.getOrDefault(KEY_CLIENT_SECRET, StringUtils.EMPTY);}
    public String getAppName() {return data.getOrDefault(KEY_APP_NAME, StringUtils.EMPTY);}
    public String getEnv() {return data.getOrDefault(KEY_ENV, StringUtils.EMPTY);}

    public boolean isValid() {return isValid(data);}

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

    private boolean isValid(Map<String, String> map) {
        for (String prop : PROPS) {
            if (!map.containsKey(prop) || StringUtils.isEmpty(map.get(prop))) {
                return false;
            }
        }
        return true;
    }

    private void loadConfigFromCRX() {
        ResourceResolver resolver = adminService.getResourceResolver(EZPIZEE_SERVICE);
        Resource resource = resolver.getResource(Constants.APP_CONFIG_PATH);
        if (resource != null) {
            ValueMap props = resource.getValueMap();
            for(String prop : PROPS) {
                if (props.containsKey(prop)) {
                    data.put(prop, props.get(prop, StringUtils.EMPTY));
                }
            }
            if (!isValid(data)) {
                data = new HashMap<>();
            }
        }
        if (resolver.isLive()) {
            resolver.close();
        }
    }
}

