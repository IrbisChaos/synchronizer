// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.util.Iterator;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Contract;

public class HashUtils
{
    private static final int prime = 31;
    
    public static Integer getHash(final Contract contract, final List<ContractEvents> createEvents, final List<ContractEvents> updateEvents) {
        final List<ContractFieldMappingEntry> entries = getContractFieldMappingEntryService().findByContract(contract.getID());
        Integer create = 0;
        Integer update = 0;
        if (createEvents != null && createEvents.size() > 0) {
            create = 1;
        }
        if (updateEvents != null && updateEvents.size() > 0) {
            update = 1;
        }
        int result = 1;
        result = 31 * result + ((contract.getAttachments() == null) ? 0 : contract.getAttachments().hashCode());
        result = 31 * result + ((contract.getComments() == null) ? 0 : contract.getComments().hashCode());
        result = 31 * result + ((contract == null) ? 0 : contract.hashCode());
        result = 31 * result + ((create == null) ? 0 : create.hashCode());
        result = 31 * result + ((update == null) ? 0 : update.hashCode());
        result = 31 * result + getHash(entries);
        return result;
    }
    
    private static ContractFieldMappingEntryService getContractFieldMappingEntryService() {
        return (ContractFieldMappingEntryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractFieldMappingEntryService.class);
    }
    
    private static int getHash(final List<ContractFieldMappingEntry> entries) {
        int result = 1;
        for (final ContractFieldMappingEntry ce : entries) {
            result = 31 * result + getHash(ce);
        }
        return result;
    }
    
    private static int getHash(final ContractFieldMappingEntry entry) {
        int result = 1;
        if (entry != null) {
            result = 31 * result + entry.getLocalFieldId().hashCode();
            result = 31 * result + entry.getLocalFieldName().hashCode();
            if (entry.getRemoteFieldName() != null) {
                result = 31 * result + entry.getRemoteFieldName().hashCode();
            }
        }
        return result;
    }
}
