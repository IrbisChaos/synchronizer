// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.boon.json.annotations.JsonProperty;
import java.util.List;

public class RemoteWorkflowConfigurationT
{
    @JsonProperty("configuration")
    public List<RemoteWorkflowMappingT> configuration;
    
    public List<RemoteWorkflowMappingT> getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final List<RemoteWorkflowMappingT> configuration) {
        this.configuration = configuration;
    }
}
