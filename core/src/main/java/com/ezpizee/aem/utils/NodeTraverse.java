package com.ezpizee.aem.utils;

import net.minidev.json.JSONArray;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class NodeTraverse
{
    private static final String STRING_LIST = "string_list";
    private static final String RESOURCE_LIST = "resource_list";
    private static final String JSON_ARRAY = "jsonarray";
    private String getAs = "";
    private final Resource rootResource;
    private final ResourceFilter resourceFilter;
    private List<String> stringList;
    private List<Resource> resourceList;
    private JSONArray jsonArrayList;

    public NodeTraverse(final Resource resource, final ResourceFilter resourceFilter) {
        this.rootResource = resource;
        this.resourceFilter = resourceFilter;
        this.stringList = new ArrayList<>();
        this.resourceList = new ArrayList<>();
        this.jsonArrayList = new JSONArray();
    }

    public List<String> getAsStringList() {
        this.getAs = STRING_LIST;
        traverse(this.rootResource);
        return stringList;
    }

    public List<Resource> getAsResourceList() {
        this.getAs = RESOURCE_LIST;
        traverse(this.rootResource);
        return resourceList;
    }

    public JSONArray getAsJSONArray() {
        this.getAs = JSON_ARRAY;
        traverse(this.rootResource);
        return jsonArrayList;
    }

    private void traverse(final Resource resource) {
        if (resource != null) {
            if (resourceFilter.execute(resource)) {
                if (STRING_LIST.equals(getAs)) {
                    stringList.add(resource.getPath());
                }
                else if (RESOURCE_LIST.equals(getAs)) {
                    resourceList.add(resource);
                }
                else if (JSON_ARRAY.equals(getAs)) {
                    jsonArrayList.add(resource.getPath());
                }
            }
            if (resource.hasChildren()) {
                Iterable<Resource> list = resource.getChildren();
                for (Resource child : list) {
                    traverse(child);
                }
            }
        }
    }
}
