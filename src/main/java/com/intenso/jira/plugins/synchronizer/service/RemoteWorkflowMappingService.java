// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.RemoteWorkflowMapping;

public interface RemoteWorkflowMappingService extends GenericService<RemoteWorkflowMapping>
{
    RemoteWorkflowMapping create(final Integer p0, final Integer p1, final String p2, final String p3, final Integer p4);
    
    List<RemoteWorkflowMapping> findByConnection(final Integer p0);
}
