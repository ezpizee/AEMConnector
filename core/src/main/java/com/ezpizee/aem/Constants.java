package com.ezpizee.aem;

public class Constants {

    private Constants() {}

    public static final String SITENAME = "Ezpizee";
    public static final String CONTENT_PATH = "/content/ezpizee";
    public static final String CONTENT_PATH_FORMAT = Constants.CONTENT_PATH + ".html#!%s";
    public static final String CONTENT_PATH_DASHBOARD = String.format(CONTENT_PATH_FORMAT, Constants.CONTENT_PATH + "/dashboard.html");
    public static final String IMG_LOGO = "/etc/designs/ezpizee/project_common/images/ezpizee.png";
    public static final String EZPIZEE_SERVICE = "ezpizee-service";
    public static final String UTF_8 = "UTF-8";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String ECMA_DATE_FORMAT = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z";
    // Properties
    public static final String PROP_JCR_PRIMARYTYPE = "jcr:primaryType";
    public static final String PROP_SLING_FOLDER = "sling:Folder";
    public static final String PROP_SLING_ORDER_FOLDER = "sling:OrderedFolder";
    public static final String PROP_NT_FOLDER = "nt:folder";
    public static final String PROP_DAMASSET = "dam:Asset";
    public static final String PROP_CQPAGE = "cq:Page";
    public static final java.lang.String AUTHENTICATION_INFO_SESSION = "user.jcr.session";
}
