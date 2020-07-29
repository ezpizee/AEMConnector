package com.ezpizee.aem.servlets;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.DataUtil;
import com.ezpizee.aem.utils.FileUtil;
import com.ezpizee.aem.utils.HostName;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.*;

import static com.ezpizee.aem.Constants.HEADER_VALUE_JSON;
import static com.ezpizee.aem.Constants.KEY_ACCESS_TOKEN;
import static com.ezpizee.aem.Constants.KEY_ENDPOINT;
import static com.ezpizee.aem.Constants.KEY_EZPZ_LOGIN;

@SlingServlet(
    paths = {"/bin/ezpizee/api"},
    methods = {HttpConstants.METHOD_GET, HttpConstants.METHOD_POST, HttpConstants.METHOD_DELETE, HttpConstants.METHOD_PUT},
    extensions = {"json"}
)
public class EzpizeeApiForward extends SlingAllMethodsServlet {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;
    private Response ezResponse;

    @Reference
    private AppConfig appConfig;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_POST);
        response.getWriter().write(ezResponse.toString());
    }

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_GET);
        response.getWriter().write(ezResponse.toString());
    }

    @Override
    protected final void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_PUT);
        response.getWriter().write(ezResponse.toString());
    }

    @Override
    protected final void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_DELETE);
        response.getWriter().write(ezResponse.toString());
    }

    private void process(SlingHttpServletRequest request, SlingHttpServletResponse response, final String method) throws IOException {

        response.setContentType(HEADER_VALUE_JSON);

        String authCookie = getAuthCookie(request);
        ezResponse = new Response();
        appConfig.load(authCookie, request.getSession());

        final String endpoint = request.getParameterMap().containsKey(KEY_ENDPOINT)
            ? HostName.getAPIServer(appConfig.getEnv()) + request.getParameter(KEY_ENDPOINT)
            : StringUtils.EMPTY;

        if (StringUtils.isNotEmpty(endpoint))
        {
            final Client client = new Client(appConfig);
            client.setBearerToken(appConfig.getBearerToken());

            switch (method)
            {
                case HttpConstants.METHOD_POST:
                    String cType = request.getHeader("Content-Type");
                    if (cType != null && cType.toLowerCase().startsWith("multipart/form-data")) {
                        client.setIsMultipart(true);
                        client.setFormParams(DataUtil.toMapObject(request.getRequestParameterMap()));
                        if (request.getRequestParameterMap().containsKey("file")) {
                            try {
                                RequestParameter param = request.getRequestParameterMap().getValue("file");
                                if (param != null) {
                                    final InputStream inputStream = param.getInputStream();
                                    client.addInputStream("file", inputStream, param.getFileName());
                                }
                            }
                            catch (IOException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                    else {
                        final String payload = FileUtil.bufferedReaderToString(request.getReader());
                        if (DataUtil.isJsonObjectString(payload)) {
                            client.setBody(payload);
                        }
                    }
                    ezResponse = client.post(endpoint);
                    break;

                case HttpConstants.METHOD_GET:
                    ezResponse = client.get(endpoint);
                    break;

                case HttpConstants.METHOD_PUT:
                    ezResponse = client.put(endpoint);
                    break;

                case HttpConstants.METHOD_DELETE:
                    ezResponse = client.delete(endpoint);
                    break;

                default:
                    ezResponse.setCode(500);
                    ezResponse.setStatus("ERROR");
                    ezResponse.setMessage("invalid_request");
                    logger.debug(request.getRequestParameterMap().toString());
            }

            if (ezResponse.getCode() != 200 && "INVALID_ACCESS_TOKEN".equals(ezResponse.getMessage())) {
                appConfig.clearAccessTokenSession(authCookie, request.getSession());
            }

            response.setStatus(ezResponse.getCode());
        }
    }

    private String getAuthCookie(SlingHttpServletRequest request) {
        Cookie cookie = request.getCookie(KEY_EZPZ_LOGIN);
        if (cookie != null) {
            return cookie.getValue();
        }
        return KEY_ACCESS_TOKEN;
    }
}