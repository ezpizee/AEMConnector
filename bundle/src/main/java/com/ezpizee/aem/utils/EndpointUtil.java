package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import org.apache.commons.lang3.StringUtils;

public class EndpointUtil
{
    private EndpointUtil() {}

    public static String getService(String env, String endpoint) {
        if (endpoint.startsWith("/"+Constants.API_VERSION+"/")) {
            String[] parts = endpoint.replace("/"+Constants.API_VERSION+"/", "").split("/");
            if (parts.length > 1 && isApiService(parts[0])) {
                return getHostName(env, parts[0]) + "/" + endpoint.replace("/"+Constants.API_VERSION+"/"+parts[0]+"/", "");
            }
        }
        return endpoint;
    }

    public static String getApiHostName(String env) { return getHostName(env, APIServiceEnum.API.toString()); }
    public static String getAdminHostName(String env) { return getHostName(env, APIServiceEnum.ADMIN.toString()); }
    public static String getAssetsHostName(String env) { return getHostName(env, APIServiceEnum.ASSETS.toString()); }
    public static String getAuthHostName(String env) { return getHostName(env, APIServiceEnum.AUTH.toString()); }
    public static String getCartHostName(String env) { return getHostName(env, APIServiceEnum.CART.toString()); }
    public static String getStoreFrontHostName(String env) { return getHostName(env, APIServiceEnum.STOREFRONT.toString()); }
    public static String getConfigHostName(String env) { return getHostName(env, APIServiceEnum.CONFIG.toString()); }
    public static String getGlobalPropertiesHostName(String env) { return getHostName(env, APIServiceEnum.GLOBALPROPERTIES.toString()); }
    public static String getPimHostName(String env) { return getHostName(env, APIServiceEnum.PIM.toString()); }
    public static String getPosHostName(String env) { return getHostName(env, APIServiceEnum.POS.toString()); }
    public static String getPriceHostName(String env) { return getHostName(env, APIServiceEnum.PRICE.toString()); }
    public static String getStoreManagerHostName(String env) { return getHostName(env, APIServiceEnum.STOREMANAGER.toString()); }

    private static String getHostName(String env, String key) {
        if (isEnv(env)) {
            if (isProd(env)) {
                return Constants.SERVICE_PROTOCOL_SCHEME + key.toLowerCase() + Constants.HOST_SFX;
            }
            return Constants.SERVICE_PROTOCOL_SCHEME + env.toLowerCase() + "-" + key.toLowerCase() + Constants.HOST_SFX;
        }
        return StringUtils.EMPTY;
    }

    private static boolean isApiService(String service) {
        for (APIServiceEnum apiService : APIServiceEnum.values()) {
            if (apiService.name().equals(service.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isProd(String env) {
        return EnvironmentEnum.PROD.name().equals(env.toUpperCase());
    }

    private static boolean isEnv(String env) {
        for (EnvironmentEnum environment : EnvironmentEnum.values()) {
            if (environment.name().equals(env.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
