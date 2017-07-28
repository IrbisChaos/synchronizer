// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.ArrayList;
import java.util.List;
import org.boon.json.annotations.JsonInclude;

public class ContractServerSyncData
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contractWithOutTriggerName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contractName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String outTriggerName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;
    private boolean outTriggerCreateEvent;
    private boolean outTriggerUpdateEvent;
    private boolean outTriggerDeleteEvent;
    private boolean outTriggerSyncComments;
    private boolean outTriggerSyncAttachments;
    private boolean outTriggerSyncWorklogs;
    private List<ExposedFieldServerSyncData> exposedFields;
    
    public ContractServerSyncData() {
        this.active = false;
        this.exposedFields = new ArrayList<ExposedFieldServerSyncData>();
    }
    
    public ContractServerSyncData(final Contract connectionContract, final List<ContractFieldMappingEntry> mappingsForConnection) {
        this.active = false;
        this.exposedFields = new ArrayList<ExposedFieldServerSyncData>();
        final List<ContractEvents> createEvents = getContractService().getEventsForContract(connectionContract.getID(), EventType.CREATE);
        final List<ContractEvents> updateEvents = getContractService().getEventsForContract(connectionContract.getID(), EventType.UPDATE);
        final List<ContractEvents> deleteEvents = getContractService().getEventsForContract(connectionContract.getID(), EventType.DELETE);
        this.contractName = connectionContract.getContractName();
        this.outTriggerName = connectionContract.getContractName();
        this.contractWithOutTriggerName = this.contractName + "-" + this.outTriggerName;
        this.outTriggerCreateEvent = !createEvents.isEmpty();
        this.outTriggerUpdateEvent = !updateEvents.isEmpty();
        this.outTriggerDeleteEvent = !deleteEvents.isEmpty();
        this.outTriggerSyncComments = (connectionContract.getComments() != null && connectionContract.getComments() == 1);
        this.outTriggerSyncAttachments = (connectionContract.getAttachments() != null && connectionContract.getAttachments() == 1);
        this.outTriggerSyncWorklogs = (connectionContract.getWorklogs() != null && connectionContract.getWorklogs() == 1);
        this.active = connectionContract.getStatus().equals(0);
        for (final ContractFieldMappingEntry mapping : mappingsForConnection) {
            this.exposedFields.add(new ExposedFieldServerSyncData(mapping));
        }
    }
    
    public String getContractWithOutTriggerName() {
        return this.contractWithOutTriggerName;
    }
    
    public void setContractWithOutTriggerName(final String contractWithOutTriggerName) {
        this.contractWithOutTriggerName = contractWithOutTriggerName;
    }
    
    public String getContractName() {
        return this.contractName;
    }
    
    public void setContractName(final String contractName) {
        this.contractName = contractName;
    }
    
    public String getOutTriggerName() {
        return this.outTriggerName;
    }
    
    public void setOutTriggerName(final String outTriggerName) {
        this.outTriggerName = outTriggerName;
    }
    
    public boolean isOutTriggerCreateEvent() {
        return this.outTriggerCreateEvent;
    }
    
    public void setOutTriggerCreateEvent(final boolean outTriggerCreateEvent) {
        this.outTriggerCreateEvent = outTriggerCreateEvent;
    }
    
    public boolean isOutTriggerUpdateEvent() {
        return this.outTriggerUpdateEvent;
    }
    
    public void setOutTriggerUpdateEvent(final boolean outTriggerUpdateEvent) {
        this.outTriggerUpdateEvent = outTriggerUpdateEvent;
    }
    
    public boolean isOutTriggerDeleteEvent() {
        return this.outTriggerDeleteEvent;
    }
    
    public void setOutTriggerDeleteEvent(final boolean outTriggerDeleteEvent) {
        this.outTriggerDeleteEvent = outTriggerDeleteEvent;
    }
    
    public boolean isOutTriggerSyncComments() {
        return this.outTriggerSyncComments;
    }
    
    public void setOutTriggerSyncComments(final boolean outTriggerSyncComments) {
        this.outTriggerSyncComments = outTriggerSyncComments;
    }
    
    public boolean isOutTriggerSyncAttachments() {
        return this.outTriggerSyncAttachments;
    }
    
    public void setOutTriggerSyncAttachments(final boolean outTriggerSyncAttachments) {
        this.outTriggerSyncAttachments = outTriggerSyncAttachments;
    }
    
    public List<ExposedFieldServerSyncData> getExposedFields() {
        return this.exposedFields;
    }
    
    public void setExposedFields(final List<ExposedFieldServerSyncData> exposedFields) {
        this.exposedFields = exposedFields;
    }
    
    public Boolean getActive() {
        return this.active;
    }
    
    public void setActive(final Boolean active) {
        this.active = active;
    }
    
    private static ContractService getContractService() {
        return (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
    }
    
    public boolean isOutTriggerSyncWorklogs() {
        return this.outTriggerSyncWorklogs;
    }
    
    public void setOutTriggerSyncWorklogs(final boolean outTriggerSyncWorklogs) {
        this.outTriggerSyncWorklogs = outTriggerSyncWorklogs;
    }
}
