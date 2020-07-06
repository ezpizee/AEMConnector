package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import org.apache.commons.lang3.StringUtils;

public class HostName
{
    private HostName() {}

    public static String getAPIServer(String env) {
        if (isEnv(env)) {
            if (Constants.ENVIRONMENTS.get(4).equals(env)) {
                return "https://api.ezpizee.com";
            }
            else if (Constants.ENVIRONMENTS.get(0).equals(env)) {
                return "http://local-api.ezpizee.com";
            }
            else {
                return "https://"+env+"-api.ezpizee.com";
            }
        }
        return StringUtils.EMPTY;
    }

    public static String getCDNServer(String env) {
        if (isEnv(env)) {
            if (Constants.ENVIRONMENTS.get(4).equals(env)) {
                return "https://cdn.ezpz.solutions";
            }
            else if (Constants.ENVIRONMENTS.get(0).equals(env)) {
                return "http://local-cdn.ezpz.solutions";
            }
            else {
                return "https://"+env+"-cdn.ezpz.solutions";
            }
        }
        return StringUtils.EMPTY;
    }

    private static boolean isEnv(String env) { return Constants.ENVIRONMENTS.contains(env); }
}
