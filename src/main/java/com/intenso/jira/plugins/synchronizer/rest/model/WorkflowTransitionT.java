// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class WorkflowTransitionT
{
    @JsonProperty
    private String transitionId;
    @JsonProperty
    private String transitionName;
    
    public WorkflowTransitionT() {
        this.transitionId = "";
        this.transitionName = "";
    }
    
    public String getTransitionId() {
        return this.transitionId;
    }
    
    public void setTransitionId(final String transitionId) {
        this.transitionId = transitionId;
    }
    
    public String getTransitionName() {
        return this.transitionName;
    }
    
    public void setTransitionName(final String transitionName) {
        this.transitionName = transitionName;
    }
}
