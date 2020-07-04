package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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

    public static Resource getJCRContentResource(ResourceResolver resolver, Resource resource) {
        final String[] parts = resource.getPath().split("/" + Constants.NODE_JCR_CONTENT);
        final String jcrPath = parts[0] + "/" + Constants.NODE_JCR_CONTENT;
        return resolver.getResource(jcrPath);
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
