package com.ezpizee.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import org.apache.commons.lang3.StringUtils;

public class InstallSPA extends WCMUsePojo {

    private boolean validAppConfig;
    private AppConfig appConfig;
    private String content;

    @Override
    public void activate() throws Exception {
        if (appConfig == null) {
            appConfig = new AppConfig(getCurrentPage().getContentResource());
            validAppConfig = appConfig.isValid();
            String cdnServer;
            if (Constants.ENVIRONMENTS.get(4).equals(appConfig.getEnv())) {
                cdnServer = "https://cdn.ezpz.solutions";
            }
            else {
                cdnServer = "http://"+appConfig.getEnv()+"-cdn.ezpz.solutions";
            }
            if (StringUtils.isEmpty(content) && !validAppConfig) {
                Client client = new Client(appConfig, true, true);
                Response response = client.get(cdnServer+"/adminui/html/install.html");
                content = response.getDataAsString();
            }
        }
    }

    public boolean isValidAppConfig() {return validAppConfig;}

    public AppConfig getAppConfig() {return appConfig;}

    public String getContent() {
        return content;
    }
}
