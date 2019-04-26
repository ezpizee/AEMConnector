package com.ezpizee.aem.models;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.utils.DataUtil;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

public class CommerceData extends BaseProperties {

    @Override
    public void activate() throws Exception {
        if (getAppConfig().isValid()) {
            final ValueMap props = getResource().getValueMap();
            String endpoint = props.get("endpoint", StringUtils.EMPTY);
            String editId = getRequest().getParameter("edit_id");
            if (StringUtils.isNotEmpty(endpoint)) {
                if (isList(props)) {
                    Client client = new Client(getAppConfig());
                    Response response = client.get(endpoint);
                    if (response.isSuccess()) {
                        data.put("list", response.getDataAsJSONArray());
                    }
                    else {
                        log.error("Response is invalid for list request");
                    }
                }
                else if (isForm(props)) {
                    if (StringUtils.isNotEmpty(editId)) {
                        Client client = new Client(getAppConfig());
                        Response response = client.get(endpoint.replace("{id}", editId).replace("{edit_id}", editId));
                        if (response.isSuccess()) {
                            data.put("data", response.getDataAsJSONObject());
                        }
                        else {
                            log.error("Response is invalid for item by id");
                        }
                    }
                    data.put("formAction", String.format(Constants.CONTENT_PATH_FORMAT, getCurrentPage().getPath() + ".html"));
                }
                data.put("targetPage", String.format(Constants.CONTENT_PATH_FORMAT, props.get("targetPage", StringUtils.EMPTY) + ".html"));
                data.put("method", props.get("method", StringUtils.EMPTY));
                data.put("delete_api_endpoint", props.get("delete_api_endpoint", StringUtils.EMPTY));
                fieldKeys(props, "actions", "action");
                fieldKeys(props, "fields", "field");
            }
            else {
                log.error("endpoint is empty");
            }
        }
        else {
            log.error("appConfig is invalid");
        }
    }

    private void fieldKeys(ValueMap props, String propName, String key) {
        if (props.containsKey(propName)) {
            String[] list = props.get(propName, String[].class);
            if (list != null) {
                JSONObject keys = new JSONObject();
                for (String jsonStr : list) {
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JSONObject obj = DataUtil.toJSONObject(jsonStr);
                        keys.put((String)obj.get(key), obj.get(key));
                    }
                }
                data.put(propName, keys);
            }
        }
    }

    private boolean isList(ValueMap props) {
        return Constants.APP_RESOURCE_LIST.equals(props.get(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY));
    }

    private boolean isForm(ValueMap props) {
        return Constants.APP_RESOURCE_FORM.equals(props.get(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY));
    }
}
