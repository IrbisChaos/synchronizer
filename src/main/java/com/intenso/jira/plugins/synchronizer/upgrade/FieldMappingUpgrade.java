// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.upgrade;

import com.atlassian.jira.component.ComponentAccessor;
import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;
import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.ArrayList;
import com.atlassian.sal.api.message.Message;
import java.util.Collection;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingService;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;

public class FieldMappingUpgrade implements PluginUpgradeTask
{
    private FieldMappingService fieldMappingService;
    private FieldMappingEntryService fieldMappingEntryService;
    private ContractFieldMappingEntryService contractFieldMappingEntryService;
    private ContractService contractService;
    
    public int getBuildNumber() {
        return 2;
    }
    
    public String getShortDescription() {
        return "Moving field mapping to contracts. Introducing field mapping templates.";
    }
    
    public Collection<Message> doUpgrade() throws Exception {
        final List<Contract> contracts = this.getContractService().findAll();
        final Collection<Message> messages = new ArrayList<Message>();
        for (final Contract c : contracts) {
            final List<FieldMappingEntry> fieldMappingEntries = this.getFieldMappingEntryService().findByMapping(c.getFieldMapping());
            for (final FieldMappingEntry fme : fieldMappingEntries) {
                final FieldMapping fm = this.getFieldMappingService().get(fme.getFieldMappingId());
                this.getContractFieldMappingEntryService().create(c.getID(), fme.getLocalFieldId(), fme.getLocalFieldName(), fme.getRemoteFieldName());
            }
        }
        return messages;
    }
    
    public String getPluginKey() {
        return "com.intenso.jira.plugins.synchronizer";
    }
    
    public FieldMappingService getFieldMappingService() {
        if (this.fieldMappingService == null) {
            this.fieldMappingService = (FieldMappingService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)FieldMappingService.class);
        }
        return this.fieldMappingService;
    }
    
    public FieldMappingEntryService getFieldMappingEntryService() {
        if (this.fieldMappingEntryService == null) {
            this.fieldMappingEntryService = (FieldMappingEntryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)FieldMappingEntryService.class);
        }
        return this.fieldMappingEntryService;
    }
    
    public ContractFieldMappingEntryService getContractFieldMappingEntryService() {
        if (this.contractFieldMappingEntryService == null) {
            this.contractFieldMappingEntryService = (ContractFieldMappingEntryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractFieldMappingEntryService.class);
        }
        return this.contractFieldMappingEntryService;
    }
    
    public ContractService getContractService() {
        if (this.contractService == null) {
            this.contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
        }
        return this.contractService;
    }
}
