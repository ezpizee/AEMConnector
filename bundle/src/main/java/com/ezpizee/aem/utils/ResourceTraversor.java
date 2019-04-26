package com.ezpizee.aem.utils;

import net.minidev.json.JSONObject;
import org.apache.sling.api.request.RecursionTooDeepException;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * For traversing resource
 *
 * @author Sothea Nim <sothea.nim@webconsol.com>
 */
public class ResourceTraversor {

    public static final class Entry {
        public final Resource resource;
        public final JSONObject json;

        public Entry(final Resource r, final JSONObject o) {
            this.resource = r;
            this.json = o;
        }
    }

    private long count;

    private long maxResources;

    private final int maxRecursionLevels;

    private final JSONObject startObject;

    private LinkedList<Entry> currentQueue;

    private LinkedList<Entry> nextQueue;

    private final Resource startResource;

    public ResourceTraversor(final Resource resource, final int levels, final long maxResources)
        throws Exception {
        this.maxResources = maxResources;
        this.maxRecursionLevels = levels;
        this.startResource = resource;
        currentQueue = new LinkedList<>();
        nextQueue = new LinkedList<>();
        this.startObject = this.adapt(resource);
    }

    public int collectResources() throws RecursionTooDeepException, Exception {
        return collectChildren(startResource, this.startObject, 0);
    }

    private int collectChildren(final Resource resource, final JSONObject jsonObj, int currentLevel)
        throws Exception {

        if (maxRecursionLevels == -1 || currentLevel < maxRecursionLevels) {
            final Iterator<Resource> children = resource.listChildren();
            while (children.hasNext()) {
                count++;
                final Resource res = children.next();
                // SLING-2320: always allow enumeration of one's children;
                // DOS-limitation is for deeper traversals.
                if (count > maxResources && maxRecursionLevels != 1) {
                    return currentLevel;
                }
                final JSONObject json = collectResource(res, jsonObj);
                nextQueue.addLast(new Entry(res, json));
            }
        }

        while (!currentQueue.isEmpty() || !nextQueue.isEmpty()) {
            if (currentQueue.isEmpty()) {
                currentLevel++;
                currentQueue = nextQueue;
                nextQueue = new LinkedList<>();
            }
            final Entry nextResource = currentQueue.removeFirst();
            final int maxLevel = collectChildren(nextResource.resource, nextResource.json, currentLevel);
            if (maxLevel != -1) {
                return maxLevel;
            }
        }
        return -1;
    }

    private JSONObject collectResource(Resource resource, final JSONObject parent) throws Exception {
        final JSONObject o = adapt(resource);
        parent.put(resource.getName(), o);
        return o;
    }

    private JSONObject adapt(final Resource resource) throws Exception {
        return ResourceToJSONSerializer.create(resource, 0);
    }

    public long getCount() {
        return count;
    }

    public JSONObject getJSONObject() {
        return startObject;
    }
}