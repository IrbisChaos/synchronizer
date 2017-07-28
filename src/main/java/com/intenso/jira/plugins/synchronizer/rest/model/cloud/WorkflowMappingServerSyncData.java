// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowIncomingTransitionsT;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowMappingT;
import java.util.ArrayList;
import java.util.List;
import org.boon.json.annotations.JsonInclude;

public class WorkflowMappingServerSyncData
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    private List<WorkflowTransitionMappingServerSyncData> transitionMappings;
    
    public WorkflowMappingServerSyncData() {
        this.transitionMappings = new ArrayList<WorkflowTransitionMappingServerSyncData>();
    }
    
    public WorkflowMappingServerSyncData(final RemoteWorkflowMappingT workflowMapping) {
        this.transitionMappings = new ArrayList<WorkflowTransitionMappingServerSyncData>();
        this.id = (long)workflowMapping.getWorkflowMappingId();
        this.name = workflowMapping.getWorkflowMappingDisplayName();
        for (final RemoteWorkflowIncomingTransitionsT incomingTransition : workflowMapping.getIncomingTransitions()) {
            this.transitionMappings.add(new WorkflowTransitionMappingServerSyncData(incomingTransition));
        }
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<WorkflowTransitionMappingServerSyncData> getTransitionMappings() {
        return this.transitionMappings;
    }
    
    public void setTransitionMappings(final List<WorkflowTransitionMappingServerSyncData> transitionMappings) {
        this.transitionMappings = transitionMappings;
    }
}
