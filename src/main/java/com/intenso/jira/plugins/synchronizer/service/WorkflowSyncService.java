// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowMappingT;
import com.intenso.jira.plugins.synchronizer.action.WorkflowMapping;
import java.util.List;

public interface WorkflowSyncService
{
    List<WorkflowMapping> getWorkflowMappingList();
    
    boolean isValidMapping(final Integer p0);
    
    boolean isValidMappingName(final String p0);
    
    String getMappingDisplayName(final Integer p0);
    
    Boolean isRestApiTransition(final Integer p0, final String p1);
    
    Integer getTransitionId(final Integer p0, final String p1);
    
    String getOutgoingTransitionText(final Integer p0, final Integer p1, final Integer p2);
    
    String getConfigurationJSONString();
    
    String getRemoteConfigurationJSONString();
    
    void saveConfigurationJSONString(final String p0);
    
    Integer getConfigurationId();
    
    void deleteConfigurationById(final Integer p0);
    
    WorkflowMappingT getWorkflowMapping(final Integer p0);
    
    boolean getOutgoingTransitionResolution(final Integer p0, final Integer p1, final Integer p2);
}
