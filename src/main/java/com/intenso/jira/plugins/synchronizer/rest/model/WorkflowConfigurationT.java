// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.boon.json.annotations.JsonProperty;
import java.util.List;

public class WorkflowConfigurationT
{
    @JsonProperty("configuration")
    public List<WorkflowMappingT> configuration;
    
    public List<WorkflowMappingT> getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final List<WorkflowMappingT> configuration) {
        this.configuration = configuration;
    }
}
