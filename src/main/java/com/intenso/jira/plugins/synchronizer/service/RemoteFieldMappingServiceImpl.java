// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.atlassian.jira.component.ComponentAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.ConnectionServerSyncData;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.RestResult;
import com.intenso.jira.plugins.synchronizer.config.IssueSyncCloudUtil;
import org.codehaus.jackson.map.ObjectMapper;
import com.intenso.jira.plugins.synchronizer.service.comm.Response;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteConfigurationWrapperT;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
import org.apache.http.entity.AbstractHttpEntity;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorServiceImpl;
import org.apache.http.entity.ByteArrayEntity;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.utils.SettingUtils;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorService;
import com.intenso.jira.plugins.synchronizer.entity.RemoteFieldMapping;

public class RemoteFieldMappingServiceImpl extends GenericServiceImpl<RemoteFieldMapping> implements RemoteFieldMappingService
{
    public static final String COL_CONNECTION = "CONNECTION";
    public static final String COL_CONTRACT_NAME = "CONTRACT_NAME";
    public static final String COL_FIELD_NAME = "FIELD_NAME";
    public static final String COL_FIELD_TYPE = "FIELD_TYPE";
    public static final String COL_UPDATE_DATE = "UPDATE_DATE";
    private CommunicatorService communicatorService;
    
    public RemoteFieldMappingServiceImpl(final ActiveObjects dao) {
        super(dao, RemoteFieldMapping.class);
    }
    
