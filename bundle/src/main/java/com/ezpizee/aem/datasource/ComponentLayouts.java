package com.ezpizee.aem.datasource;


import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ComponentLayouts extends com.ezpizee.aem.datasource.DataSource {

    protected void loadData(ResourceResolver resolver, Config dsCfg, List<Map<String, Object>> options) {

        if (resolver != null) {
            final String configRoot = getLayoutPath(dsCfg);
            if (StringUtils.isNotEmpty(configRoot)) {
                final Resource configRootResource = resolver.getResource(configRoot);
                if (configRootResource == null) {
                    getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
                    return;
                }

                for (Iterator<Resource> it = configRootResource.listChildren(); it.hasNext();) {
                    String layout = it.next().getName();
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("value", layout);
                    obj.put("text", layout.replace(".html", ""));
                    options.add(obj);
                }
            }
        }
    }

    private String getLayoutPath(Config dsCfg) {
        if (dsCfg != null && dsCfg.getParentResource() != null) {
            String s = dsCfg.getParentResource().getPath().replace("/mnt/override/apps/", "/apps/");
            String[] a = s.split("/cq:dialog");
            return a[0] + "/layouts";
        }
        return StringUtils.EMPTY;
    }
}
