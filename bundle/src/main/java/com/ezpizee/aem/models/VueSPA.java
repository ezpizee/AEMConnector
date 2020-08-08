package com.ezpizee.aem.models;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.utils.CookieUtil;
import com.ezpizee.aem.utils.FileUtil;
import com.ezpizee.aem.utils.HostName;
import com.ezpizee.aem.utils.RunModesUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.sling.settings.SlingSettingsService;

public class VueSPA extends BaseModel {

    private static final String INSTALL_HTML = "/install/html/index.aem.html";
    private static final String ADMIN_HTML = "/adminui/html/index.aem.html";
    private String htmlContent, installHtmContent;

    @Override
    public void exec() {
        com.ezpizee.aem.services.AppConfig appConfig = getSlingScriptHelper().getService(com.ezpizee.aem.services.AppConfig.class);
        SlingSettingsService sss = getSlingScriptHelper().getService(SlingSettingsService.class);
        String env = RunModesUtil.env(sss);
        Client client = new Client();
        installHtmContent = client.getContent(HostName.getCDNServer(env)+INSTALL_HTML);
        if (appConfig != null) {
            if (appConfig.isValid()) {
                AccessToken accessToken = getSlingScriptHelper().getService(AccessToken.class);
                if (accessToken != null) {
                    accessToken.load(CookieUtil.getAuthCookie(getRequest()), getRequest().getSession());
                }
                htmlContent = client.getContent(HostName.getCDNServer(appConfig.getEnv())+ADMIN_HTML);
            }
            else {
                htmlContent = installHtmContent;
            }

            String replace1 = "<body";
            String replace2 = "<head>";
            htmlContent = htmlContent.replace(replace1, replaceBodyStr(replace1, sss)).replace(replace2, replacePlatformJSStr(replace2));
            installHtmContent = installHtmContent.replace(replace2, replaceEzpzOverrideData(replace2, appConfig));
        }
    }

    public String getHtmlContent() {return htmlContent;}
    public String getInstallHtmContent() {return installHtmContent;}

    private String replaceBodyStr(String pattern, SlingSettingsService sss) {
        return pattern+" data-run-mode='"+(RunModesUtil.isAuthor(sss)?"author":"publish")+"'";
    }

    private String replacePlatformJSStr(String pattern) {
        return pattern +
            FileUtil.getContentAsStringFromFileOnCRX(
                resolver,
                "/apps/ezpizee/components/structure/base/ezpz-override-endpoints.html"
            );
    }

    private String replaceEzpzOverrideData(String pattern, com.ezpizee.aem.services.AppConfig appConfig) {
        JsonObject object = new JsonObject();
        object.add("app_name", new JsonPrimitive(appConfig.getAppName()));
        object.add("", new JsonPrimitive(appConfig.getClientSecret()));
        object.add("", new JsonPrimitive(appConfig.getClientId()));
        object.add("", new JsonPrimitive(appConfig.getEnv()));
        return pattern +
            "<script>" +
            "window.EzpzOverrideData={"+
            "app_name:\""+appConfig.getAppName()+"\","+
            "client_secret:\""+appConfig.getClientSecret()+"\","+
            "client_id:\""+appConfig.getClientId()+"\","+
            "env:\""+appConfig.getEnv()+"\""+
            "}"+
            "</script>";
    }
}
