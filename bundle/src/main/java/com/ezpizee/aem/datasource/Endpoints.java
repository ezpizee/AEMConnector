package com.ezpizee.aem.datasource;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.models.BaseProperties;
import com.ezpizee.aem.utils.ConfigUtil;
import com.ezpizee.aem.utils.DataUtil;
import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Endpoints extends BaseProperties {

    @Override
    public void activate() throws Exception {
        List<Resource> fakeResourceList = new ArrayList<>();
        getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        ValueMap vm = new ValueMapDecorator(new HashMap<>());
        vm.put("value", "");
        vm.put("text", "None");
        fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));

        if (getAppConfig().isValid()) {
            JSONObject data = DataUtil.toJSONObject(ConfigUtil.getResource("data/endpoints.json"));
            for (String service : data.keySet()) {
                JSONObject actions = (JSONObject)data.get(service);
                for (String action : actions.keySet()) {
                    vm = new ValueMapDecorator(new HashMap<>());
                    String endpoint = (String)(((JSONObject)actions.get(action)).get("uri"));
                    vm.put("value", endpoint);
                    vm.put("text", endpoint);
                    fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));
                }
            }
        }

        DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
        getRequest().setAttribute(DataSource.class.getName(), ds);
    }
}
