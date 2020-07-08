package com.ezpizee.aem.services;

import aQute.bnd.annotation.ProviderType;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpSession;
import java.util.Map;

@ProviderType
public interface AppConfig {
    void load(HttpSession session);
    void load(JsonObject props);
    boolean hasBearerToken();

    String getBearerToken();
    String getClientId();
    String getClientSecret();
    String getAppName();
    String getEnv();
    Map<String, String> getData();

    boolean isValid();

    void storeConfig();
    void keepAccessTokenInSession(JsonObject token, HttpSession session);
}
