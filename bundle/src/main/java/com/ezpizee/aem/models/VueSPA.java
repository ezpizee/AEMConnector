package com.ezpizee.aem.models;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.utils.CookieUtil;
import com.ezpizee.aem.utils.HostName;
import com.ezpizee.aem.utils.RunModesUtil;
import org.apache.sling.settings.SlingSettingsService;

public class VueSPA extends BaseModel {

    private static final String INSTALL_HTML = "/install/html/index.aem.html";
    private static final String ADMIN_HTML = "/adminui/html/index.aem.html";
    private String htmlContent, installHtmContent;

    @Override
    public void activate() throws Exception {
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
                htmlContent = client.getContent(HostName.getCDNServer(appConfig.getEnv())+ADMIN_HTML)
                    .replace("<body", "<body data-run-mode='"+(RunModesUtil.isAuthor(sss)?"author":"publish")+"'");
            }
            else {
                htmlContent = installHtmContent;
            }
        }
    }

    public String getHtmlContent() {return htmlContent;}
    public String getInstallHtmContent() {return installHtmContent;}
}
