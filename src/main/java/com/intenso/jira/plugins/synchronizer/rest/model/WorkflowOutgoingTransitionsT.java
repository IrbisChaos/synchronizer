// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.boon.json.annotations.JsonInclude;
import org.boon.json.annotations.JsonProperty;

public class WorkflowOutgoingTransitionsT
{
    @JsonProperty("prevStatusId")
    public Integer prevStatusId;
    @JsonProperty("currStatusId")
    public Integer currStatusId;
    @JsonProperty("outTransitionText")
    public String outTransitionText;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("resolution")
    public Boolean resolution;
    
    public Integer getPrevStatusId() {
        return this.prevStatusId;
    }
    
    public void setPrevStatusId(final Integer prevStatusId) {
        this.prevStatusId = prevStatusId;
    }
    
    public Integer getCurrStatusId() {
        return this.currStatusId;
    }
    
    public void setCurrStatusId(final Integer currStatusId) {
        this.currStatusId = currStatusId;
    }
    
    public String getOutTransitionText() {
        return this.outTransitionText;
    }
    
    public void setOutTransitionText(final String outTransitionText) {
        this.outTransitionText = outTransitionText;
    }
    
    public Boolean getResolution() {
        return this.resolution;
    }
    
    public void setResolution(final Boolean resolution) {
        this.resolution = resolution;
    }
}
