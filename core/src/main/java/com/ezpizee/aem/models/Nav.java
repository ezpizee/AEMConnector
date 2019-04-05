package com.ezpizee.aem.models;

import com.day.cq.wcm.api.Page;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.utils.HashUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;

public class Nav extends BaseProperties {

    @Override
    public void activate() throws Exception {
        final Page adminPage = getPageManager().getPage(Constants.CONTENT_PATH);
        if (adminPage != null) {
            data = new JSONObject();
            final JSONArray items = new JSONArray();
            final Iterator<Page> ip = adminPage.listChildren();
            while (ip.hasNext()) {
                final Page service = ip.next();
                if (notHidden(service)) {
                    final JSONObject item = item(service);
                    final JSONArray children = children(service);
                    if (!children.isEmpty()) {
                        item.put("children", children);
                    }
                    items.add(item);
                }
            }
            data.put("items", items);
        }
    }

    private boolean notHidden(Page page) { return !page.getProperties().get("hideInNav", false); }

    private JSONArray children(Page service) {
        JSONArray list = new JSONArray();
        final Iterator<Page> ip = service.listChildren();
        while (ip.hasNext()) {
            list.add(item(ip.next()));
        }
        return list;
    }

    private JSONObject item(Page page) {
        final JSONObject item = new JSONObject();
        item.put("title", page.getTitle());
        item.put("url", path(page.getPath()));
        Resource jcrRes = page.getContentResource();
        if (jcrRes != null && jcrRes.getValueMap().containsKey("icon")) {
            item.put("icon", jcrRes.getValueMap().get("icon"));
        }
        item.put("id", "item-"+HashUtil.md5(page.getPath()));
        return item;
    }

    private String path(String path) { return String.format(Constants.CONTENT_PATH_FORMAT, path + ".html"); }
}
