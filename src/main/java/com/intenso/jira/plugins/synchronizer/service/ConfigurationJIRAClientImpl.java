// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldMappingRestrictedT;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.comm.ResponseErrorsAware;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.RestResult;
import java.util.HashMap;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.config.IssueSyncCloudUtil;
import com.intenso.jira.plugins.synchronizer.service.comm.RemoteAuthenticationKeyResult;
import org.codehaus.jackson.map.ObjectMapper;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.ServerInfo;
import com.intenso.jira.plugins.synchronizer.service.comm.Response;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorServiceImpl;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorService;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.atlassian.jira.issue.CustomFieldManager;

public class ConfigurationJIRAClientImpl implements ConfigurationJIRAClient
{
    private static String USER_PROFILE_REST_API;
    private static final String SERVER_TITLE_FOR_401_STATUS = "Content type in response is not equal to application/json;charset=UTF-8";
    private CustomFieldManager customFieldManager;
    private ConnectionService connectionService;
    private ExtendedLogger logger;
    private CommunicatorService communicatorService;
    
    public ConfigurationJIRAClientImpl(final ConnectionService connectionService, final CustomFieldManager customFieldManager, final CommunicatorService communicatorService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.setConnectionService(connectionService);
        this.setCustomFieldManager(customFieldManager);
        this.communicatorService = communicatorService;
    }
    
    @Override
    public boolean testLocalConnection(final String localUrl, final String username, final String password) {
        boolean test = false;
        final Response response = this.communicatorService.callInternalRest(username, password, CommunicatorServiceImpl.HttpRequestMethod.GET, localUrl + ConfigurationJIRAClientImpl.USER_PROFILE_REST_API + "?username=" + username);
        if (response != null) {
            final int status = response.getStatus();
            if (status == 200) {
                test = true;
                this.logger.debug(ExtendedLoggerMessageType.CFG, "Local connection test ok");
            }
            else {
                this.logger.error(ExtendedLoggerMessageType.CFG, "Error trying to establish local connection. Response status is: " + status);
            }
        }
        else {
            this.logger.error(ExtendedLoggerMessageType.CFG, "Error trying to establish local connection. Probably bad server URL. ");
        }
        return test;
    }
    
