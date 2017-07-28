// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.boon.json.annotations.JsonInclude;
import org.boon.json.annotations.JsonProperty;

public class WorkflowIncomingTransitionsT
{
    @JsonProperty("inTransitionText")
    public String inTransitionText;
    @JsonProperty("workflowTransitionId")
    public Integer workflowTransitionId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("useRestApi")
    public Boolean useRestApi;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("resolution")
    public Boolean resolution;
    
    public String getInTransitionText() {
        return this.inTransitionText;
    }
    
    public void setInTransitionText(final String inTransitionText) {
        this.inTransitionText = inTransitionText;
    }
    
    public Integer getWorkflowTransitionId() {
        return this.workflowTransitionId;
    }
    
    public void setWorkflowTransitionId(final Integer workflowTransitionId) {
        this.workflowTransitionId = workflowTransitionId;
    }
    
    public Boolean getUseRestApi() {
        return this.useRestApi;
    }
    
    public void setUseRestApi(final Boolean useRestApi) {
        this.useRestApi = useRestApi;
    }
    
    public Boolean getResolution() {
        return this.resolution;
    }
    
    public void setResolution(final Boolean resolution) {
        this.resolution = resolution;
    }
}
