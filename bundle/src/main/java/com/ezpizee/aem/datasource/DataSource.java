package com.ezpizee.aem.datasource;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.services.AdminService;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ezpizee.aem.Constants.EZPIZEE_SERVICE;

public class DataSource extends WCMUsePojo {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void activate() throws Exception {
        exec();
    }

    protected void loadData(ResourceResolver resolver, Config dsCfg, List<Map<String, Object>> options) {}

    protected void exec() throws Exception {

        final AdminService adminService = getSlingScriptHelper().getService(AdminService.class);
        final ResourceResolver resolver = adminService != null ? adminService.getResourceResolver(getSubServer()) : null;
        final Config dsCfg = new Config(getResource().getChild("datasource"));
        final List<Map<String, Object>> options = new ArrayList<>();

        if (dsCfg.get("addNone", false)) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("", "");
            options.add(empty);
        }

        loadData(resolver, dsCfg, options);

        @SuppressWarnings("unchecked")
        com.adobe.granite.ui.components.ds.DataSource ds = new SimpleDataSource(new TransformIterator(options.iterator(), new Transformer() {
            public Object transform(Object input) {
                try {
                    Map<String, Object> obj = (Map<String, Object>) input;
                    ValueMap vm = new ValueMapDecorator(new HashMap<>());
                    vm.put("value", obj.get("value"));
                    vm.put("text", obj.get("text"));
                    return new ValueMapResource(resolver, new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        getRequest().setAttribute(com.adobe.granite.ui.components.ds.DataSource.class.getName(), ds);
    }

    protected String getSubServer() {return EZPIZEE_SERVICE;}
}
