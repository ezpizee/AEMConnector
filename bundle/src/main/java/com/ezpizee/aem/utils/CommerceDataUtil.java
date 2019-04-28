package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.models.AppConfig;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CommerceDataUtil {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final JSONObject data;

    public CommerceDataUtil() {data = new JSONObject();}

    public JSONObject fetch(final AppConfig appConfig, final Map<String, Object> props) {
        if (appConfig.isValid() && props != null && !props.isEmpty()) {
            String endpoint = props.getOrDefault("endpoint", StringUtils.EMPTY).toString();
            String editId = props.getOrDefault("edit_id", StringUtils.EMPTY).toString();
            if (StringUtils.isNotEmpty(endpoint)) {
                if (isList(props)) {
                    Client client = new Client(appConfig);
                    Response response = client.get(endpoint);
                    if (response.isSuccess()) {
                        data.put("list", response.getDataAsJSONArray());
                        if (response.hasData("pagination")) {
                            data.put("pagination", response.getDataAsJSONObject());
                        }
                    }
                    else {
                        log.error("Response is invalid for list request");
                    }
                    data.put("form_page", String.format(Constants.CONTENT_PATH_FORMAT, props.get("currentPagePath")));
                }
                else if (isForm(props)) {
                    if (StringUtils.isNotEmpty(editId)) {
                        Client client = new Client(appConfig);
                        Response response = client.get(endpoint.replace("{id}", editId).replace("{edit_id}", editId));
                        if (response.isSuccess()) {
                            data.put("data", response.getDataAsJSONObject());
                        }
                        else {
                            log.error("Response is invalid for item by id");
                        }
                    }
                    data.put("formAction", String.format(Constants.CONTENT_PATH_FORMAT, props.get("currentPagePath")));
                }
                data.put("targetPage", String.format(Constants.CONTENT_PATH_FORMAT, props.getOrDefault("targetPage", StringUtils.EMPTY) + ".html"));
                data.put("method", props.getOrDefault("method", StringUtils.EMPTY));
                data.put("delete_api_endpoint", props.getOrDefault("delete_api_endpoint", StringUtils.EMPTY));
                data.put("display_template", props.getOrDefault("display_template", StringUtils.EMPTY));
                data.put("page_title", props.getOrDefault(com.day.cq.wcm.api.NameConstants.PN_TITLE, StringUtils.EMPTY));
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
        return data;
    }

    private void fieldKeys(final Map<String, Object> props, String propName, String key) {
        if (props.containsKey(propName)) {
            String[] list = (String[]) props.get(propName);
            if (list != null) {
                JSONObject keys = new JSONObject();
                for (String jsonStr : list) {
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JSONObject obj = DataUtil.toJSONObject(jsonStr);
                        keys.put((String)obj.get(key), true);
                    }
                }
                if (propName.equals("fields")) {
                    JSONObject fieldKeys = new JSONObject();
                    JSONObject fieldLabels = new JSONObject();
                    for (String field : keys.keySet()) {
                        fieldKeys.put(field, true);
                        fieldLabels.put(field, "LABEL_"+field.toUpperCase());
                    }
                    data.put("fieldKeys", fieldKeys);
                    data.put("fieldLabels", fieldLabels);
                }
                else {
                    data.put(propName, keys);
                }
            }
        }
    }

    private boolean isList(final Map<String, Object> props) {
        return Constants.APP_RESOURCE_LIST.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY));
    }

    private boolean isForm(final Map<String, Object> props) {
        return Constants.APP_RESOURCE_FORM.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY));
    }
}
