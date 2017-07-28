// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowHelpViewModelT
{
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<WorkflowEntryViewModelT> entries;
    
    public WorkflowHelpViewModelT(final String id, final String name) {
        this.entries = new ArrayList<WorkflowEntryViewModelT>();
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
    
    public List<WorkflowEntryViewModelT> getEntries() {
        return this.entries;
    }
    
    public void setEntries(final List<WorkflowEntryViewModelT> entries) {
        this.entries = entries;
    }
}
