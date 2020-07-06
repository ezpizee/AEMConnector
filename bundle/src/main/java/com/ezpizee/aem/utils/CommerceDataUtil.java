package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.models.AppConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.day.cq.wcm.api.NameConstants.PN_TITLE;

public class CommerceDataUtil {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final JsonObject data;
    private Map<String, Object> props;
    private String endpoint;

    public CommerceDataUtil() {data = new JsonObject();}

    public JsonObject fetch(final AppConfig appConfig, final Map<String, Object> props) {
        if (appConfig.isValid() && props != null && !props.isEmpty()) {
            this.props = props;
            endpoint = props.getOrDefault(Constants.KEY_ENDPOINT, StringUtils.EMPTY).toString();
            if (StringUtils.isNotEmpty(endpoint)) {

                formatEndpoint();

                if (isList() || isAsset()) {
                    Client client = new Client(appConfig);
                    Response response = client.get(endpoint);
                    if (response.isNotError()) {
                        data.add(Constants.KEY_LIST, response.getDataAsJsonArray());
                        if (response.hasData(Constants.KEY_PAGINATION)) {
                            data.add(Constants.KEY_PAGINATION, response.getDataAsJsonObject());
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
                        if (response.isNotError()) {
                            // For product form > product type > product attribute fields
                            if (response.getDataAsJsonObject().has("product_type") &&
                                ((JsonObject)response.getDataAsJsonObject().get("product_type")).has("product_attrs")
                            ) {
                                JsonArray prodAttrs = response.getDataAsJsonObject().get("product_type").getAsJsonObject().get("product_attrs").getAsJsonArray();
                                if (prodAttrs != null && prodAttrs.size() > 0) {
                                    for (int i = 0; i < prodAttrs.size(); i++) {
                                        JsonObject jsonObject = (JsonObject) prodAttrs.get(i);
                                        if (jsonObject != null && jsonObject.has("field_type")) {
                                            jsonObject.add("product_attr_field", new JsonPrimitive("field_types/form-"+jsonObject.get("field_type")));
                                            prodAttrs.set(i, jsonObject);
                                        }
                                    }
                                    ((JsonObject)response.getDataAsJsonObject().get("product_type")).add("product_attrs", prodAttrs);
                                }
                            }

                            data.add(Constants.KEY_ITEM_DATA, response.getDataAsJsonObject());
                        }
                        else {
                            LOG.debug("Response is invalid for item by id");
                        }
                    }
                    data.add(Constants.KEY_FIELD_TYPES, DataUtil.toJsonArray(ConfigUtil.getData("fieldtypes")));
                }
                data.add(Constants.KEY_PAGE_TITLE, new JsonPrimitive(props.getOrDefault(PN_TITLE, StringUtils.EMPTY).toString()));
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
            data.add(key, new JsonPrimitive(props.get(key).toString()));
        }
    }

    private void path(String key) {
        if (props.containsKey(key)) {
            data.add(key, new JsonPrimitive(String.format(Constants.CONTENT_PATH_FORMAT, props.get(key) + ".ezpz.json")));
        }
    }

    private void formatEndpoint() {
        if (props.containsKey(Constants.KEY_REST_API_URI_PARAMS)) {
            JsonObject params = null;
            Object object = props.get(Constants.KEY_REST_API_URI_PARAMS);
            if (object instanceof String) {
                String str = (String)props.get(Constants.KEY_REST_API_URI_PARAMS);
                if (StringUtils.isNotEmpty(str)) {
                    params = DataUtil.toJsonObject(str);
                }
            }
            else if (object instanceof JsonObject) {
                params = (JsonObject)object;
            }
            if (params != null) {
                for (String key : params.keySet()) {
                    endpoint = endpoint.replace("{"+key+"}", params.get(key).getAsString());
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
                    JsonObject fieldKeys = new JsonObject();
                    JsonObject fieldLabels = new JsonObject();
                    for (String jsonStr : list) {
                        if (StringUtils.isNotEmpty(jsonStr)) {
                            JsonObject obj = DataUtil.toJsonObject(jsonStr);
                            fieldKeys.add(obj.get(key).getAsString(), new JsonPrimitive(true));
                            fieldLabels.add(obj.get(key).getAsString(), new JsonPrimitive("LABEL_"+(obj.get(key).getAsString()).toUpperCase()));
                        }
                    }
                    data.add(Constants.KEY_FIELD_KEYS, fieldKeys);
                    data.add(Constants.KEY_FIELD_LABELS, fieldLabels);
                }
                else {
                    JsonObject keys = new JsonObject();
                    for (String jsonStr : list) {
                        if (StringUtils.isNotEmpty(jsonStr)) {
                            JsonObject obj = DataUtil.toJsonObject(jsonStr);
                            keys.add(obj.get(key).getAsString(), new JsonPrimitive(true));
                        }
                    }
                    data.add(propName, keys);
                }
            }
        }
    }

    private boolean isAsset() { return Constants.APP_RESOURCE_ASSET.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY)); }

    private boolean isList() { return Constants.APP_RESOURCE_LIST.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY)); }

    private boolean isForm() { return Constants.APP_RESOURCE_FORM.equals(props.getOrDefault(Constants.PROP_SLING_RESOURCETYPE, StringUtils.EMPTY)); }
}
