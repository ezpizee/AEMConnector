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
    private Map<String, Object> props;
    private String endpoint;


    public CommerceDataUtil() {data = new JSONObject();}

    public JSONObject fetch(final AppConfig appConfig, final Map<String, Object> props) {
        if (appConfig.isValid() && props != null && !props.isEmpty()) {
            this.props = props;
            endpoint = props.getOrDefault("endpoint", StringUtils.EMPTY).toString();
            if (StringUtils.isNotEmpty(endpoint)) {

                formatEndpoint();

                if (isList()) {
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
                }

                else if (isForm()) {
                    String editId = props.getOrDefault("edit_id", StringUtils.EMPTY).toString();
                    if (StringUtils.isNotEmpty(editId)) {
                        Client client = new Client(appConfig);
                        Response response = client.get(endpoint.replace("{id}", editId).replace("{edit_id}", editId));
                        if (response.isSuccess()) {
                            data.put("item_data", response.getDataAsJSONObject());
                        }
                        else {
                            log.error("Response is invalid for item by id");
                        }
                    }
                    data.put("formAction", String.format(Constants.CONTENT_PATH_FORMAT, props.get("currentPagePath")));
                }
                data.put("method", props.getOrDefault("method", StringUtils.EMPTY));
                data.put("delete_api_endpoint", props.getOrDefault("delete_api_endpoint", StringUtils.EMPTY));
                data.put("display_template", props.getOrDefault("display_template", StringUtils.EMPTY));
                data.put("page_title", props.getOrDefault(com.day.cq.wcm.api.NameConstants.PN_TITLE, StringUtils.EMPTY));
                fieldKeys("actions", "action");
                fieldKeys("fields", "field");
                path("folder_form_page");
                path("file_form_page");
                path("list_page");
                path("form_page");
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

    private void path(String key) {
        if (props.containsKey(key)) {
            data.put(key, String.format(Constants.CONTENT_PATH_FORMAT, props.get(key) + ".ezpz.json"));
        }
    }

    private void formatEndpoint() {
        if (props.containsKey("rest_api_uri_params")) {
            String str = (String)props.get("rest_api_uri_params");
            if (StringUtils.isNotEmpty(str)) {
                JSONObject params = DataUtil.toJSONObject(str);
                for (String key : params.keySet()) {
                    endpoint = endpoint.replace("{"+key+"}", params.getAsString(key));
                }
            }
        }
    }

    private void fieldKeys(String propName, String key) {
        if (props.containsKey(propName)) {
            Object object = props.get(propName);
            String[] list = new String[]{};
            if (object instanceof String) {
                list = new String[]{(String)object};
            }
            else if (object instanceof String[]) {
                list = (String[])object;
            }
            if (list.length > 0) {
                if (propName.equals("fields")) {
                    JSONObject fieldKeys = new JSONObject();
                    JSONObject fieldLabels = new JSONObject();
                    for (String jsonStr : list) {
                        if (StringUtils.isNotEmpty(jsonStr)) {
                            JSONObject obj = DataUtil.toJSONObject(jsonStr);
                            fieldKeys.put((String)obj.get(key), true);
                            fieldLabels.put((String)obj.get(key), "LABEL_"+((String)obj.get(key)).toUpperCase());
                        }
                    }
                    data.put("fieldKeys", fieldKeys);
                    data.put("fieldLabels", fieldLabels);
                }
                else {
                    JSONObject keys = new JSONObject();
                    for (String jsonStr : list) {
                        if (StringUtils.isNotEmpty(jsonStr)) {
                            JSONObject obj = DataUtil.toJSONObject(jsonStr);
                            keys.put((String)obj.get(key), true);
                        }
                    }
                    data.put(propName, keys);
                }
            }
        }
    }

    private boolean isList() {
        return Constants.APP_RESOURCE_LIST.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY));
    }

    private boolean isForm() {
        return Constants.APP_RESOURCE_FORM.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY));
    }
}
