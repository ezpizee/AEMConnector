package com.ezpizee.aem.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceFilter.class);
    private final String propertyName;
    private final String propertyValue;
    private final ResourceValidatorInterface validator;

    public ResourceFilter(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.validator = null;
    }

    public ResourceFilter() {
        this.propertyName = null;
        this.propertyValue = null;
        this.validator = null;
    }

    public ResourceFilter(ResourceValidatorInterface validator) {
        this.propertyName = null;
        this.propertyValue = null;
        if (validator != null) {
            this.validator = validator;
        }
        else {
            this.validator = null;
        }
    }

    public boolean execute(final Resource resource) {
        if (resource != null) {
            if (this.validator != null) {
                return this.validator.valid(resource);
            }
            else if (this.propertyName == null && this.propertyValue == null) {
                return true;
            }
            else if (StringUtils.isNotEmpty(this.propertyName) && StringUtils.isNotEmpty(this.propertyValue)) {
                ValueMap valueMap = resource.getValueMap();
                if (valueMap.containsKey(this.propertyName)) {
                    return this.propertyValue.equals(valueMap.get(this.propertyName, ""));
                }
            }
        }
        return false;
    }
}
