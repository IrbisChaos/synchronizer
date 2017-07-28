// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.RemoteWorkflowMappingService;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.RemoteContractService;
import com.intenso.jira.plugins.synchronizer.service.RemoteFieldMappingService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import org.codehaus.jackson.JsonParseException;
import java.io.InputStream;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.smile.SmileParser;
import org.codehaus.jackson.smile.SmileFactory;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonGenerationException;
import com.atlassian.jira.issue.fields.Field;
import java.util.HashSet;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.ExposedFieldServerSyncData;
import com.intenso.jira.plugins.synchronizer.entity.RemoteWorkflowMapping;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowIncomingTransitionsT;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowMappingT;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.WorkflowTransitionMappingServerSyncData;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.WorkflowMappingServerSyncData;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.ContractServerSyncData;
import com.intenso.jira.plugins.synchronizer.entity.RemoteContract;
import com.intenso.jira.plugins.synchronizer.entity.RemoteFieldMapping;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.ConnectionServerSyncData;
import org.codehaus.jackson.map.ObjectMapper;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowConfigurationT;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import java.util.Iterator;
import java.util.List;
import java.sql.Timestamp;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteContractT;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteFieldMappingT;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteConfigurationWrapperT;
import com.intenso.jira.plugins.synchronizer.entity.Connection;

public class SettingUtils
{
    private static ExtendedLogger logger;
    public static final Integer PARTIAL_ERROR_STATUS;
    public static final Integer INVALID_LICENSE_STATUS;
    public static final String PLUGIN_KEY = "com.intenso.jira.plugins.synchronizer";
    
    public static final RemoteConfigurationWrapperT getConfigurationForConnection(final Connection connection) {
        final List<ContractFieldMappingEntry> mappingsForConnection = getContractFieldMappingEntryService().findByConnection(connection.getID());
        final List<RemoteFieldMappingT> configList = new ArrayList<RemoteFieldMappingT>();
        final List<Contract> contracts = getContractService().findByConnection(connection.getID());
        final List<RemoteContractT> localContracts = new ArrayList<RemoteContractT>();
        if (contracts != null) {
            for (final Contract c : contracts) {
                final List<ContractEvents> createEvents = getContractService().getEventsForContract(c.getID(), EventType.CREATE);
                final List<ContractEvents> updateEvents = getContractService().getEventsForContract(c.getID(), EventType.UPDATE);
                final List<ContractEvents> deleteEvents = getContractService().getEventsForContract(c.getID(), EventType.DELETE);
                localContracts.add(new RemoteContractT(c, createEvents, updateEvents, deleteEvents));
            }
        }
        for (final ContractFieldMappingEntry entry : mappingsForConnection) {
            final FieldType ft = getFieldType(entry.getLocalFieldId());
            String contractName = null;
            for (final Contract c2 : contracts) {
                if (c2.getID() == entry.getContractId()) {
                    contractName = c2.getContractName();
                    break;
                }
            }
            if (contractName == null) {
                SettingUtils.logger.error(ExtendedLoggerMessageType.CFG, "Wrong contract in configuration, probably no such contract for field mapping.");
            }
            else {
                configList.add(new RemoteFieldMappingT(entry, ft.ordinal(), contractName));
            }
        }
        final RemoteWorkflowConfigurationT workflows = getRemoteWorkflowConfigurationT();
        final RemoteConfigurationWrapperT cacheConfig = new RemoteConfigurationWrapperT();
        cacheConfig.setConfiguration(configList);
        cacheConfig.setContracts(localContracts);
        cacheConfig.setWorkflows(workflows);
        cacheConfig.setTimestamp(new Timestamp(new Date().getTime()));
        return cacheConfig;
    }
    
