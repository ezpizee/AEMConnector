package com.ezpizee.aem.models;

import com.ezpizee.aem.utils.ContentScraperUtil;
import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;

public class IncludePage extends BaseProperties {

    private static final String PROP_FRAGMENT = "fragment";
    private String fragment;

    @Override
    public void activate() throws Exception {
        resolverFactory = getSlingScriptHelper().getService(ResourceResolverFactory.class);
        ValueMap props = getResource().getValueMap();
        if (props.containsKey(PROP_FRAGMENT)) {
            Resource fragmentResource = getResourceResolver().getResource(props.get(PROP_FRAGMENT, "") + "/jcr:content/data");
            if (fragmentResource != null) {
                fragment = ContentScraperUtil.getContent(getRequest(), getResponse(), fragmentResource.getPath());
            }
        }
    }

    public String getFragment() { return fragment; }

    public JSONObject mockedData() {
        return null;
    }
}