    @Override
    public RemoteResponse testRemoteConnection(final String remoteJiraUrl, final String proxy, final String remoteAuthKey) {
        int result = RemoteJiraType.UNKNOWN.ordinal();
        boolean unauthorizedStatus = false;
        final StringBuilder builder = new StringBuilder();
        builder.append(remoteJiraUrl);
        builder.append("/rest/api/2/serverInfo");
        final String testAddress = builder.toString();
        final Response response = this.communicatorService.callExternalRest(testAddress, proxy, CommunicatorServiceImpl.HttpRequestMethod.GET, remoteAuthKey);
        if (response != null) {
            final String jsonString = response.getJson();
            ServerInfo serverInfoT = null;
            if (!response.getContentType().getValue().equals("application/json;charset=UTF-8") && response.getStatus().equals(401)) {
                serverInfoT = new ServerInfo(remoteJiraUrl, "Content type in response is not equal to application/json;charset=UTF-8");
                unauthorizedStatus = true;
            }
            else if (jsonString != null) {
                try {
                    final ObjectMapper objectMapper = new ObjectMapper();
                    serverInfoT = (ServerInfo)objectMapper.readValue(jsonString, (Class)ServerInfo.class);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (serverInfoT != null) {
                RemoteJiraType remoteJiraType = RemoteJiraType.getByName(serverInfoT.getDeploymentType());
                if (remoteJiraType == null) {
                    remoteJiraType = RemoteJiraType.SERVER;
                }
                result = remoteJiraType.ordinal();
            }
        }
        return new RemoteResponse(result, unauthorizedStatus);
    }
    
    @Override
    public boolean testRemoteAuthenticationKey(final String remoteJiraUrl, final String proxy, final String remoteAuthKey) {
        boolean result = false;
        final StringBuilder builder = new StringBuilder();
        builder.append(remoteJiraUrl);
        builder.append("/rest/synchronizer/1.0/connection/test/");
        builder.append(remoteAuthKey);
        final String testAddress = builder.toString();
        this.logger.debug(ExtendedLoggerMessageType.CFG, "Test remote authentication key " + testAddress);
        final Response response = this.communicatorService.callExternalRest(testAddress, proxy, CommunicatorServiceImpl.HttpRequestMethod.GET, remoteAuthKey);
        if (response != null) {
            final int status = response.getStatus();
            if (status == 200) {
                result = true;
                this.logger.debug(ExtendedLoggerMessageType.CFG, "Remote authentication key ok");
            }
            else if (status == 401) {
                result = true;
                this.logger.debug(ExtendedLoggerMessageType.CFG, "Remote authentication key ok. Status code: 401.");
            }
            else {
                this.logger.debug(ExtendedLoggerMessageType.CFG, "Remote authentication key is wrong.");
            }
        }
        return result;
    }
    
    @Override
    public RemoteAuthenticationKeyResult testRemoteAuthenticationKeyCloud(final String remoteJiraUrl, final String proxy, final String remoteAuthKey) {
        final StringBuilder builder = new StringBuilder();
        builder.append(IssueSyncCloudUtil.getCloudServerUrl());
        builder.append("/server/rest/api/1/connection/test");
        builder.append("?sourceUrl=");
        builder.append(ComponentAccessor.getApplicationProperties().getString("jira.baseurl"));
        builder.append("&remoteUrl=");
        builder.append(remoteJiraUrl);
        builder.append("&remoteKey=");
        builder.append(remoteAuthKey);
        final String testAddress = builder.toString();
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("IssueSyncToken", IssueSyncCloudUtil.getSecretToken());
        final ResponseErrorsAware response = this.communicatorService.callExternalRest(testAddress, proxy, remoteAuthKey, CommunicatorServiceImpl.HttpRequestMethod.GET, headers);
        if (response.getResponse() != null) {
            final String jsonString = response.getResponse().getJson();
            RestResult restResultT = null;
            if (jsonString != null) {
                try {
                    final ObjectMapper objectMapper = new ObjectMapper();
                    restResultT = (RestResult)objectMapper.readValue(jsonString, (Class)RestResult.class);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.logger.debug(ExtendedLoggerMessageType.CFG, "Test remote authentication key with " + testAddress + " failed: " + e.getMessage());
                    return RemoteAuthenticationKeyResult.connectionError(e);
                }
            }
            if (restResultT != null) {
                try {
                    final Boolean keyValid = (Boolean)restResultT.getDataObject();
                    if (keyValid) {
                        this.logger.debug(ExtendedLoggerMessageType.CFG, "Test remote authentication key with " + testAddress + " succeed");
                        return RemoteAuthenticationKeyResult.validKey();
                    }
                    this.logger.debug(ExtendedLoggerMessageType.CFG, "Test remote authentication key with " + testAddress + " failed, invalid key");
                    return RemoteAuthenticationKeyResult.invalidKey();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.logger.debug(ExtendedLoggerMessageType.CFG, "Test remote authentication key with " + testAddress + " failed: " + e.getMessage());
                    return RemoteAuthenticationKeyResult.connectionError(e);
                }
            }
            this.logger.debug(ExtendedLoggerMessageType.CFG, "Test remote authentication key with " + testAddress + " failed, empty result");
            return RemoteAuthenticationKeyResult.connectionError("result is empty");
        }
        this.logger.debug(ExtendedLoggerMessageType.CFG, "Test remote authentication key with " + testAddress + " failed: " + response.getException().getMessage());
        return RemoteAuthenticationKeyResult.connectionError(response);
    }
    
    @Override
    public List<FieldMappingRestrictedT> getFieldMappingFrom(final Integer connectionId) {
        final Connection connection = this.connectionService.get(connectionId);
        final StringBuilder sb = new StringBuilder();
        sb.append(connection.getRemoteJiraURL());
        sb.append("/rest/synchronizer/1.0/in/");
        sb.append("fieldMapping");
        final Response response = this.communicatorService.callExternalRest(sb.toString(), connection.getProxy(), CommunicatorServiceImpl.HttpRequestMethod.GET, connection.getRemoteAuthKey());
        if (response == null) {
            return new ArrayList<FieldMappingRestrictedT>();
        }
        final String responseString = response.getJson();
        if (response.getStatus() >= 400) {
            return null;
        }
        final List<FieldMappingRestrictedT> restrictedFields = this.parseResponse(responseString);
        return restrictedFields;
    }
    
    @Override
    public List<String> getContractsFrom(final Integer connectionId) {
        final Connection connection = this.connectionService.get(connectionId);
        final StringBuilder sb = new StringBuilder();
        sb.append(connection.getRemoteJiraURL());
        sb.append("/rest/synchronizer/1.0/in/");
        sb.append("contracts");
        final Response response = this.communicatorService.callExternalRest(sb.toString(), connection.getProxy(), CommunicatorServiceImpl.HttpRequestMethod.GET, connection.getRemoteAuthKey());
        if (response == null) {
            return new ArrayList<String>();
        }
        final String responseString = response.getJson();
        if (response.getStatus() >= 400) {
            return null;
        }
        final List<String> restrictedFields = this.parseStringListResponse(responseString);
        return restrictedFields;
    }
    
    private List<FieldMappingRestrictedT> parseResponse(final String json) {
        if (json == null) {
            return new ArrayList<FieldMappingRestrictedT>();
        }
        final Gson gson = new Gson();
        final Type listType = new TypeToken<List<FieldMappingRestrictedT>>() {}.getType();
        final List<FieldMappingRestrictedT> restrictedFields = gson.fromJson(json, listType);
        return restrictedFields;
    }
    
    private List<String> parseStringListResponse(final String json) {
        if (json == null) {
            return new ArrayList<String>();
        }
        final Gson gson = new Gson();
        final Type listType = new TypeToken<List<String>>() {}.getType();
        final List<String> restrictedFields = gson.fromJson(json, listType);
        return restrictedFields;
    }
    
    public ConnectionService getConnectionService() {
        return this.connectionService;
    }
    
    public void setConnectionService(final ConnectionService connectionService) {
        this.connectionService = connectionService;
    }
    
    public CustomFieldManager getCustomFieldManager() {
        return this.customFieldManager;
    }
    
    public void setCustomFieldManager(final CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
    
    static {
        ConfigurationJIRAClientImpl.USER_PROFILE_REST_API = "/rest/api/2/user";
    }
}
