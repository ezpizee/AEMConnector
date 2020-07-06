package com.ezpizee.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.utils.HostName;
import org.apache.commons.lang3.StringUtils;

public class VueSPA extends WCMUsePojo {

    private String content;

    @Override
    public void activate() throws Exception {
        final AppConfig appConfig = new AppConfig(getCurrentPage().getContentResource());
        if (StringUtils.isEmpty(content) && appConfig.isValid()) {
            String cdnServer = HostName.getCDNServer(appConfig.getEnv());
            Client client = new Client(appConfig, true, true);
            Response response = client.get(cdnServer+"/adminui/html/admin.html");
            content = response.getDataAsString();
        }
    }

    public String getContent() {
        return content;
    }
}
