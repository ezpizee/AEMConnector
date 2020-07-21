package com.ezpizee.aem.services;

import aQute.bnd.annotation.ProviderType;
import com.ezpizee.aem.utils.Token;
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

    Token getToken();

    boolean isValid();

    void storeConfig();

    void keepAccessTokenInSession(JsonObject token, HttpSession session);

    void keepAccessTokenInSession(String key, JsonObject token, HttpSession session);

    void loadAccessToken(HttpSession session);

    void loadAccessToken(String key, HttpSession session);

    void clearAccessTokenSession(String key, HttpSession session);

    void refreshToken(String key, HttpSession session);

    void logout(String key, HttpSession session);
}
