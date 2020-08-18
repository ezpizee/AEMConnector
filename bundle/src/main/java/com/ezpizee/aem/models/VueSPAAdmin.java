package com.ezpizee.aem.models;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.utils.*;
import com.google.gson.JsonArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.settings.SlingSettingsService;

import static com.ezpizee.aem.Constants.URL_ADMINUI_VERSION;

public class VueSPAAdmin extends BaseModel {

    private static final String ADMIN_HTML = "/adminui/{version}/index.aem.html";
    private String htmlContent;

    @Override
    public void exec() {
        com.ezpizee.aem.services.AppConfig appConfig = getSlingScriptHelper().getService(com.ezpizee.aem.services.AppConfig.class);
        SlingSettingsService sss = getSlingScriptHelper().getService(SlingSettingsService.class);
        Client client = new Client();
        if (appConfig != null && appConfig.isValid()) {
            AccessToken accessToken = getSlingScriptHelper().getService(AccessToken.class);
            if (accessToken != null) {
                accessToken.load(CookieUtil.getAuthCookie(getRequest()), getRequest().getSession());
            }
            htmlContent = client.getContent(HostName.getCDNServer(appConfig.getEnv())+ADMIN_HTML.replace("{version}", getVersion(client)));
            String replace1 = "<body";
            String replace2 = "<head>";
            htmlContent = htmlContent.replace(replace1, replaceBodyStr(replace1, sss)).replace(replace2, replacePlatformJSStr(replace2));
        }
        else {
            htmlContent = client.getContent(HostName.getProdCDNServer()+ADMIN_HTML.replace("{version}", getVersion(client)));
        }
    }

    public String getHtmlContent() {return htmlContent;}

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

    private String getVersion(Client client) {
        String verion = getResource().getValueMap().get("version", String.class);
        if (StringUtils.isEmpty(verion)) {
            String data = client.getContent(URL_ADMINUI_VERSION);
            if (DataUtil.isJsonString(data)) {
                JsonArray array = DataUtil.toJsonArray(data);
                verion = array.get(array.size()-1).getAsJsonObject().get("value").getAsString();
            }
        }
        return verion;
    }
}
