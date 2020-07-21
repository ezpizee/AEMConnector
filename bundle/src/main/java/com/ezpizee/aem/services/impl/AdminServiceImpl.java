package com.ezpizee.aem.services.impl;

import com.ezpizee.aem.services.AdminService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Component
public class AdminServiceImpl implements AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public ResourceResolver getResourceResolver(String subService) {
        if (resolverFactory != null) {
            try {
                Map<String, Object> localParams = new ConcurrentHashMap<>();
                localParams.put(ResourceResolverFactory.SUBSERVICE, subService);
                return resolverFactory.getServiceResourceResolver(localParams);
            }
            catch (LoginException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }
}
