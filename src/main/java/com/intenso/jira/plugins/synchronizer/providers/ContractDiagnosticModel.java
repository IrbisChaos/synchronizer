// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.providers;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ContractDiagnosticModel implements Serializable
{
    private static final long serialVersionUID = 1545656464L;
    public String name;
    public boolean isEnabled;
    public boolean isAttachementEnabled;
    public boolean isSynchronizeAllAttachmentsEnabled;
    public boolean isCommentEnabled;
    public boolean isCommentExternalEnabled;
    public boolean isAllCommentsSynchronizationEnabled;
    public boolean isWorkflowEnabled;
    public List<String> createEvents;
    public List<String> updateEvents;
    public List<String> deleteEvents;
    public String jqlConstraints;
    public int id;
    
    public ContractDiagnosticModel() {
        this.createEvents = new ArrayList<String>();
        this.updateEvents = new ArrayList<String>();
        this.deleteEvents = new ArrayList<String>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean getIsEnabled() {
        return this.isEnabled;
    }
    
    public boolean getIsAttachementEnabled() {
        return this.isAttachementEnabled;
    }
    
    public boolean getIsCommentEnabled() {
        return this.isCommentEnabled;
    }
    
    public boolean getIsSynchronizeAllAttachmentsEnabled() {
        return this.isSynchronizeAllAttachmentsEnabled;
    }
    
    public boolean getIsCommentExternalEnabled() {
        return this.isCommentExternalEnabled;
    }
    
    public boolean getIsWorkflowEnabled() {
        return this.isWorkflowEnabled;
    }
    
    public List<String> getCreateEvents() {
        return this.createEvents;
    }
    
    public List<String> getUpdateEvents() {
        return this.updateEvents;
    }
    
    public List<String> getDeleteEvents() {
        return this.deleteEvents;
    }
    
    public String getJqlConstraints() {
        return this.jqlConstraints;
    }
    
    public int getId() {
        return this.id;
    }
    
    public boolean getIsAllCommentsSynchronizationEnabled() {
        return this.isAllCommentsSynchronizationEnabled;
    }
}
