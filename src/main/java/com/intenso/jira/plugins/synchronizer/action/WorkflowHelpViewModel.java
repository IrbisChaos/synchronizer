// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import java.util.ArrayList;
import java.util.List;

public class WorkflowHelpViewModel
{
    private String id;
    private String name;
    private List<WorkflowEntryViewModel> entries;
    
    public WorkflowHelpViewModel(final String id, final String name) {
        this.entries = new ArrayList<WorkflowEntryViewModel>();
        this.id = id;
        this.name = name;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<WorkflowEntryViewModel> getEntries() {
        return this.entries;
    }
    
    public void setEntries(final List<WorkflowEntryViewModel> entries) {
        this.entries = entries;
    }
}
