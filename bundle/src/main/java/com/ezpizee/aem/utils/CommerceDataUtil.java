package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.models.AppConfig;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CommerceDataUtil {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final JSONObject data;
    private Map<String, Object> props;
    private String endpoint;

    public CommerceDataUtil() {data = new JSONObject();}

    public JSONObject fetch(final AppConfig appConfig, final Map<String, Object> props) {
        if (appConfig.isValid() && props != null && !props.isEmpty()) {
            this.props = props;
            endpoint = props.getOrDefault(Constants.KEY_ENDPOINT, StringUtils.EMPTY).toString();
            if (StringUtils.isNotEmpty(endpoint)) {

                formatEndpoint();

                if (isList() || isAsset()) {
                    Client client = new Client(appConfig);
                    Response response = client.get(endpoint);
                    if (response.isSuccess()) {
                        data.put(Constants.KEY_LIST, response.getDataAsJSONArray());
                        if (response.hasData(Constants.KEY_PAGINATION)) {
                            data.put(Constants.KEY_PAGINATION, response.getDataAsJSONObject());
                        }
                    }
                    else {
                        LOG.debug("Response is invalid for list request");
                    }
                }

                else if (isForm()) {
                    String editId = props.getOrDefault(Constants.KEY_EDIT_ID, props.getOrDefault(Constants.KEY_ID, StringUtils.EMPTY)).toString();
                    if (StringUtils.isNotEmpty(editId)) {
                        Client client = new Client(appConfig);
                        Response response = client.get(endpoint.replace("{"+Constants.KEY_ID+"}", editId).replace("{"+Constants.KEY_EDIT_ID+"}", editId));
                        if (response.isSuccess()) {
                            // For product form > product type > product attribute fields
                            if (response.getDataAsJSONObject().containsKey("product_type") &&
                                ((JSONObject)response.getDataAsJSONObject().get("product_type")).containsKey("product_attrs")
                            ) {
                                JSONArray prodAttrs = (JSONArray) ((JSONObject)response.getDataAsJSONObject().get("product_type")).get("product_attrs");
                                if (prodAttrs != null && !prodAttrs.isEmpty()) {
                                    for (int i = 0; i < prodAttrs.size(); i++) {
                                        JSONObject jsonObject = (JSONObject) prodAttrs.get(i);
                                        if (jsonObject != null && jsonObject.containsKey("field_type")) {
                                            jsonObject.put("product_attr_field", "field_types/form-"+jsonObject.get("field_type"));
                                            prodAttrs.set(i, jsonObject);
                                        }
                                    }
                                    ((JSONObject)response.getDataAsJSONObject().get("product_type")).put("product_attrs", prodAttrs);
                                }
                            }

                            data.put(Constants.KEY_ITEM_DATA, response.getDataAsJSONObject());
                        }
                        else {
                            LOG.debug("Response is invalid for item by id");
                        }
                    }
                    data.put(Constants.KEY_FIELD_TYPES, DataUtil.toJSONArray(ConfigUtil.getData("fieldtypes")));
                }
                data.put(Constants.KEY_PAGE_TITLE, props.getOrDefault(com.day.cq.wcm.api.NameConstants.PN_TITLE, StringUtils.EMPTY));
                setData(Constants.KEY_METHOD);
                setData(Constants.KEY_DELETE_API_ENDPOINT);
                setData(Constants.KEY_DISPLAY_TEMPLATE);
                fieldKeys(Constants.KEY_ACTIONS, "action");
                fieldKeys(Constants.KEY_FIELDS, "field");
                path(Constants.KEY_FOLDER_FORM_AGE);
                path(Constants.KEY_FILE_FORM_PAGE);
                path(Constants.KEY_LIST_PAGE);
                path(Constants.KEY_FORM_PAGE);
                path(Constants.KEY_FORM_ACTION);
            }
            else {
                LOG.debug("endpoint is empty");
            }
        }
        else {
            LOG.debug("appConfig is invalid");
        }
        return data;
    }

    private void setData(String key) {
        if (props.containsKey(key)) {
            data.put(key, props.get(key));
        }
    }

    private void path(String key) {
        if (props.containsKey(key)) {
            data.put(key, String.format(Constants.CONTENT_PATH_FORMAT, props.get(key) + ".ezpz.json"));
        }
    }

    private void formatEndpoint() {
        if (props.containsKey(Constants.KEY_REST_API_URI_PARAMS)) {
            JSONObject params = null;
            Object object = props.get(Constants.KEY_REST_API_URI_PARAMS);
            if (object instanceof String) {
                String str = (String)props.get(Constants.KEY_REST_API_URI_PARAMS);
                if (StringUtils.isNotEmpty(str)) {
                    params = DataUtil.toJSONObject(str);
                }
            }
            else if (object instanceof JSONObject) {
                params = (JSONObject)object;
            }
            if (params != null) {
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
                if (propName.equals(Constants.KEY_FIELDS)) {
                    JSONObject fieldKeys = new JSONObject();
                    JSONObject fieldLabels = new JSONObject();
                    for (String jsonStr : list) {
                        if (StringUtils.isNotEmpty(jsonStr)) {
                            JSONObject obj = DataUtil.toJSONObject(jsonStr);
                            fieldKeys.put((String)obj.get(key), true);
                            fieldLabels.put((String)obj.get(key), "LABEL_"+((String)obj.get(key)).toUpperCase());
                        }
                    }
                    data.put(Constants.KEY_FIELD_KEYS, fieldKeys);
                    data.put(Constants.KEY_FIELD_LABELS, fieldLabels);
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

    private boolean isAsset() { return Constants.APP_RESOURCE_ASSET.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY)); }

    private boolean isList() { return Constants.APP_RESOURCE_LIST.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY)); }

    private boolean isForm() { return Constants.APP_RESOURCE_FORM.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY)); }
}
