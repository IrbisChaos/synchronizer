// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowMappingT;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.ArrayList;
import java.util.List;
import org.boon.json.annotations.JsonInclude;

public class ConnectionServerSyncData
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sourceUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remoteUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String localAuthenticationKey;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remoteAuthenticationKey;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean passiveInstance;
    private List<ContractServerSyncData> contracts;
    private List<WorkflowMappingServerSyncData> workflows;
    
    public ConnectionServerSyncData() {
        this.contracts = new ArrayList<ContractServerSyncData>();
        this.workflows = new ArrayList<WorkflowMappingServerSyncData>();
    }
    
    public ConnectionServerSyncData(final Connection connection, final List<Contract> connectionContracts, final List<RemoteWorkflowMappingT> workflowMappings) {
        this.contracts = new ArrayList<ContractServerSyncData>();
        this.workflows = new ArrayList<WorkflowMappingServerSyncData>();
        this.remoteUrl = connection.getRemoteJiraURL();
        this.sourceUrl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
        this.localAuthenticationKey = connection.getLocalAuthKey();
        this.remoteAuthenticationKey = connection.getRemoteAuthKey();
        this.passiveInstance = (connection.getPassive() != null && connection.getPassive() == 1);
        final List<ContractFieldMappingEntry> mappingsForConnection = getContractFieldMappingEntryService().findByConnection(connection.getID());
        for (final Contract connectionContract : connectionContracts) {
            final List<ContractFieldMappingEntry> contractFieldMappings = this.getContractFieldMappingEntries(mappingsForConnection, connectionContract);
            this.contracts.add(new ContractServerSyncData(connectionContract, contractFieldMappings));
        }
        if (workflowMappings != null) {
            for (final RemoteWorkflowMappingT workflowMapping : workflowMappings) {
                this.workflows.add(new WorkflowMappingServerSyncData(workflowMapping));
            }
        }
    }
    
    private List<ContractFieldMappingEntry> getContractFieldMappingEntries(final List<ContractFieldMappingEntry> mappingsForConnection, final Contract connectionContract) {
        final List<ContractFieldMappingEntry> contractFieldMappings = new ArrayList<ContractFieldMappingEntry>();
        for (final ContractFieldMappingEntry mappingEntry : mappingsForConnection) {
            if (mappingEntry.getContractId().equals(connectionContract.getID())) {
                contractFieldMappings.add(mappingEntry);
            }
        }
        return contractFieldMappings;
    }
    
    public String getRemoteUrl() {
        return this.remoteUrl;
    }
    
    public void setRemoteUrl(final String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }
    
    public String getLocalAuthenticationKey() {
        return this.localAuthenticationKey;
    }
    
    public void setLocalAuthenticationKey(final String localAuthenticationKey) {
        this.localAuthenticationKey = localAuthenticationKey;
    }
    
    public String getRemoteAuthenticationKey() {
        return this.remoteAuthenticationKey;
    }
    
    public void setRemoteAuthenticationKey(final String remoteAuthenticationKey) {
        this.remoteAuthenticationKey = remoteAuthenticationKey;
    }
    
    public List<ContractServerSyncData> getContracts() {
        return this.contracts;
    }
    
    public void setContracts(final List<ContractServerSyncData> contracts) {
        this.contracts = contracts;
    }
    
    public Boolean getPassiveInstance() {
        return this.passiveInstance;
    }
    
    public void setPassiveInstance(final Boolean passiveInstance) {
        this.passiveInstance = passiveInstance;
    }
    
    public String getSourceUrl() {
        return this.sourceUrl;
    }
    
    public void setSourceUrl(final String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public List<WorkflowMappingServerSyncData> getWorkflows() {
        return this.workflows;
    }
    
    public void setWorkflows(final List<WorkflowMappingServerSyncData> workflows) {
        this.workflows = workflows;
    }
    
    private static ContractFieldMappingEntryService getContractFieldMappingEntryService() {
        return (ContractFieldMappingEntryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractFieldMappingEntryService.class);
    }
}
