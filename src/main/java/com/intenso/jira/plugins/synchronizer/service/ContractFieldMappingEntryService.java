// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;

public interface ContractFieldMappingEntryService extends SyncAwareService<ContractFieldMappingEntry>
{
    ContractFieldMappingEntry create(final Integer p0, final String p1, final String p2, final String p3);
    
    List<ContractFieldMappingEntry> findByContract(final Integer p0);
    
    void deleteByContract(final Integer p0);
    
    Integer countByContract(final Integer p0);
    
    List<ContractFieldMappingEntry> findByConnection(final Integer p0);
}