    private static RemoteWorkflowConfigurationT getRemoteWorkflowConfigurationT() {
        RemoteWorkflowConfigurationT workflows = null;
        final String json = getWorkflowSyncService().getRemoteConfigurationJSONString();
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            workflows = (RemoteWorkflowConfigurationT)objectMapper.readValue(json, (Class)RemoteWorkflowConfigurationT.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return workflows;
    }
    
    public static ConnectionServerSyncData getConfigurationForConnectionCloud(final Connection connection) {
        final List<Contract> contracts = getContractService().findByConnection(connection.getID());
        final RemoteWorkflowConfigurationT remoteWorkflowConfigurationT = getRemoteWorkflowConfigurationT();
        final ConnectionServerSyncData config = new ConnectionServerSyncData(connection, contracts, remoteWorkflowConfigurationT.getConfiguration());
        return config;
    }
    
    public static void updateConfiguration(final RemoteConfigurationWrapperT remoteConfig) {
        synchronized (RemoteFieldMapping.class) {
            final List<RemoteContract> currentContract = getRemoteContractService().findByConnection(remoteConfig.getConnection());
            final List<RemoteContractT> contracts = remoteConfig.getContracts();
            for (final RemoteContract cc : currentContract) {
                boolean found = false;
                for (final RemoteContractT rc : contracts) {
                    if (rc.getContract().equals(cc.getContract())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    deleteRemoteContract(cc);
                }
            }
            for (final RemoteContractT rc2 : contracts) {
                boolean found = false;
                for (final RemoteContract cc2 : currentContract) {
                    if (cc2.getContract().equals(rc2.getContract())) {
                        found = true;
                        if (cc2.getHash() == null) {
                            deleteRemoteContract(cc2);
                            break;
                        }
                        if (!cc2.getHash().equals(rc2.getHash())) {
                            deleteRemoteContract(cc2);
                            addNewRemoteContract(rc2, remoteConfig.getConfiguration(), remoteConfig.getConnection());
                            break;
                        }
                        break;
                    }
                }
                if (!found) {
                    addNewRemoteContract(rc2, remoteConfig.getConfiguration(), remoteConfig.getConnection());
                }
            }
        }
        deleteRemoteWorkflowMapping(remoteConfig.getConnection());
        if (remoteConfig.getWorkflows() != null) {
            addNewRemoteWorkflowMapping(remoteConfig.getWorkflows().getConfiguration(), remoteConfig.getConnection());
        }
    }
    
    public static void updateConfiguration(final ConnectionServerSyncData remoteConfig, final Integer connectionId) {
        synchronized (RemoteFieldMapping.class) {
            final List<RemoteContract> currentContract = getRemoteContractService().findByConnection(connectionId);
            final List<ContractServerSyncData> contracts = remoteConfig.getContracts();
            for (final RemoteContract cc : currentContract) {
                deleteRemoteContract(cc);
            }
            for (final ContractServerSyncData rc : contracts) {
                addNewRemoteContract(rc, connectionId);
            }
        }
        deleteRemoteWorkflowMapping(connectionId);
        for (final WorkflowMappingServerSyncData workflowMapping : remoteConfig.getWorkflows()) {
            for (final WorkflowTransitionMappingServerSyncData transitionMapping : workflowMapping.getTransitionMappings()) {
                final Integer resolution = (int)(((boolean)transitionMapping.getSetResolution()) ? 1 : 0);
                getRemoteWorkflowMappingService().create(connectionId, (int)(Object)workflowMapping.getId(), workflowMapping.getName(), transitionMapping.getOutgoingCode(), resolution);
            }
        }
    }
    
    private static void addNewRemoteWorkflowMapping(final List<RemoteWorkflowMappingT> configuration, final Integer connection) {
        for (final RemoteWorkflowMappingT rmw : configuration) {
            for (final RemoteWorkflowIncomingTransitionsT rwit : rmw.incomingTransitions) {
                Integer resolution = 0;
                if (rwit.getResolution() != null) {
                    resolution = (((boolean)rwit.getResolution()) ? 1 : 0);
                }
                getRemoteWorkflowMappingService().create(connection, rmw.getWorkflowMappingId(), rmw.getWorkflowMappingDisplayName(), rwit.getInTransitionText(), resolution);
            }
        }
    }
    
    private static void deleteRemoteWorkflowMapping(final Integer connection) {
        final List<RemoteWorkflowMapping> remoteMapping = getRemoteWorkflowMappingService().findByConnection(connection);
        for (final RemoteWorkflowMapping rw : remoteMapping) {
            getRemoteWorkflowMappingService().delete(rw);
        }
    }
    
    private static RemoteContract addNewRemoteContract(final RemoteContractT rc, final List<RemoteFieldMappingT> configuration, final Integer connection) {
        final RemoteContract remoteContract = getRemoteContractService().create(connection, rc.getContract(), rc.getCreate(), rc.getUpdate(), rc.getDelete(), rc.getComments(), rc.getAttachments(), rc.getWorklogs(), rc.getHash());
        for (final RemoteFieldMappingT rft : configuration) {
            if (rft.getContractName().equals(rc.getContract())) {
                getRemoteFieldMappingService().create(rft.getFieldName(), rft.getFieldType(), rc.getContract(), connection);
            }
        }
        return remoteContract;
    }
    
    private static RemoteContract addNewRemoteContract(final ContractServerSyncData rc, final Integer connection) {
        final RemoteContract remoteContract = getRemoteContractService().create(connection, rc.getContractWithOutTriggerName(), rc.isOutTriggerCreateEvent() ? 1 : 0, rc.isOutTriggerUpdateEvent() ? 1 : 0, rc.isOutTriggerDeleteEvent() ? 1 : 0, rc.isOutTriggerSyncComments() ? 1 : 0, rc.isOutTriggerSyncAttachments() ? 1 : 0, rc.isOutTriggerSyncWorklogs() ? 1 : 0, -1);
        final List<ExposedFieldServerSyncData> fieldsMapping = rc.getExposedFields();
        for (final ExposedFieldServerSyncData fieldMapping : fieldsMapping) {
            final Integer type = FieldType.valueOf(fieldMapping.getJiraFieldType()).ordinal();
            getRemoteFieldMappingService().create(fieldMapping.getName(), type, rc.getContractWithOutTriggerName(), connection);
        }
        return remoteContract;
    }
    
    private static void deleteRemoteContract(final RemoteContract cc) {
        final List<RemoteFieldMapping> remoteMapping = getRemoteFieldMappingService().findByContractAndConnection(cc.getContract(), cc.getConnection());
        for (final RemoteFieldMapping rf : remoteMapping) {
            getRemoteFieldMappingService().delete(rf);
        }
        getRemoteContractService().delete(cc);
    }
    
    public static void clearOldConfiguration() {
        final List<Connection> connections = getConnectionService().getAll();
        final List<RemoteContract> remoteContracts = getRemoteContractService().getAll();
        synchronized (RemoteFieldMapping.class) {
            final List<RemoteFieldMapping> remoteFieldMappings = getRemoteFieldMappingService().getAll();
            final HashSet<Integer> connectionsIds = new HashSet<Integer>(1);
            final HashSet<Integer> remoteConnectionsIds = new HashSet<Integer>(1);
            final HashSet<Integer> remoteFieldMappingConnectionIds = new HashSet<Integer>(1);
            if (connections != null && connections.size() > 0) {
                for (final Connection connection : connections) {
                    connectionsIds.add(connection.getID());
                }
            }
            if (remoteContracts != null && remoteContracts.size() > 0) {
                for (final RemoteContract remoteContract : remoteContracts) {
                    remoteConnectionsIds.add(remoteContract.getConnection());
                }
            }
            if (remoteFieldMappings != null && remoteFieldMappings.size() > 0) {
                for (final RemoteFieldMapping remoteFieldMapping : remoteFieldMappings) {
                    remoteFieldMappingConnectionIds.add(remoteFieldMapping.getConnection());
                }
            }
            if (connectionsIds.size() > 0 && remoteConnectionsIds.size() > 0) {
                for (final Integer remoteConnection : remoteConnectionsIds) {
                    if (!connectionsIds.contains(remoteConnection)) {
                        final List<RemoteContract> remoteContractsToDelete = getRemoteContractService().findByConnection(remoteConnection);
                        for (final RemoteContract remoteContractToDelete : remoteContractsToDelete) {
                            getRemoteContractService().delete(remoteContractToDelete);
                        }
                    }
                }
                if (remoteFieldMappingConnectionIds.size() > 0) {
                    for (final Integer remoteFieldMappingConnectionId : remoteFieldMappingConnectionIds) {
                        if (!connectionsIds.contains(remoteFieldMappingConnectionId)) {
                            final List<RemoteFieldMapping> remoteFieldMappingsToDelete = getRemoteFieldMappingService().findByContractAndConnection("%", remoteFieldMappingConnectionId);
                            for (final RemoteFieldMapping remoteFieldMappingToDelete : remoteFieldMappingsToDelete) {
                                getRemoteFieldMappingService().delete(remoteFieldMappingToDelete);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static FieldType getFieldType(final String fieldId) {
        Field f = getFieldManager().getField(fieldId);
        if (f == null) {
            f = (Field)getCustomFieldManager().getCustomFieldObject(fieldId);
        }
        if (f == null) {
            return FieldType.TYPE_LIMITED_TEXT;
        }
        final FieldType type = FieldType.getCustomFieldTypeFromClass(f);
        return type;
    }
    
    public static byte[] writeRemoteConfigurationWrapper(final RemoteConfigurationWrapperT wrapper) {
        final ObjectMapper mapper = getMapper(RemoteConfigurationWrapperT.class);
        try {
            return mapper.writeValueAsBytes((Object)wrapper);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e2) {
            e2.printStackTrace();
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        return null;
    }
    
    private static ObjectMapper getMapper(final Class clazz) {
        final SmileFactory factory = new SmileFactory();
        factory.configure(SmileParser.Feature.REQUIRE_HEADER, false);
        final ObjectMapper mapper = new ObjectMapper((JsonFactory)factory);
        mapper.registerSubtypes(new Class[] { clazz });
        return mapper;
    }
    
    public static RemoteConfigurationWrapperT parseRemoteConfigurationWrapper(final InputStream inputStream) {
        final ObjectMapper mapper = getMapper(RemoteConfigurationWrapperT.class);
        RemoteConfigurationWrapperT remoteConfig = null;
        try {
            remoteConfig = (RemoteConfigurationWrapperT)mapper.readValue(inputStream, (Class)RemoteConfigurationWrapperT.class);
        }
        catch (JsonParseException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e2) {
            e2.printStackTrace();
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        return remoteConfig;
    }
    
    private static ContractFieldMappingEntryService getContractFieldMappingEntryService() {
        return (ContractFieldMappingEntryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractFieldMappingEntryService.class);
    }
    
    private static FieldManager getFieldManager() {
        return ComponentAccessor.getFieldManager();
    }
    
    private static CustomFieldManager getCustomFieldManager() {
        return ComponentAccessor.getCustomFieldManager();
    }
    
    private static RemoteFieldMappingService getRemoteFieldMappingService() {
        return (RemoteFieldMappingService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)RemoteFieldMappingService.class);
    }
    
    private static RemoteContractService getRemoteContractService() {
        return (RemoteContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)RemoteContractService.class);
    }
    
    private static ContractService getContractService() {
        return (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
    }
    
    private static WorkflowSyncService getWorkflowSyncService() {
        return (WorkflowSyncService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)WorkflowSyncService.class);
    }
    
    private static RemoteWorkflowMappingService getRemoteWorkflowMappingService() {
        return (RemoteWorkflowMappingService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)RemoteWorkflowMappingService.class);
    }
    
    private static ConnectionService getConnectionService() {
        return (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
    }
    
    static {
        SettingUtils.logger = ExtendedLoggerFactory.getLogger(SettingUtils.class);
        PARTIAL_ERROR_STATUS = 250;
        INVALID_LICENSE_STATUS = 551;
    }
}
