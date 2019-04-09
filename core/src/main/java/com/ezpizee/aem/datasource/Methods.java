package com.ezpizee.aem.datasource;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.MethodEnum;
import com.ezpizee.aem.models.BaseProperties;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Methods extends BaseProperties {

    @Override
    public void activate() throws Exception {
        List<Resource> fakeResourceList = new ArrayList<>();
        getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        ValueMap vm = new ValueMapDecorator(new HashMap<>());
        vm.put("value", "");
        vm.put("text", "None");
        fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));

        for (MethodEnum method : MethodEnum.values()) {
            vm = new ValueMapDecorator(new HashMap<>());
            vm.put("value", method.name());
            vm.put("text", method.name());
            fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));
        }

        DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
        getRequest().setAttribute(DataSource.class.getName(), ds);
    }
}
