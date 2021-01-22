package com.ezpizee.aem.servlets;

import com.ezpizee.aem.models.Cards;
import com.ezpizee.aem.models.UserModel;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.io.IOException;

@SlingServlet(
        paths = {"/bin/takeda/mindful"},
        methods = {HttpConstants.METHOD_POST},
        extensions = {"json"}
)
public class MindfulServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private JsonObject data;
    private RequestParameterMap requestParameterMap;
    private String tipsRoot, disclaimerPath;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {process(request, response);}

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {process(request, response);}

    private void process(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        UserModel userModel = new UserModel();
        userModel.process();
        data = new JsonObject();
        data.add("isRegistered", new JsonPrimitive(userModel.isRegistered()));
        data.add("userId", new JsonPrimitive(userModel.getUserId()));
        requestParameterMap =  request.getRequestParameterMap();
        String scope = requestParameterMap.containsKey("scope") ? requestParameterMap.getValue("scope").getString() : "";
        tipsRoot = requestParameterMap.containsKey("fragmentRootPath") ? requestParameterMap.getValue("fragmentRootPath").getString() : "";
        disclaimerPath = requestParameterMap.containsKey("disclaimerPath") ? requestParameterMap.getValue("disclaimerPath").getString() : "";

        if (StringUtils.isNotEmpty(scope))
        {
            response.setStatus(200);
            switch (scope)
            {
                case "associateMCIDAndCDMID":
                    this.associateMCIDAndCDMIDAPICall(request);
                    break;

                case "retrieveEngagementByCDMID":
                    this.retrieveEngagementByCDMIDAPICall(request);
                    break;

                case "retrieveEngagementByMCID":
                    this.retrieveEngagementByMCIDAPICall(request);
                    break;

                case "storeEngagementWithMCIDAndCDMID":
                    this.storeEngagementWithMCIDAndCDMIDAPICall(request);
                    break;

                case "storeEngagementWithMCID":
                    this.storeEngagementWithMCIDAPICall(request);
                    break;

                case "genCDMID":
                    this.genCDMIDAPICall(request);
                    break;

                case "getCDMID":
                    this.getCDMIDAPICall(request);
                    break;

                case "getMCID":
                    this.genMCIDAPICall(request);
                    break;

                case "getMCDI":
                    this.getMCIDAPICall(request);
                    break;

                case "registerUser":
                    this.registerUserAPICall(request);
                    break;

                case "like":
                    this.likeAPICall(request);
                    break;

                case "dismiss":
                    this.dismissAPICall(request);
                    break;

                case "addReminder":
                    this.addReminderAPICall(request);
                    break;

                case "cardTips":
                    this.cardTips(request);
                    break;

            }
        }
        else
        {
            data.add("error", new JsonPrimitive("Scope is missing"));
            response.setStatus(422);
        }

        response.getWriter().write(data.toString());
    }

    private void associateMCIDAndCDMIDAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("mcid") && requestParameterMap.containsKey("cdmid")) {
            String mcid = requestParameterMap.getValue("mcid").getString();
            this.data.add("mcid", new JsonPrimitive(mcid));
            String cdmid = requestParameterMap.getValue("cdmid").getString();
            this.data.add("cdmid", new JsonPrimitive(cdmid));
        }
    }

    private void retrieveEngagementByCDMIDAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("mcid") && requestParameterMap.containsKey("cdmid")) {
            String cdmid = requestParameterMap.getValue("cdmid").getString();
            this.data.add("cdmid", new JsonPrimitive(cdmid));
        }
    }

    private void retrieveEngagementByMCIDAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("mcid") && requestParameterMap.containsKey("cdmid")) {
            String mcid = requestParameterMap.getValue("mcid").getString();
            this.data.add("mcid", new JsonPrimitive(mcid));
        }
    }

    private void storeEngagementWithMCIDAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("engagement")) {
            String engagement = requestParameterMap.getValue("engagement").getString();
            this.data.add("'engagement'", new JsonPrimitive(engagement));
        }
    }

    private void storeEngagementWithMCIDAndCDMIDAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("engagement")) {
            String engagement = requestParameterMap.getValue("engagement").getString();
            this.data.add("'engagement'", new JsonPrimitive(engagement));
        }
    }

    private void getCDMIDAPICall(SlingHttpServletRequest request)
    {
        // ID: CDM ID
        // 1. set cookie in JavaScript
        // cdmid is assumed (because in real implementation, this is an API call)
        String id = "cdmid-123456789";
        this.data.add("cdmid", new JsonPrimitive(id));
    }

    private void genCDMIDAPICall(SlingHttpServletRequest request)
    {
        // ID: CDM ID
        String id = "cdmid-123456789";
        this.data.add("cdmid", new JsonPrimitive(id));
    }

    private void getMCIDAPICall(SlingHttpServletRequest request)
    {
        // ID: MC ID
        // 1. set cookie in JavaScript
        // mcid is assumed (because in real implementation, this is an API call)
        String id = "mcid-987654321";
        this.data.add("mcid", new JsonPrimitive(id));
    }

    private void genMCIDAPICall(SlingHttpServletRequest request)
    {
        // ID: CM ID
        // 1. set cookie here or in JavaScript
        // mcid is assumed (because in real implementation, this is an API call)
        String id = "mcid-987654321";
        this.data.add("mcid", new JsonPrimitive(id));
    }

    private void registerUserAPICall(SlingHttpServletRequest request)
    {
        String id = "usermindfull-192837465";
        this.data.add("user", new JsonPrimitive(id));
    }

    private void likeAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("uuid")) {
            String uuid = requestParameterMap.getValue("uuid").getString();
            this.data.add("uuid", new JsonPrimitive(uuid));
        }
    }

    private void dismissAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("uuid")) {
            String uuid = requestParameterMap.getValue("uuid").getString();
            this.data.add("uuid", new JsonPrimitive(uuid));
        }
    }

    private void addReminderAPICall(SlingHttpServletRequest request)
    {
        if (requestParameterMap.containsKey("uuid")) {
            String uuid = requestParameterMap.getValue("uuid").getString();
            this.data.add("uuid", new JsonPrimitive(uuid));
        }
    }

    private void cardTips(SlingHttpServletRequest request)
    {
        Cards cards = new Cards();
        cards.loadData(request.getResourceResolver(), tipsRoot, disclaimerPath);
        this.data.add("cards", cards.getCards());
    }
}