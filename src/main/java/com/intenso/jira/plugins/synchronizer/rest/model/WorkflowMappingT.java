// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.List;
import org.boon.json.annotations.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowMappingT
{
    @JsonProperty("workflowMappingId")
    public Integer workflowMappingId;
    @JsonProperty("workflowMappingDisplayName")
    public String workflowMappingDisplayName;
    @JsonProperty("workflowId")
    public Integer workflowId;
    @JsonProperty("outgoingTransitions")
    public List<WorkflowOutgoingTransitionsT> outgoingTransitions;
    @JsonProperty("incomingTransitions")
    public List<WorkflowIncomingTransitionsT> incomingTransitions;
    @JsonProperty("workflowName")
    public String workflowName;
    @JsonProperty("remoteWorkflowName")
    public String remoteWorkflowName;
    @JsonProperty("remoteWorkflowId")
    public String remoteWorkflowId;
    
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
    
    public Integer getWorkflowId() {
        return this.workflowId;
    }
    
    public void setWorkflowId(final Integer workflowId) {
        this.workflowId = workflowId;
    }
    
    public List<WorkflowOutgoingTransitionsT> getOutgoingTransitions() {
        return this.outgoingTransitions;
    }
    
    public void setOutgoingTransitions(final List<WorkflowOutgoingTransitionsT> outgoingTransitions) {
        this.outgoingTransitions = outgoingTransitions;
    }
    
    public List<WorkflowIncomingTransitionsT> getIncomingTransitions() {
        return this.incomingTransitions;
    }
    
    public void setIncomingTransitions(final List<WorkflowIncomingTransitionsT> incomingTransitions) {
        this.incomingTransitions = incomingTransitions;
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
    
    public String getRemoteWorkflowId() {
        return this.remoteWorkflowId;
    }
    
    public void setRemoteWorkflowId(final String remoteWorkflowId) {
        this.remoteWorkflowId = remoteWorkflowId;
    }
}
