// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import java.sql.Timestamp;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteConfigurationWrapperT
{
    @JsonProperty
    private Timestamp timestamp;
    @JsonProperty
    private Integer connection;
    @JsonProperty
    private List<RemoteFieldMappingT> configuration;
    @JsonProperty
    private List<RemoteContractT> contracts;
    @JsonProperty
    private RemoteWorkflowConfigurationT workflows;
    
    public List<RemoteFieldMappingT> getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final List<RemoteFieldMappingT> configuration) {
        this.configuration = configuration;
    }
    
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getConnection() {
        return this.connection;
    }
    
    public void setConnection(final Integer connection) {
        this.connection = connection;
    }
    
    public List<RemoteContractT> getContracts() {
        return this.contracts;
    }
    
    public void setContracts(final List<RemoteContractT> contracts) {
        this.contracts = contracts;
    }
    
    public RemoteWorkflowConfigurationT getWorkflows() {
        return this.workflows;
    }
    
    public void setWorkflows(final RemoteWorkflowConfigurationT workflows) {
        this.workflows = workflows;
    }
}
