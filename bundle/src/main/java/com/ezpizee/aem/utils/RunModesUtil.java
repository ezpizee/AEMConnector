package com.ezpizee.aem.utils;

import org.apache.sling.settings.SlingSettingsService;

import java.util.Set;

public class RunModesUtil {

    public static boolean isAuthor(SlingSettingsService slingSettingsService) { return isRunMode(slingSettingsService, "author"); }

    public static boolean isPublish(SlingSettingsService slingSettingsService) { return isRunMode(slingSettingsService, "publish"); }

    public static boolean isLocal(SlingSettingsService slingSettingsService) { return isRunMode(slingSettingsService, "local"); }

    public static boolean isDev(SlingSettingsService slingSettingsService) { return isRunMode(slingSettingsService, "dev"); }

    public static boolean isQA(SlingSettingsService slingSettingsService) { return isRunMode(slingSettingsService, "qa"); }

    public static boolean isStage(SlingSettingsService slingSettingsService) { return isRunMode(slingSettingsService, "stage"); }

    public static boolean isProd(SlingSettingsService slingSettingsService) { return isRunMode(slingSettingsService, "prod"); }

    public static String env(SlingSettingsService sss) {
        if (isDev(sss)) {return "dev";}
        if (isQA(sss)) {return "qa";}
        if (isStage(sss)) {return "stage";}
        if (isProd(sss)) {return "prod";}
        return "local";
    }

    private static boolean isRunMode(SlingSettingsService slingSettingsService, String key) {
        if (slingSettingsService != null) {
            Set<String> runModes = slingSettingsService.getRunModes();
            for (String runMode : runModes) {
                if (runMode.toLowerCase().equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }
}
