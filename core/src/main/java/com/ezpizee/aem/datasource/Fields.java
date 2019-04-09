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
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fields extends BaseProperties {

    protected String dataPath = "data/fields.json";

    @Override
    public void activate() throws Exception {
        List<Resource> fakeResourceList = new ArrayList<>();
        getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        ValueMap vm = new ValueMapDecorator(new HashMap<>());
        vm.put("value", "");
        vm.put("text", "None");
        fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));

        String jsonStr = ConfigUtil.getResource(this.dataPath);
        if (StringUtils.isNotEmpty(jsonStr)) {
            final JSONArray fieldsList = DataUtil.toJSONArray(jsonStr);
            if (!fieldsList.isEmpty()) {
                for (int i = 0; i < fieldsList.size(); i++) {
                    final JSONObject field = (JSONObject)fieldsList.get(i);
                    vm = new ValueMapDecorator(new HashMap<>());
                    vm.put("value", field.get("key"));
                    vm.put("text", field.get("value"));
                    fakeResourceList.add(new ValueMapResource(getResourceResolver(), new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));
                }
            }
        }

        DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
        getRequest().setAttribute(DataSource.class.getName(), ds);
    }
}
