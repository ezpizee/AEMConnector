package com.ezpizee.aem.services;

import aQute.bnd.annotation.ProviderType;
import java.util.Map;

@ProviderType
public interface AppConfig {

    void setData(Map<String, String> data);
    String getClientId();
    String getClientSecret();
    String getAppName();
    String getEnv();
    boolean isValid();
    void storeConfig();
}
