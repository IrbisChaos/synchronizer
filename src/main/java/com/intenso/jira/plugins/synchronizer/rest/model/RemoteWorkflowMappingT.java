// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteWorkflowMappingT
{
    @JsonProperty("workflowMappingId")
    public Integer workflowMappingId;
    @JsonProperty("workflowMappingDisplayName")
    public String workflowMappingDisplayName;
    @JsonProperty("incomingTransitions")
    public List<RemoteWorkflowIncomingTransitionsT> incomingTransitions;
    @JsonProperty("workflowName")
    public String workflowName;
    @JsonProperty("remoteWorkflowName")
    public String remoteWorkflowName;
    
    public Integer getWorkflowMappingId() {
        return this.workflowMappingId;
    }
    
    public void setWorkflowMappingId(final Integer workflowMappingId) {
        this.workflowMappingId = workflowMappingId;
    }
    
    public String getWorkflowMappingDisplayName() {
        return this.workflowMappingDisplayName;
    }
    
    public void setWorkflowMappingDisplayName(final String workflowMappingDisplayName) {
        this.workflowMappingDisplayName = workflowMappingDisplayName;
    }
    
    public String getWorkflowName() {
        return this.workflowName;
    }
    
    public void setWorkflowName(final String workflowName) {
        this.workflowName = workflowName;
    }
    
    public String getRemoteWorkflowName() {
        return this.remoteWorkflowName;
    }
    
    public void setRemoteWorkflowName(final String remoteWorkflowName) {
        this.remoteWorkflowName = remoteWorkflowName;
    }
    
    public List<RemoteWorkflowIncomingTransitionsT> getIncomingTransitions() {
        return this.incomingTransitions;
    }
    
    public void setIncomingTransitions(final List<RemoteWorkflowIncomingTransitionsT> incomingTransitions) {
        this.incomingTransitions = incomingTransitions;
    }
}
