package com.ezpizee.aem.utils;

import org.apache.sling.api.resource.Resource;

public interface ResourceValidatorInterface {
    boolean valid(Resource resource);
}
