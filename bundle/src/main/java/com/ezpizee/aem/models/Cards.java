package com.ezpizee.aem.models;


import com.adobe.cq.sightly.WCMUsePojo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class Cards extends WCMUsePojo {

    private static final String PATH_SFX = "jcr:content/data/master";
    private Logger logger = LoggerFactory.getLogger(getClass());
    private JsonArray cards;

    @Override
    public void activate() throws Exception {
        String tipsRoot = getResource().getValueMap().get("fragmentRootPath", "");
        String disclaimerPath = getResource().getValueMap().get("disclaimerPath", "");
        loadData(getResourceResolver(), tipsRoot, disclaimerPath);
    }

    public void loadData(ResourceResolver resolver, String tipsRoot, String disclaimerPath) {
        cards = new JsonArray();
        if (StringUtils.isNotEmpty(tipsRoot) && StringUtils.isNotEmpty(disclaimerPath)) {
            Resource disclaimerPathRes = resolver.getResource(disclaimerPath+"/"+PATH_SFX);
            if (disclaimerPathRes != null) {
                logger.error(disclaimerPathRes.getPath());
                String disclaimer = disclaimerPathRes.getValueMap().get("disclaimer", "");
                Resource tipsRootRes = resolver.getResource(tipsRoot);
                if (tipsRootRes != null) {
                    Iterator<Resource> ir = tipsRootRes.listChildren();
                    while (ir.hasNext()) {
                        Resource resource = ir.next();
                        String uuid = resource.getValueMap().get("jcr:uuid", "");
                        resource = resource.getChild(PATH_SFX);
                        if (resource != null) {
                            JsonObject obj = new JsonObject();
                            obj.add("uuid", new JsonPrimitive(uuid));
                            ValueMap valueMap = resource.getValueMap();
                            for (String key : valueMap.keySet()) {
                                Object value = valueMap.get(key);
                                if (value instanceof String) {
                                    obj.add(key, new JsonPrimitive(value.toString()));
                                }
                            }
                            obj.add("disclaimer", new JsonPrimitive(disclaimer));
                            cards.add(obj);
                        }
                    }
                }
            }
        }
    }

    public JsonArray getCards() {return cards;}
}
