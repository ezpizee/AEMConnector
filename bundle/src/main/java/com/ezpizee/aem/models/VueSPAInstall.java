package com.ezpizee.aem.models;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.utils.HostName;
import com.ezpizee.aem.utils.RunModesUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.sling.settings.SlingSettingsService;

public class VueSPAInstall extends BaseModel {

    private static final String INSTALL_HTML = "/install/html/index.aem.html";
    private String htmlContent;

    @Override
    public void exec() {
        com.ezpizee.aem.services.AppConfig appConfig = getSlingScriptHelper().getService(com.ezpizee.aem.services.AppConfig.class);
        SlingSettingsService sss = getSlingScriptHelper().getService(SlingSettingsService.class);
        Client client = new Client();
        if (appConfig != null && appConfig.isValid()) {
            htmlContent = client.getContent(HostName.getCDNServer(appConfig.getEnv())+INSTALL_HTML);
            String replace2 = "<head>";
            htmlContent = htmlContent.replace(replace2, replaceEzpzOverrideData(replace2, appConfig));
        }
        else {
            htmlContent = client.getContent(HostName.getCDNServer(RunModesUtil.env(sss))+INSTALL_HTML);
        }
    }

    public String getHtmlContent() {return htmlContent;}

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
