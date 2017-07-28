// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractFieldMappingEntryT
{
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String localFieldId;
    @JsonProperty
    private String localFieldIdName;
    @JsonProperty
    private String localFieldName;
    @JsonProperty
    private String remoteFieldName;
    
    public ContractFieldMappingEntryT(final ContractFieldMappingEntry e) {
        this.localFieldId = e.getLocalFieldId();
        this.localFieldName = e.getLocalFieldName();
        this.remoteFieldName = e.getRemoteFieldName();
        this.id = e.getID();
        if (this.localFieldId != null) {
            this.localFieldIdName = FieldMappingUtils.prepareFields().get(e.getLocalFieldId());
            if (this.localFieldIdName == null) {
                this.localFieldIdName = this.localFieldId;
            }
        }
    }
    
    public ContractFieldMappingEntryT(final FieldMappingEntry e) {
        this.localFieldId = e.getLocalFieldId();
        this.localFieldName = e.getLocalFieldName();
        this.remoteFieldName = "";
        this.id = -1;
        if (this.localFieldId != null) {
            this.localFieldIdName = FieldMappingUtils.prepareFields().get(e.getLocalFieldId());
            if (this.localFieldIdName == null) {
                this.localFieldIdName = this.localFieldId;
            }
        }
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
    
    public String getLocalFieldIdName() {
        return this.localFieldIdName;
    }
    
    public void setLocalFieldIdName(final String localFieldIdName) {
        this.localFieldIdName = localFieldIdName;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
}
