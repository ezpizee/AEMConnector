package com.ezpizee.aem.services;

import aQute.bnd.annotation.ProviderType;
import org.apache.sling.api.resource.ResourceResolver;

@ProviderType
public interface AdminService {
    ResourceResolver getResourceResolver(String subService);
}
