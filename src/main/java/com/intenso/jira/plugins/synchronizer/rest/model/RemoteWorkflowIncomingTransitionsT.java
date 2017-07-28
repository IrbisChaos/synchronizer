// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.boon.json.annotations.JsonInclude;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteWorkflowIncomingTransitionsT
{
    @JsonProperty("inTransitionText")
    public String inTransitionText;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("resolution")
    public Boolean resolution;
    
    public String getInTransitionText() {
        return this.inTransitionText;
    }
    
    public void setInTransitionText(final String inTransitionText) {
        this.inTransitionText = inTransitionText;
    }
    
    public Boolean getResolution() {
        return this.resolution;
    }
    
    public void setResolution(final Boolean resolution) {
        this.resolution = resolution;
    }
}
