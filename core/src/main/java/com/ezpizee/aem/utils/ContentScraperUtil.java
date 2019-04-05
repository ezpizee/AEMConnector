package com.ezpizee.aem.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ContentScraperUtil {

    protected static final Logger log = LoggerFactory.getLogger(ContentScraperUtil.class);

    private ContentScraperUtil() {}

    public static String getContent(final SlingHttpServletRequest request, final SlingHttpServletResponse response, String path) {

        String pageContent = "";

        try {
            if (path != null) {
                if (!path.endsWith(".html")) {
                    path += ".html";
                }
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
                CharResponseWrapper wrappedResponse = new CharResponseWrapper(response);
                requestDispatcher.include(request, wrappedResponse);
                pageContent = wrappedResponse.toString();
            }
        }
        catch (ServletException | IOException e) {
            log.error(e.getMessage(), e);
        }

        return pageContent;
    }

    private static final class CharResponseWrapper extends HttpServletResponseWrapper {

        private CharArrayWriter output;

        public CharResponseWrapper(final HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        @Override
        public String toString() {
            return output.toString();
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }
    }
}
