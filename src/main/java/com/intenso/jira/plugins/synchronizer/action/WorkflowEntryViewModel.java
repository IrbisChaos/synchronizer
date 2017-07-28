// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

public class WorkflowEntryViewModel
{
    private String prevStatusName;
    private String prevStatusId;
    private String prevStatusColor;
    private String currStatusName;
    private String currStatusId;
    private String currStatusColor;
    private String transitionId;
    private String transitionName;
    
    public WorkflowEntryViewModel() {
        this.prevStatusName = "";
        this.prevStatusId = "";
        this.prevStatusColor = "";
        this.currStatusName = "";
        this.currStatusId = "";
        this.currStatusColor = "";
        this.transitionId = "";
        this.transitionName = "";
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
}
