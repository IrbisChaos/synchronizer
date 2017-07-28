// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowIncomingTransitionsT;

public class WorkflowTransitionMappingServerSyncData
{
    private String outgoingCode;
    private Boolean setResolution;
    
    public WorkflowTransitionMappingServerSyncData() {
    }
    
    public WorkflowTransitionMappingServerSyncData(final RemoteWorkflowIncomingTransitionsT incomingTransition) {
        this.outgoingCode = incomingTransition.getInTransitionText();
        this.setResolution = incomingTransition.getResolution();
    }
    
    public String getOutgoingCode() {
        return this.outgoingCode;
    }
    
    public void setOutgoingCode(final String outgoingCode) {
        this.outgoingCode = outgoingCode;
    }
    
    public Boolean getSetResolution() {
        return this.setResolution;
    }
    
    public void setSetResolution(final Boolean setResolution) {
        this.setResolution = setResolution;
    }
}
