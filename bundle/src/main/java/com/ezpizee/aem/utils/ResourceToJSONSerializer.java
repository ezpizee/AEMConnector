package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * For JSON serialization of jcr properties
 *
 * @author Sothea Nim <sothea.nim@webconsol.com>
 */

public abstract class ResourceToJSONSerializer {

    public static JSONObject create(final Resource resource, final int maxRecursionLevels) throws Exception {
        return create(resource, 0, maxRecursionLevels);
    }

    private static JSONObject create(final Resource resource,
                                     final int currentRecursionLevel,
                                     final int maxRecursionLevels) throws Exception {

        final ValueMap props = resource.getValueMap();
        final JSONObject obj = new JSONObject();

        // the node's actual properties
        for (Map.Entry prop : props.entrySet()) {
            if (prop.getValue() != null) {
                createProperty(obj, props, prop.getKey().toString(), prop.getValue());
            }
        }

        // the child nodes
        if (recursionLevelActive(currentRecursionLevel, maxRecursionLevels)) {
            final Iterator<Resource> children = resource.listChildren();
            while (children.hasNext()) {
                final Resource n = children.next();
                createSingleResource(n, obj, currentRecursionLevel, maxRecursionLevels);
            }
        }

        return obj;
    }

    private static final Locale DATE_FORMAT_LOCALE = Locale.US;

    private static final DateFormat CALENDAR_FORMAT = new SimpleDateFormat(Constants.ECMA_DATE_FORMAT, DATE_FORMAT_LOCALE);

    private static synchronized String format(final Calendar date) {
        return CALENDAR_FORMAT.format(date.getTime());
    }

    private static Object getValue(final Object value) {
        if (value instanceof InputStream) {
            // input stream is already handled
            return 0;
        } else if (value instanceof Calendar) {
            return format((Calendar) value);
        } else if (value instanceof Boolean) {
            return value;
        } else if (value instanceof Long) {
            return value;
        } else if (value instanceof Integer) {
            return value;
        } else if (value instanceof Double) {
            return value;
        } else {
            return value.toString();
        }
    }

    private static void createSingleResource(final Resource n,
                                             final JSONObject parent,
                                             final int currentRecursionLevel,
                                             final int maxRecursionLevels) throws Exception {
        if (recursionLevelActive(currentRecursionLevel, maxRecursionLevels)) {
            parent.put(n.getName(), create(n, currentRecursionLevel + 1, maxRecursionLevels));
        }
    }

    private static boolean recursionLevelActive(final int currentRecursionLevel, final int maxRecursionLevels) {
        return maxRecursionLevels < 0 || currentRecursionLevel < maxRecursionLevels;
    }

    private static void createProperty(final JSONObject obj, final ValueMap valueMap, final String key, final Object value) throws Exception {
        Object[] values = null;
        if (value.getClass().isArray()) {
            values = (Object[]) value;
            // write out empty array
            if (values.length == 0) {
                obj.put(key, new JSONArray());
                return;
            }
        }

        // special handling for binaries: we dump the length and not the data!
        if (value instanceof InputStream || (values != null && values[0] instanceof InputStream)) {
            // TODO for now we mark binary properties with an initial colon in
            // their name
            // (colon is not allowed as a JCR property name)
            // in the name, and the value should be the size of the binary data
            if (values == null) {
                obj.put(":" + key, getLength(valueMap, -1, key, (InputStream) value));
            } else {
                final JSONArray result = new JSONArray();
                for (int i = 0; i < values.length; i++) {
                    result.add(getLength(valueMap, i, key, (InputStream) values[i]));
                }
                obj.put(":" + key, result);
            }
            return;
        }

        if (!value.getClass().isArray()) {
            obj.put(key, getValue(value));
        } else if (values != null) {
            final JSONArray result = new JSONArray();
            for (Object v : values) {
                result.add(getValue(v));
            }
            obj.put(key, result);
        }
    }

    private static long getLength(final ValueMap valueMap, final int index, final String key, final InputStream stream) {
        try { stream.close(); } catch (IOException ignore) { }
        long length = -1;
        if (valueMap != null) {
            if (index == -1) {
                length = valueMap.get(key, length);
            } else {
                Long[] lengths = valueMap.get(key, Long[].class);
                if (lengths != null && lengths.length > index) {
                    length = lengths[index];
                }
            }
        }
        return length;
    }
}