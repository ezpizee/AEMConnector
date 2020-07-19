package com.ezpizee.aem.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public class CookieUtil {

    private static final int maxAge = 60*60;

    /**
     * Set cookie that doesn't have expiry
     *
     * @param request
     * @param response
     * @param key
     * @param val
     */
    public static void setSessionCookie(SlingHttpServletRequest request, SlingHttpServletResponse response, String key, String val) {
        set(request, response, key, val, -1);
    }

    /**
     * Set cookie for 1 hour
     *
     * @param request
     * @param response
     * @param key
     * @param val
     */
    public static void setDefaultCookie(SlingHttpServletRequest request, SlingHttpServletResponse response, String key, String val) {
        set(request, response, key, val, maxAge);
    }

    /**
     * Remove/delete cookie
     *
     * @param request
     * @param response
     * @param key
     */
    public static void remove(SlingHttpServletRequest request, SlingHttpServletResponse response, String key) {
        set(request, response, key, null, 0);
    }

    /**
     * Set cookie with custom maxAge (in seconds)
     *
     * @param request
     * @param response
     * @param key
     * @param val
     * @param maxAge
     */
    public static void set(SlingHttpServletRequest request, SlingHttpServletResponse response, String key, String val, int maxAge) {
        com.adobe.cq.commerce.common.CookieUtil.setCookie(request, response, key, val, maxAge, true);
    }
}
