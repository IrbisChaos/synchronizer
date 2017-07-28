// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMappingEntryT
{
    @JsonProperty
    private Integer id;
    @JsonProperty
    private Integer fieldMappingId;
    @JsonProperty
    private String localFieldId;
    @JsonProperty
    private String localFieldIdName;
    @JsonProperty
    private String localFieldName;
    @JsonProperty
    private String remoteFieldName;
    
    public FieldMappingEntryT() {
    }
    
    public FieldMappingEntryT(final FieldMappingEntryTBuilder e) {
        this.setId(e.getId());
        this.setFieldMappingId(e.getFieldMappingId());
        this.setLocalFieldId(e.getLocalFieldId());
        this.setLocalFieldIdName(e.getLocalFieldIdName());
        this.setLocalFieldName(e.getLocalFieldName());
        this.setRemoteFieldName(e.getRemoteFieldName());
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getLocalFieldId() {
        return this.localFieldId;
    }
    
    public void setLocalFieldId(final String localFieldId) {
        this.localFieldId = localFieldId;
    }
    
    public String getLocalFieldName() {
        return this.localFieldName;
    }
    
    public void setLocalFieldName(final String localFieldName) {
        this.localFieldName = localFieldName;
    }
    
    public String getRemoteFieldName() {
        return this.remoteFieldName;
    }
    
    public void setRemoteFieldName(final String remoteFieldName) {
        this.remoteFieldName = remoteFieldName;
    }
    
    public Integer getFieldMappingId() {
        return this.fieldMappingId;
    }
    
    public void setFieldMappingId(final Integer fieldMappingId) {
        this.fieldMappingId = fieldMappingId;
    }
    
    public String getLocalFieldIdName() {
        return this.localFieldIdName;
    }
    
    public void setLocalFieldIdName(final String localFieldIdName) {
        this.localFieldIdName = localFieldIdName;
    }
}