    @Override
    public RemoteFieldMapping create(final String fieldName, final Integer fieldType, final String contractName, final Integer connection) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONNECTION", connection);
        params.put("CONTRACT_NAME", contractName);
        params.put("FIELD_NAME", fieldName);
        params.put("FIELD_TYPE", fieldType);
        params.put("UPDATE_DATE", new Timestamp(new Date().getTime()));
        final RemoteFieldMapping rc = (RemoteFieldMapping)this.getDao().create((Class)RemoteFieldMapping.class, (Map)params);
        return rc;
    }
    
    @Override
    public Boolean sendConfiguration(final Connection connection) {
        Boolean result = Boolean.FALSE;
        SettingUtils.clearOldConfiguration();
        if (connection.getPassive() != null && !connection.getPassive().equals(1)) {
            this.getLogger().info(ExtendedLoggerMessageType.CFG, "Connection is in passive mode and will not be able to communicate with remote instance!");
        }
        else if (connection.getRemoteJiraType() == null || connection.getRemoteJiraType().equals(RemoteJiraType.SERVER.ordinal()) || connection.getRemoteJiraType().equals(RemoteJiraType.UNKNOWN.ordinal())) {
            result = this.sendConfigurationServer(connection);
        }
        else if (connection.getRemoteJiraType() != null && connection.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal())) {
            result = this.sendConfigurationCloud(connection);
        }
        return result;
    }
    
    private Boolean sendConfigurationServer(final Connection connection) {
        SettingUtils.clearOldConfiguration();
        final RemoteConfigurationWrapperT localConfiguration = SettingUtils.getConfigurationForConnection(connection);
        final byte[] localConfigurationBytes = SettingUtils.writeRemoteConfigurationWrapper(localConfiguration);
        if (localConfigurationBytes == null) {
            this.getLogger().error(ExtendedLoggerMessageType.CFG, "Local configuration cannot be write to byte[]");
            return Boolean.FALSE;
        }
        final ByteArrayEntity entity = new ByteArrayEntity(localConfigurationBytes);
        final Response response = this.getCommunicatorService().callExternalRest(connection.getRemoteJiraURL() + "/rest/synchronizer/1.0/in/queueout/configuration", connection.getProxy(), connection.getRemoteAuthKey(), CommunicatorServiceImpl.HttpRequestMethod.POST, entity);
        if (response == null || response.getStatus() >= 400) {
            this.getLogger().error(ExtendedLoggerMessageType.CFG, "Unable to post configuration " + ((response != null) ? response.getStatus() : "response = null"));
            return Boolean.FALSE;
        }
        try (final ByteArrayInputStream bos = (response.getBytes() != null) ? new ByteArrayInputStream(response.getBytes()) : null) {
            final RemoteConfigurationWrapperT remoteWrapper = SettingUtils.parseRemoteConfigurationWrapper((response.getJson() != null) ? IOUtils.toInputStream(response.getJson()) : bos);
            if (remoteWrapper == null) {
                this.getLogger().error(ExtendedLoggerMessageType.CFG, "Unable to read remote configuration!");
                return Boolean.FALSE;
            }
            remoteWrapper.setConnection(connection.getID());
            SettingUtils.updateConfiguration(remoteWrapper);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.TRUE;
    }
    
    private Boolean sendConfigurationCloud(final Connection connection) {
        final ConnectionServerSyncData localConfiguration = SettingUtils.getConfigurationForConnectionCloud(connection);
        String localConfigurationJson = "";
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            localConfigurationJson = objectMapper.writeValueAsString((Object)localConfiguration);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final String url = IssueSyncCloudUtil.getCloudServerUrl("/server/rest/api/1/connection/synchronize");
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/json");
        headers.put("IssueSyncToken", IssueSyncCloudUtil.getSecretToken());
        final Response response = this.getCommunicatorService().callExternalRest(url, connection.getProxy(), connection.getRemoteAuthKey(), CommunicatorServiceImpl.HttpRequestMethod.POST, headers, localConfigurationJson);
        if (response == null || response.getStatus() >= 400) {
            this.getLogger().error(ExtendedLoggerMessageType.CFG, "Unable to post configuration " + ((response != null) ? response.getStatus() : "response = null"));
            return Boolean.FALSE;
        }
        final String responseJson = response.getJson();
        RestResult restResult = null;
        if (responseJson != null) {
            try {
                restResult = (RestResult)objectMapper.readValue(responseJson, (Class)RestResult.class);
            }
            catch (Exception e2) {
                e2.printStackTrace();
                return Boolean.FALSE;
            }
        }
        if (restResult != null) {
            String remoteConfigurationJson = "";
            if (restResult.getDataObject() != null) {
                try {
                    remoteConfigurationJson = objectMapper.writeValueAsString(restResult.getDataObject());
                }
                catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            ConnectionServerSyncData remoteConfiguration = null;
            try {
                remoteConfiguration = (ConnectionServerSyncData)objectMapper.readValue(remoteConfigurationJson, (Class)ConnectionServerSyncData.class);
            }
            catch (Exception e4) {
                e4.printStackTrace();
            }
            if (remoteConfiguration != null) {
                SettingUtils.updateConfiguration(remoteConfiguration, connection.getID());
            }
        }
        return Boolean.TRUE;
    }
    
    @Override
    public List<RemoteFieldMapping> findByContractAndConnection(final String contractName, final Integer connectionId) {
        final RemoteFieldMapping[] mappings = (RemoteFieldMapping[])this.getDao().find((Class)RemoteFieldMapping.class, Query.select().where("CONNECTION = ? AND CONTRACT_NAME = ? ", new Object[] { connectionId, contractName }));
        return (mappings != null) ? Arrays.asList(mappings) : new ArrayList<RemoteFieldMapping>();
    }
    
    @Override
    public RemoteFieldMapping findByContractAndConnectionAndName(final String contractName, final Integer connectionId, final String remoteFieldName) {
        final RemoteFieldMapping[] mappings = (RemoteFieldMapping[])this.getDao().find((Class)RemoteFieldMapping.class, Query.select().where("CONNECTION = ? AND CONTRACT_NAME = '" + contractName + "' AND " + "FIELD_NAME" + " = '" + remoteFieldName + "'", new Object[] { connectionId }));
        return (mappings != null && mappings.length > 0) ? mappings[0] : null;
    }
    
    public CommunicatorService getCommunicatorService() {
        if (this.communicatorService == null) {
            this.communicatorService = (CommunicatorService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)CommunicatorService.class);
        }
        return this.communicatorService;
    }
}
