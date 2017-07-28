// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowEntryViewModelT
{
    @JsonProperty
    private String prevStatusName;
    @JsonProperty
    private String prevStatusId;
    @JsonProperty
    private String prevStatusColor;
    @JsonProperty
    private String currStatusName;
    @JsonProperty
    private String currStatusId;
    @JsonProperty
    private String currStatusColor;
    @JsonProperty
    List<WorkflowTransitionT> transitions;
    
    public WorkflowEntryViewModelT() {
        this.prevStatusName = "";
        this.prevStatusId = "";
        this.prevStatusColor = "";
        this.currStatusName = "";
        this.currStatusId = "";
        this.currStatusColor = "";
        this.transitions = new ArrayList<WorkflowTransitionT>();
    }
    
    public String getPrevStatusName() {
        return this.prevStatusName;
    }
    
    public void setPrevStatusName(final String prevStatusName) {
        this.prevStatusName = prevStatusName;
    }
    
    public String getPrevStatusId() {
        return this.prevStatusId;
    }
    
    public void setPrevStatusId(final String prevStatusId) {
        this.prevStatusId = prevStatusId;
    }
    
    public String getCurrStatusName() {
        return this.currStatusName;
    }
    
    public void setCurrStatusName(final String currStatusName) {
        this.currStatusName = currStatusName;
    }
    
    public String getCurrStatusId() {
        return this.currStatusId;
    }
    
    public void setCurrStatusId(final String currStatusId) {
        this.currStatusId = currStatusId;
    }
    
    public String getPrevStatusColor() {
        return this.prevStatusColor;
    }
    
    public void setPrevStatusColor(final String prevStatusColor) {
        this.prevStatusColor = prevStatusColor;
    }
    
    public String getCurrStatusColor() {
        return this.currStatusColor;
    }
    
    public void setCurrStatusColor(final String currStatusColor) {
        this.currStatusColor = currStatusColor;
    }
    
    public List<WorkflowTransitionT> getTransitions() {
        return this.transitions;
    }
    
    public void setTransitions(final List<WorkflowTransitionT> transitions) {
        this.transitions = transitions;
    }
}
