package com.ezpizee.aem.utils;

import org.apache.commons.lang3.StringUtils;

public class PathUtil {

    private PathUtil() {}

    public static String url(String path) {
        if (!StringUtils.isEmpty(path) && path.startsWith("/content/") && !path.endsWith(".html") &&
            !path.startsWith("/content/dam/")) {
            path = path + ".html";
        }
        return path;
    }

    public static boolean isExternalLink(String path) {
        if (!StringUtils.isEmpty(path) && (path.startsWith("http://") || path.startsWith("https://"))) {
            return true;
        }
        return false;
    }
}
