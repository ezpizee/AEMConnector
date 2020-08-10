package com.ezpizee.aem.models;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.utils.CookieUtil;
import com.ezpizee.aem.utils.FileUtil;
import com.ezpizee.aem.utils.HostName;
import com.ezpizee.aem.utils.RunModesUtil;
import org.apache.sling.settings.SlingSettingsService;

public class VueSPAAdmin extends BaseModel {

    private static final String ADMIN_HTML = "/adminui/html/index.aem.html";
    private String htmlContent;

    @Override
    public void exec() {
        com.ezpizee.aem.services.AppConfig appConfig = getSlingScriptHelper().getService(com.ezpizee.aem.services.AppConfig.class);
        SlingSettingsService sss = getSlingScriptHelper().getService(SlingSettingsService.class);
        String env = RunModesUtil.env(sss);
        Client client = new Client();
        if (appConfig != null && appConfig.isValid()) {
            AccessToken accessToken = getSlingScriptHelper().getService(AccessToken.class);
            if (accessToken != null) {
                accessToken.load(CookieUtil.getAuthCookie(getRequest()), getRequest().getSession());
            }
            htmlContent = client.getContent(HostName.getCDNServer(appConfig.getEnv())+ADMIN_HTML);
            String replace1 = "<body";
            String replace2 = "<head>";
            htmlContent = htmlContent.replace(replace1, replaceBodyStr(replace1, sss)).replace(replace2, replacePlatformJSStr(replace2));
        }
        else {
            htmlContent = client.getContent(HostName.getCDNServer(env)+ADMIN_HTML);
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
}
