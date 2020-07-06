package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.models.AppConfig;
import com.ezpizee.aem.security.Jwt;
import com.google.gson.JsonObject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class AppConfigLoader {

    private AppConfig appConfig;

    public AppConfigLoader(ResourceResolver resolver, final HttpSession session) {
        Resource resource = resolver.getResource(Constants.APP_CONFIG_PATH);
        if (resource != null) {
            ValueMap props = resource.getValueMap();
            appConfig = new AppConfig(props);
        }
        else {
            Map<String, String> props = new HashMap<>();
            appConfig = new AppConfig(props);
        }
        if (appConfig.isValid()) {
            Object obj = session.getAttribute(Constants.KEY_ACCESS_TOKEN);
            if (obj != null) {
                appConfig.setAccessToken((String)obj);
            }
            else {
                loadAccessToken(session);
            }
        }
    }

    public AppConfig getAppConfig() { return appConfig; }

    private void loadAccessToken(final HttpSession session) {
        if (appConfig != null && appConfig.isValid()) {
            Client client = new Client(appConfig);
            client.setRequiredAccessToken(false);
            client.setAuth(appConfig.getClientId(), appConfig.getClientSecret());
            client.addHeader(Constants.HEADER_PARAM_CTYPE, Constants.HEADER_VALUE_FORM);
            client.addHeader(Constants.HEADER_PARAM_JWT, Jwt.clientTokenForAccessTokenRequest(
                appConfig.getEnv(), "", appConfig.getClientId(), "", appConfig.getAppName()
            ));
            Map<String, Object> formParams = new HashMap<>();
            formParams.put("grant_type", "client_credentials");
            client.setFormParams(formParams);
            Response response = client.post(Constants.ENDPOINT_GET_TOKEN);
            if (response.isNotError()) {
                JsonObject data = response.getDataAsJsonObject();
                if (data.has(Constants.KEY_ACCESS_TOKEN)) {
                    appConfig.setAccessToken(data.get(Constants.KEY_ACCESS_TOKEN).getAsString());
                    session.setAttribute(Constants.KEY_ACCESS_TOKEN, data.get(Constants.KEY_ACCESS_TOKEN));
                }
            }
        }
    }
}
