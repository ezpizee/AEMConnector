package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import org.apache.commons.lang3.StringUtils;

public class HostName
{
    private static final String EZPIZEE_SFX = ".ezpizee.com";
    private static final String EZPZ_SFX = ".ezpz.solutions";

    private HostName() {}

    public static String getLocalCDNServer() {return getCDNServer(Constants.ENVIRONMENTS.get(0));}
    public static String getDevCDNServer() {return getCDNServer(Constants.ENVIRONMENTS.get(1));}
    public static String getQACDNServer() {return getCDNServer(Constants.ENVIRONMENTS.get(2));}
    public static String getStageCDNServer() {return getCDNServer(Constants.ENVIRONMENTS.get(3));}
    public static String getProdCDNServer() {return getCDNServer(Constants.ENVIRONMENTS.get(4));}
    public static String getLocalAPIServer() {return getAPIServer(Constants.ENVIRONMENTS.get(0));}
    public static String getDevAPIServer() {return getAPIServer(Constants.ENVIRONMENTS.get(1));}
    public static String getQAAPIServer() {return getAPIServer(Constants.ENVIRONMENTS.get(2));}
    public static String getStageAPIServer() {return getAPIServer(Constants.ENVIRONMENTS.get(3));}
    public static String getProdAPIServer() {return getAPIServer(Constants.ENVIRONMENTS.get(4));}

    public static String getAPIServer(String env) {
        if (isEnv(env)) {
            if (Constants.ENVIRONMENTS.get(4).equals(env)) {
                return "https://api" + EZPIZEE_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(0).equals(env)) {
                return "http://local-api" + EZPIZEE_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(1).equals(env)) {
                return "https://dev-api" + EZPZ_SFX;
            }
            else {
                return "https://"+env+EZPIZEE_SFX;
            }
        }
        return StringUtils.EMPTY;
    }

    public static String getCDNServer(String env) {
        if (isEnv(env)) {
            if (Constants.ENVIRONMENTS.get(4).equals(env)) {
                return "https://cdn"+EZPZ_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(0).equals(env)) {
                return "http://local-cdn"+EZPZ_SFX;
            }
            else {
                return "https://"+env+"-cdn"+EZPZ_SFX;
            }
        }
        return StringUtils.EMPTY;
    }

    private static boolean isEnv(String env) { return Constants.ENVIRONMENTS.contains(env); }
}
