package com.ezpizee.aem.datasource;


import com.adobe.granite.ui.components.Config;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.utils.DataUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ezpizee.aem.Constants.URL_ADMINUI_VERSION;

public class EzpizeeVersions extends DataSource {

    protected void loadData(ResourceResolver resolver, Config dsCfg, List<Map<String, Object>> options) {

        if (resolver != null) {

            Client client = new Client();
            String data = client.getContent(URL_ADMINUI_VERSION);
            if (DataUtil.isJsonString(data)) {
                JsonArray array = DataUtil.toJsonArray(data);
                for (int i = 0; i < array.size(); i++) {
                    JsonObject object = array.get(i).getAsJsonObject();
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("value", object.get("value").getAsString());
                    obj.put("text", object.get("text").getAsString());
                    options.add(obj);
                }
            }
        }
    }
}
