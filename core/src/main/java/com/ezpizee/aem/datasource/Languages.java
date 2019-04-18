package com.ezpizee.aem.datasource;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.models.BaseProperties;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Languages extends BaseProperties {

    @Override
    public void activate() throws Exception {
        List<Resource> fakeResourceList = new ArrayList<>();
        getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        ValueMap vm = new ValueMapDecorator(new HashMap<>());
        vm.put("value", "");
        vm.put("text", "None");
        fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));

        Resource resource = getResourceResolver().getResource("/libs/wcm/core/resources/languages");
        if (resource != null) {
            Iterator<Resource> ir = resource.listChildren();
            while(ir.hasNext()) {
                Resource child = ir.next();
                ValueMap valueMap = child.getValueMap();
                if (valueMap.containsKey("country") && valueMap.containsKey("language") && !valueMap.get("country").toString().equals("*")) {
                    vm = new ValueMapDecorator(new HashMap<>());
                    vm.put("value", child.getName());
                    vm.put("text", valueMap.get("language") + " - " + valueMap.get("country"));
                    fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));
                }
            }
        }

        DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
        getRequest().setAttribute(DataSource.class.getName(), ds);
    }
}
