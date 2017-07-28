// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.entity.RemoteFieldMapping;

public interface RemoteFieldMappingService extends GenericService<RemoteFieldMapping>
{
    RemoteFieldMapping create(final String p0, final Integer p1, final String p2, final Integer p3);
    
    Boolean sendConfiguration(final Connection p0);
    
    List<RemoteFieldMapping> findByContractAndConnection(final String p0, final Integer p1);
    
    RemoteFieldMapping findByContractAndConnectionAndName(final String p0, final Integer p1, final String p2);
}
