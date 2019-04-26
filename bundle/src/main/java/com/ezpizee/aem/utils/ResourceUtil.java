package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sothea Nim
 *
 * Utility class for handling resource
 */

public class ResourceUtil {

    private ResourceUtil() {}

    public static JSONObject toJSONObject(Resource resource)
        throws Exception {
        return toJSONObject(resource, -1);
    }

    public static JSONObject toJSONObject(final Resource resource, final int levels)
        throws Exception {
        return toJSONObject(resource, levels, Integer.MAX_VALUE);
    }

    public static JSONObject toJSONObject(final Resource resource, final int levels, final long maxResources)
        throws Exception {
        ResourceTraversor traversor = new ResourceTraversor(resource, levels, maxResources);
        return traversor.getJSONObject();
    }

    public static boolean isAsset(Resource resource) {
        if (resource != null) {
            return is(resource, Constants.PROP_JCR_PRIMARYTYPE, Constants.PROP_DAMASSET);
        }
        return false;
    }

    public static boolean isCQPage(Resource resource) {
        if (resource != null) {
            return is(resource, Constants.PROP_JCR_PRIMARYTYPE, Constants.PROP_CQPAGE);
        }
        return false;
    }

    public static boolean isFolder(Resource resource) {
        if (resource != null) {
            List<String> values = new ArrayList<>();
            values.add(Constants.PROP_SLING_FOLDER);
            values.add(Constants.PROP_SLING_ORDER_FOLDER);
            values.add(Constants.PROP_NT_FOLDER);
            return is(resource, Constants.PROP_JCR_PRIMARYTYPE, values);
        }
        return false;
    }

    private static boolean is(Resource resource, String key, String value) {
        ValueMap valueMap = resource.getValueMap();
        return valueMap.get(key, "").equals(value);
    }

    private static boolean is(Resource resource, String key, List<String> values) {
        ValueMap valueMap = resource.getValueMap();
        return values.contains(valueMap.get(key, ""));
    }
}
