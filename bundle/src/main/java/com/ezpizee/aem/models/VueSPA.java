package com.ezpizee.aem.models;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.HostName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VueSPA extends BaseModel {

    private static final String INSTALL_HTML = "/install/html/index.aem.html";
    private static final String ADMIN_HTML = "/adminui/html/index.aem.html";
    private String content;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void activate() throws Exception {
        content = null;
        final AppConfig appConfig = getSlingScriptHelper().getService(AppConfig.class);
        if (appConfig != null) {
            appConfig.load(getAuthCookie(), getRequest().getSession());
            if (appConfig.isValid() && appConfig.hasBearerToken()) {
                loadContent(appConfig.getEnv(), appConfig, ADMIN_HTML);
            }
            else {
                loadContent(appConfig.getEnv(), appConfig, INSTALL_HTML);
            }
        }
    }

    private void loadContent(String env, AppConfig appConfig, String uri) {
        if (content == null) {
            String cdnServer = HostName.getCDNServer(env);
            Client client = new Client(appConfig, true, true);
            Response response = client.get(cdnServer + uri);
            if (response.isError()) {
                content = null;
                if ("local".equals(appConfig.getEnv())) {
                    loadContent("dev", appConfig, uri);
                }
                else if ("dev".equals(appConfig.getEnv())) {
                    loadContent("stage", appConfig, uri);
                }
                else if ("stage".equals(appConfig.getEnv())) {
                    loadContent("prod", appConfig, uri);
                }
                else {
                    content = "<html><head><title>Connection Error</title></head><body>"+
                        "<h1>Please make sure you are connected to the internet to be able to run Ezpizee application.</h1>"+
                        "</body></html>";
                }
            }
            else {
                content = response.getDataAsString();
            }
        }
    }

    public String getContent() { return content; }
}
