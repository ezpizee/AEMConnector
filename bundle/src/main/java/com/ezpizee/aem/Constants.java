package com.ezpizee.aem;

import org.apache.sling.api.servlets.HttpConstants;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    private Constants() {}

    public static final String ETC_COMMERCE_PATH = "/etc/commerce/ezpizee";
    public static final String APP_CONFIG_PATH = ETC_COMMERCE_PATH + "/config";
    public static final String EZPIZEE_SERVICE = "ezpizee-service";
    public static final String UTF_8 = "UTF-8";
    public static final String EXT_HTML = ".html";

    public static final String HEADER_PARAM_ACCEPT = HttpConstants.HEADER_ACCEPT;
    public static final String HEADER_PARAM_CTYPE = "Content-Type";
    public static final String HEADER_PARAM_ACCESS_TOKEN = "Authorization";
    public static final String HEADER_PARAM_USER_AGENT = "User-Agent";
    public static final String HEADER_VALUE_JSON = "application/json";
    public static final String HEADER_VALUE_USER_AGENT = "Ezpizee Web/1.0";
    public static final String HEADER_PARAM_APP_NAME = "App-Name";
    public static final String HEADER_PARAM_APP_VERSION = "App-Version";
    public static final String HEADER_VALUE_APP_VERSION = "0.0.1";
    public static final String HEADER_PARAM_APP_PLATFORM = "App-Platform";
    public static final String HEADER_VALUE_APP_PLATFORM = "Unknown";
    public static final String HEADER_PARAM_OS_PLATFORM_VERSION = "OS-Platform-Version";
    public static final String HEADER_VALUE_OS_PLATFORM_VERSION = "Unknown";
    public static final String HEADER_LANGUAGE_TAG = "Language-Tag";

    public static final String URL_ADMINUI_VERSION = "https://cdn.ezpz.solutions/adminui/versions.json";

    public static final String KEY_ENDPOINT = "endpoint";

    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_CLIENT_SECRET = "client_secret";
    public static final String KEY_APP_NAME = "app_name";
    public static final String KEY_ENV = "env";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_EZPZ_LOGIN = "ezpizee_login";
    public static final String KEY_EXPIRE_IN = "expire_in";

    public static final String PROP_SLING_FOLDER = "sling:Folder";
    public static final String PROP_SLING_ORDER_FOLDER = "sling:OrderedFolder";
    public static final String PROP_NT_UNSTRUCTURE = "nt:unstructured";

    public static final List<String> ENVIRONMENTS = new ArrayList<>();

    static {
        ENVIRONMENTS.add("local");
        ENVIRONMENTS.add("dev");
        ENVIRONMENTS.add("qa");
        ENVIRONMENTS.add("stage");
        ENVIRONMENTS.add("prod");
    }
}
