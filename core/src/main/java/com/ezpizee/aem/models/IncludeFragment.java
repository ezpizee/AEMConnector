package com.ezpizee.aem.models;

import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;

public class IncludeFragment extends BaseProperties {

    private static final String PROP_FRAGMENT = "fragment";

    @Override
    public void activate() throws Exception {
        resolverFactory = getSlingScriptHelper().getService(ResourceResolverFactory.class);
        ValueMap props = getResource().getValueMap();
        if (props.containsKey(PROP_FRAGMENT)) {
            Resource fragmentResource = getResourceResolver().getResource(props.get(PROP_FRAGMENT, "") + "/jcr:content/data/master");
            if (fragmentResource != null) {
                this.loadData(fragmentResource);
            }
        }
    }

    public JSONObject mockedData() {
        return null;
    }
}
