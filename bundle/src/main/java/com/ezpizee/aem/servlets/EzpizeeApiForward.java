package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
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

import java.io.*;

@SlingServlet(
    paths = {"/bin/ezpizee/api"},
    methods = {HttpConstants.METHOD_GET, HttpConstants.METHOD_POST, HttpConstants.METHOD_DELETE, HttpConstants.METHOD_PUT},
    extensions = {"json"}
)
public class EzpizeeApiForward extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EzpizeeApiForward.class);
    private static final long serialVersionUID = 1L;
    private Response responseObject;

    @Reference
    private AppConfig appConfig;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_POST);
        response.getWriter().write(responseObject.toString());
    }

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_GET);
        response.getWriter().write(responseObject.toString());
    }

    @Override
    protected final void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_PUT);
        response.getWriter().write(responseObject.toString());
    }

    @Override
    protected final void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_DELETE);
        response.getWriter().write(responseObject.toString());
    }

    private void process(SlingHttpServletRequest request, SlingHttpServletResponse response, final String method) throws IOException {

        response.setContentType(Constants.HEADER_VALUE_JSON);

        responseObject = new Response();
        appConfig.load(request.getSession());

        final String endpoint = request.getParameterMap().containsKey(Constants.KEY_ENDPOINT)
            ? HostName.getAPIServer(appConfig.getEnv()) + request.getParameter(Constants.KEY_ENDPOINT)
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
                                LOG.error(e.getMessage(), e);
                            }
                        }
                    }
                    else {
                        final String payload = FileUtil.bufferedReaderToString(request.getReader());
                        if (DataUtil.isJsonObjectString(payload)) {
                            client.setBody(payload);
                        }
                    }
                    responseObject = client.post(endpoint);
                    break;

                case HttpConstants.METHOD_GET:
                    responseObject = client.get(endpoint);
                    break;

                case HttpConstants.METHOD_PUT:
                    responseObject = client.put(endpoint);
                    break;

                case HttpConstants.METHOD_DELETE:
                    responseObject = client.delete(endpoint);
                    break;
                    
                default:
                    responseObject.setCode(500);
                    responseObject.setStatus("ERROR");
                    responseObject.setMessage("invalid_request");
                    LOG.debug(request.getRequestParameterMap().toString());
            }

            response.setStatus(responseObject.getCode());
        }
    }
}