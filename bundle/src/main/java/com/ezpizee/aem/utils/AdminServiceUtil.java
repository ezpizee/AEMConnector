package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class use for obtaining service system user resource resolver
 */

public class AdminServiceUtil {

    private AdminServiceUtil() {}

    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceUtil.class);

    /**
     * @param resolverFactory ResourceResolverFactory
     * @return the ResourceResolver
     */
    public static ResourceResolver getResourceResolver(ResourceResolverFactory resolverFactory) {
        return getResourceResolver(resolverFactory, Constants.EZPIZEE_SERVICE);
    }

    /**
     * @param resolverFactory ResourceResolverFactory
     * @param session Session
     * @return the ResourceResolver
     */
    public static ResourceResolver getResourceResolver(ResourceResolverFactory resolverFactory, Session session) {
        try {
            return resolverFactory.getResourceResolver(
                Collections.<String, Object>singletonMap(Constants.AUTHENTICATION_INFO_SESSION, session)
            );
        }
        catch (LoginException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param resolverFactory ResourceResolverFactory
     * @param systemUser String
     * @return the ResourceResolver
     */
    public static ResourceResolver getResourceResolver(ResourceResolverFactory resolverFactory, String systemUser) {
        try {
            Map<String, Object> localParams = new ConcurrentHashMap<>();
            localParams.put(ResourceResolverFactory.SUBSERVICE, systemUser);
            return resolverFactory.getServiceResourceResolver(localParams);
        }
        catch (LoginException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static Session getSession(SlingRepository repository) {
        return getSession(repository, Constants.EZPIZEE_SERVICE);
    }

    /**
     * @param repository SlingRepository
     * @param systemUser String
     * @return the Session
     */
    public static Session getSession(SlingRepository repository, String systemUser) {
        try {
            return repository.loginService(systemUser, repository.getDefaultWorkspace());
        }
        catch (RepositoryException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
