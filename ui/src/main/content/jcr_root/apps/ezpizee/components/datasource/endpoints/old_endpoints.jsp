<%--
  #%L
  WEBCONSOL Package
  %%
  Copyright (C) 2018 WEBCONSOL Inc
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  #L%
  --%>
<%@page session="false" import="
    org.apache.sling.api.resource.Resource,
    org.apache.sling.api.resource.ResourceUtil,
    org.apache.sling.api.resource.ValueMap,
    org.apache.sling.api.resource.ResourceResolver,
    org.apache.sling.api.resource.ResourceMetadata,
    org.apache.sling.api.wrappers.ValueMapDecorator,
    com.adobe.granite.ui.components.ds.DataSource,
    com.adobe.granite.ui.components.ds.EmptyDataSource,
    com.adobe.granite.ui.components.ds.SimpleDataSource,
    com.adobe.granite.ui.components.ds.ValueMapResource,
    java.util.List,
    com.ezpizee.aem.Constants,
    com.ezpizee.aem.models.AppConfig,
    com.ezpizee.aem.utils.EndpointUtil,
    com.ezpizee.aem.http.Client,
    com.ezpizee.aem.http.Response,
    net.minidev.json.JSONObject,
    java.util.ArrayList,
    java.util.HashMap"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%

    // set fallback
    request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

    Resource datasource = resource.getChild("datasource");
    ResourceResolver resolver = resource.getResourceResolver();
    ValueMap dsProperties = ResourceUtil.getValueMap(datasource);
    List<Resource> fakeResourceList = new ArrayList<>();

    ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
    vm.put("value", "");
    vm.put("text", "None");
    fakeResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));

    Resource appConfigResource = resolver.getResource(Constants.APP_CONFIG_PATH);
    if (appConfigResource != null) {
        AppConfig appConfig = new AppConfig(appConfigResource);
        if (appConfig.isValid()) {
            String uri = Constants.SERVICE_PROTOCOL_SCHEME + EndpointUtil.getApiHostName(appConfig.getEnv()) + Constants.ENDPOINT_ENDPOINTS;
            Client client = new Client(appConfig);
            Response clientResponse = client.get(uri);
            if (clientResponse.isSuccess()) {
                JSONObject data = clientResponse.getDataAsJSONObject();
                for (String service : data.keySet()) {
                    JSONObject actions = (JSONObject)data.get(service);
                    for (String action : actions.keySet()) {
                        vm = new ValueMapDecorator(new HashMap<String, Object>());
                        String endpoint = (String)(((JSONObject)actions.get(action)).get("uri"));
                        vm.put("value", endpoint);
                        vm.put("text", endpoint);
                        fakeResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), Constants.PROP_NT_UNSTRUCTURE, vm));
                    }
                }
            }
        }
    }

    DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
    request.setAttribute(DataSource.class.getName(), ds);
%>