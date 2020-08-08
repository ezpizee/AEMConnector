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

                String replace1 = "<body";
                String replace2 = "<head>";

                htmlContent = client.getContent(HostName.getCDNServer(appConfig.getEnv())+ADMIN_HTML)
                    .replace(replace1, replaceBodyStr(replace1, sss))
                    .replace(replace2, replacePlatformJSStr(replace2, sss));
            }
            else {
                htmlContent = installHtmContent;
            }
        }
    }

    public String getHtmlContent() {return htmlContent;}
    public String getInstallHtmContent() {return installHtmContent;}

    private String replaceBodyStr(String pattern, SlingSettingsService sss) {
        return pattern+" data-run-mode='"+(RunModesUtil.isAuthor(sss)?"author":"publish")+"'";
    }

    private String replacePlatformJSStr(String pattern, SlingSettingsService sss) {
        StringBuilder sb = new StringBuilder();
        sb.append(pattern);
        if (RunModesUtil.isPublish(sss)) {
            sb.append("<script>")
                .append("window.EzpizeeOverrideEndpoints={};")
                .append("window.EzpizeeOverrideEndpoints.csrfToken=\"/libs/granite/csrf/token.json\";")
                .append("</script>");
        }
        return sb.toString();
    }
}
