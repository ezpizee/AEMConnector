package com.ezpizee.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.HostName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VueSPA extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(VueSPA.class);
    private static final String INSTALL_HTML = "/install/html/index.aem.html";
    private static final String ADMIN_HTML = "/adminui/html/index.aem.html";
    private String content;

    @Override
    public void activate() throws Exception {

        final AppConfig appConfig = getSlingScriptHelper().getService(AppConfig.class);
        if (appConfig != null) {
            appConfig.load(getRequest().getSession());
            if (appConfig.isValid() && appConfig.hasBearerToken()) {
                loadContent(appConfig, ADMIN_HTML);
            }
            else {
                loadContent(appConfig, INSTALL_HTML);
            }
        }
    }

    private void loadContent(AppConfig appConfig, String uri) {
        String cdnServer = HostName.getCDNServer(appConfig.getEnv());
        Client client = new Client(appConfig, true, true);
        Response response = client.get(cdnServer + uri);
        content = response.getDataAsString();
    }

    public String getContent() {
        return content;
    }
}
