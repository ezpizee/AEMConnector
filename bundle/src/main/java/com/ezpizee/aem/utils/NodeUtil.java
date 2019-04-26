package com.ezpizee.aem.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Map;

import static com.ezpizee.aem.Constants.PROP_NT_UNSTRUCTURE;

public class NodeUtil {

    private static final Logger LOG = LoggerFactory.getLogger(NodeUtil.class);

    private NodeUtil() {}

    public static void save(ResourceResolver resolver, String path, Map<String, String> props) {
        if (resolver != null && StringUtils.isNotEmpty(path) && props != null && !props.isEmpty()) {
            try {
                Session session = resolver.adaptTo(Session.class);
                if (session != null) {
                    Node node = addIfNotAlreadyExist(resolver, path);
                    LOG.error("SOTHEA session: 1");
                    if (node != null) {
                        LOG.error("SOTHEA node: 1");
                        for (String prop : props.keySet()) {
                            node.setProperty(prop, props.get(prop));
                        }
                        session.save();
                    }
                }
            }
            catch (RepositoryException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static Node addIfNotAlreadyExist(ResourceResolver resolver, String path, String primaryType, Map<String, String> props) {
        if (!exists(resolver, path)) {
            final Node node = addIfNotAlreadyExist(resolver, path, primaryType);
            final Session session = resolver.adaptTo(Session.class);
            if (node != null && !props.isEmpty() && session != null) {
                try {
                    for (String key : props.keySet()) {
                        node.setProperty(key, props.get(key));
                    }
                    session.save();
                }
                catch (RepositoryException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            return node;
        }
        return getNode(resolver, path);
    }

    public static Node addIfNotAlreadyExist(ResourceResolver resolver, String path, String primaryType) {
        if (exists(resolver, path)) {
            return getNode(resolver, path);
        }
        else {
            if (path.endsWith("/")) { path = path.substring(0, path.length()-1); }
            LOG.error("SOTHEA path: "+path);
            String[] parts = path.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length-1; i++) { sb.append(parts[i]).append("/"); }
            String parent = sb.toString();
            if (parent.endsWith("/")) { parent = parent.substring(0, parent.length()-1); }
            LOG.error("SOTHEA parent: "+parent);
            if (resolver.getResource(parent) != null) {
                Session session = resolver.adaptTo(Session.class);
                if (session != null) {
                    try {
                        LOG.error("SOTHEA child: "+parts[parts.length-1]);
                        Node parentNode = session.getNode(parent);
                        Node child = parentNode.addNode(parts[parts.length-1], primaryType);
                        session.save();
                        return child;
                    }
                    catch (RepositoryException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }
        return null;
    }

    public static Node addIfNotAlreadyExist(ResourceResolver resolver, String path) {
        return addIfNotAlreadyExist(resolver, path, PROP_NT_UNSTRUCTURE);
    }

    private static boolean exists(ResourceResolver resolver, String path) {
        return resolver.getResource(path) != null;
    }

    private static Node getNode(ResourceResolver resolver, String path) {
        return resolver.getResource(path).adaptTo(Node.class);
    }
}
