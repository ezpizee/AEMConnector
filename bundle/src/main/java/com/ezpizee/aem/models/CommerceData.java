package com.ezpizee.aem.models;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.utils.CommerceDataUtil;
import com.ezpizee.aem.utils.DataUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Map;

public class CommerceData extends BaseProperties {

    @Override
    public void activate() throws Exception {
        Map<String, Object> props = null;
        if (!getResource().getName().equals(Constants.NODE_JCR_CONTENT)) {
            String[] parts = getResource().getPath().split("/"+Constants.NODE_JCR_CONTENT);
            Resource resource = getResourceResolver().getResource(parts[0]+"/"+Constants.NODE_JCR_CONTENT);
            if (resource != null) {
                props = DataUtil.valueMap2Map(resource.getValueMap());
            }
        }
        else {
            props = DataUtil.valueMap2Map(getResource().getValueMap());
        }
        if (props != null) {
            props.put("edit_id", getRequest().getRequestParameterMap().containsKey("edit_id")?getRequest().getParameter("edit_id"): StringUtils.EMPTY);
            props.put("currentPagePath", getCurrentPage().getPath() + ".html");
            CommerceDataUtil commerceDataUtil = new CommerceDataUtil();
            data = commerceDataUtil.fetch(getAppConfig(), props);
        }
    }

    public String getDataAsString() {return data.toString();}
}
