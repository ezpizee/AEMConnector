package com.ezpizee.aem.datasource;


import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.ezpizee.aem.utils.FileUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomSelectOptions extends com.ezpizee.aem.datasource.DataSource {

    public void loadData(ResourceResolver resolver, Config dsCfg, List<Map<String, Object>> options) {

        if (resolver != null) {
            final String configRoot = dsCfg.get("configRoot");
            if (StringUtils.isNotEmpty(configRoot)) {
                final Resource configRootResource = resolver.getResource(configRoot);
                if (configRootResource == null) {
                    getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
                    return;
                }

                if (configRoot.endsWith(".json")) {
                    String data = FileUtil.getContentAsStringFromFileOnCRX(resolver, configRoot);
                    try {
                        JsonParser parser = new JsonParser();
                        JsonArray array = parser.parse(data).getAsJsonArray();
                        if (array != null && array.size() > 0) {
                            for (int i = 0; i < array.size(); i++) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("text", array.get(i).getAsJsonObject().get("text").getAsString());
                                map.put("value", array.get(i).getAsJsonObject().get("value").getAsString());
                                options.add(map);
                            }
                        }
                    }
                    catch (JsonSyntaxException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                else {
                    for (Iterator<Resource> it = configRootResource.listChildren(); it.hasNext();) {
                        options.add(it.next().getValueMap());
                    }
                }
            }
        }
    }
}
