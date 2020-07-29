package com.ezpizee.aem.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.http.Cookie;

import static com.ezpizee.aem.Constants.KEY_ACCESS_TOKEN;
import static com.ezpizee.aem.Constants.KEY_EZPZ_LOGIN;

public class CookieUtil {

    private static final int maxAge = 60*60;

    /**
     * Set cookie that doesn't have expiry
     *
     * @param request SlingHttpServletRequest
     * @param response SlingHttpServletResponse
     * @param key String
     * @param val String
     */
    public static void setSessionCookie(SlingHttpServletRequest request, SlingHttpServletResponse response, String key, String val) {
        set(request, response, key, val, -1);
    }

    /**
     * Set cookie for 1 hour
     *
     * @param request SlingHttpServletRequest
     * @param response SlingHttpServletResponse
     * @param key String
     * @param val String
     */
    public static void setDefaultCookie(SlingHttpServletRequest request, SlingHttpServletResponse response, String key, String val) {
        set(request, response, key, val, maxAge);
    }

    /**
     * Remove/delete cookie
     *
     * @param request SlingHttpServletRequest
     * @param response SlingHttpServletResponse
     * @param key String
     */
    public static void remove(SlingHttpServletRequest request, SlingHttpServletResponse response, String key) {
        set(request, response, key, null, 0);
    }

    /**
     * Set cookie with custom maxAge (in seconds)
     *
     * @param request SlingHttpServletRequest
     * @param response SlingHttpServletResponse
     * @param key String
     * @param val String
     * @param maxAge int
     */
    public static void set(SlingHttpServletRequest request, SlingHttpServletResponse response, String key, String val, int maxAge) {
        com.adobe.cq.commerce.common.CookieUtil.setCookie(request, response, key, val, maxAge, true);
    }

    /**
     * Return cookie value, which is the key for the authentication session
     *
     * @param request SlingHttpServletRequest
     * @return String
     */
    public static String getAuthCookie(SlingHttpServletRequest request) {
        Cookie cookie = request.getCookie(KEY_EZPZ_LOGIN);
        if (cookie != null) {
            return cookie.getValue();
        }
        return KEY_ACCESS_TOKEN;
    }
}
