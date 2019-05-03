package com.ezpizee.aem;

public class Constants {

    private Constants() {}

    public static final String APP_RESOURCE_ADMIN = "ezpizee/components/structure/admin";
    public static final String APP_RESOURCE_DASHBOARD = "ezpizee/components/structure/dashboard";
    public static final String APP_RESOURCE_LIST = "ezpizee/components/structure/list";
    public static final String APP_RESOURCE_FORM = "ezpizee/components/structure/form";
    public static final String APP_RESOURCE_ASSET = "ezpizee/components/structure/asset";

    public static final String SERVICE_PROTOCOL_SCHEME = "https://";
    public static final String HOST_SFX = ".ezpizee.com";
    public static final String API_VERSION = "v1";
    public static final String SITENAME = "Ezpizee";
    public static final String VERSION = "1.0.0";

    public static final String ENDPOINT_GEN_SSH_KEYS = "/"+API_VERSION+"/config/generate/ssh/keys";
    public static final String ENDPOINT_GET_TOKEN = "/"+API_VERSION+"/auth/token";
    public static final String ENDPOINT_ENDPOINTS = "/"+API_VERSION+"/endpoints/list";
    public static final String SERVLET_INSTALL = "/bin/ezpizee/install";
    public static final String SERVLET_DELETE = "/bin/ezpizee/delete";

    public static final String FORM_COMMERCE_FORM_NAME = "commerce_form";
    public static final String FORM_WC_CONTENT_FORM_NAME = "wc_content_form";

    public static final String ETC_COMMERCE_PATH = "/etc/commerce/ezpizee";
    public static final String CONTENT_PATH = "/content/ezpizee";
    public static final String APP_CONFIG_PATH = ETC_COMMERCE_PATH + "/config";
    public static final String CONTENT_PATH_FORMAT = Constants.CONTENT_PATH + ".html#!%s";
    public static final String CONTENT_PATH_DASHBOARD = String.format(CONTENT_PATH_FORMAT, Constants.CONTENT_PATH + "/dashboard.html");
    public static final String IMG_LOGO = "/etc/designs/ezpizee/project_common/images/ezpizee.png";
    public static final String EZPIZEE_SERVICE = "ezpizee-service";
    public static final String UTF_8 = "UTF-8";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String ECMA_DATE_FORMAT = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z";

    public static final String HEADER_PARAM_ACCEPT = "Accept";
    public static final String HEADER_PARAM_CTYPE = "Content-Type";
    public static final String HEADER_PARAM_USER_NAME = "User-Name";
    public static final String HEADER_PARAM_ACCESS_TOKEN = "Access-Token";
    public static final String HEADER_PARAM_USER_AGENT = "User-Agent";
    public static final String HEADER_PARAM_JWT = "JWT-Token";
    public static final String HEADER_VALUE_JSON = "application/json";
    public static final String HEADER_VALUE_FORM = "application/x-www-form-urlencoded";
    public static final String HEADER_VALUE_USER_AGENT = "Ezpizee/1.0";
    public static final String HEADER_PARAM_DELETE_ID = "Delete-Id";
    public static final String HEADER_PARAM_CLIENT_ID = "Client-Id";
    public static final String HEADER_PARAM_HASHED_APP_NAME = "Hashed-App-Name";

    public static final String KEY_REST_API_URI_PARAMS = "rest_api_uri_params";
    public static final String KEY_LIST = "list";
    public static final String KEY_PAGINATION = "pagination";
    public static final String KEY_ITEM_DATA = "item_data";
    public static final String KEY_ENDPOINT = "endpoint";
    public static final String KEY_EDIT_ID = "edit_id";
    public static final String KEY_ID = "id";
    public static final String KEY_PAGE_TITLE = "page_title";
    public static final String KEY_METHOD = "method";
    public static final String KEY_DELETE_API_ENDPOINT = "delete_api_endpoint";
    public static final String KEY_DISPLAY_TEMPLATE = "display_template";
    public static final String KEY_ACTIONS = "actions";
    public static final String KEY_FIELDS = "fields";
    public static final String KEY_FOLDER_FORM_AGE = "folder_form_page";
    public static final String KEY_FILE_FORM_PAGE = "file_form_page";
    public static final String KEY_LIST_PAGE = "list_page";
    public static final String KEY_FORM_PAGE = "form_page";
    public static final String KEY_FORM_ACTION = "formAction";
    public static final String KEY_FIELD_KEYS = "fieldKeys";
    public static final String KEY_FIELD_LABELS = "fieldLabels";
    public static final String KEY_PARENT_ID = "parent_id";
    public static final String KEY_HASHED_APP_NAME = "hashedAppName";
    public static final String KEY_FORM_API_ENDPOINT = "form_api_endpoint";
    public static final String KEY_FIELD_TYPES = "fieldTypes";

    // Properties
    public static final String NODE_JCR_CONTENT = "jcr:content";
    public static final String PROP_JCR_PRIMARYTYPE = "jcr:primaryType";
    public static final String PROP_SLING_FOLDER = "sling:Folder";
    public static final String PROP_SLING_ORDER_FOLDER = "sling:OrderedFolder";
    public static final String PROP_NT_FOLDER = "nt:folder";
    public static final String PROP_NT_UNSTRUCTURE = "nt:unstructured";
    public static final String PROP_DAMASSET = "dam:Asset";
    public static final String PROP_CQPAGE = "cq:Page";
    public static final String PROP_SLING_RESOURCETYPE = "sling:resourceType";
    public static final java.lang.String AUTHENTICATION_INFO_SESSION = "user.jcr.session";
}
