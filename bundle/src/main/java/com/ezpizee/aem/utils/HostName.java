package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;

public class HostName
{
    private static final String HTTPS_SCHEMA = "https://", HTTP_SCHEMA = "https://";
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
            if (Constants.ENVIRONMENTS.get(0).equals(env)) {
                return HTTP_SCHEMA + env + "-api" + EZPIZEE_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(1).equals(env)) {
                return HTTP_SCHEMA + env + "-api" + EZPZ_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(2).equals(env)) {
                return HTTP_SCHEMA + env + "-api" + EZPZ_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(3).equals(env)) {
                return HTTP_SCHEMA + env + "-api" + EZPIZEE_SFX;
            }
        }
        return HTTPS_SCHEMA + "api" + EZPIZEE_SFX;
    }

    public static String getCDNServer(String env) {
        if (isEnv(env)) {
            if (Constants.ENVIRONMENTS.get(0).equals(env)) {
                return HTTP_SCHEMA + env + "-cdn" + EZPZ_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(1).equals(env)) {
                return HTTPS_SCHEMA + env + "-cdn" + EZPZ_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(2).equals(env)) {
                return HTTPS_SCHEMA + env + "-cdn" + EZPZ_SFX;
            }
            else if (Constants.ENVIRONMENTS.get(3).equals(env)) {
                return HTTPS_SCHEMA + env + "-cdn" + EZPZ_SFX;
            }
        }
        return HTTPS_SCHEMA + "cdn" + EZPZ_SFX;
    }

    private static boolean isEnv(String env) { return Constants.ENVIRONMENTS.contains(env); }
}
