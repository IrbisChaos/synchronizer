// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteFieldMappingT
{
    @JsonProperty
    private String contractName;
    @JsonProperty
    private String fieldName;
    @JsonProperty
    private Integer fieldType;
    
    public RemoteFieldMappingT() {
    }
    
    public RemoteFieldMappingT(final ContractFieldMappingEntry entry, final Integer fieldType, final String contractName) {
        this.fieldName = entry.getLocalFieldName();
        this.fieldType = fieldType;
        this.contractName = contractName;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public Integer getFieldType() {
        return this.fieldType;
    }
    
    public void setFieldType(final Integer fieldType) {
        this.fieldType = fieldType;
    }
    
    public String getContractName() {
        return this.contractName;
    }
    
    public void setContractName(final String contractName) {
        this.contractName = contractName;
    }
}
