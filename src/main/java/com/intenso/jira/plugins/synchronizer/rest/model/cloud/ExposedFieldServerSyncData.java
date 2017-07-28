// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

import com.intenso.jira.plugins.synchronizer.utils.SettingUtils;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import org.boon.json.annotations.JsonInclude;

public class ExposedFieldServerSyncData
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String jiraFieldId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String jiraFieldType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    
    public ExposedFieldServerSyncData() {
    }
    
    public ExposedFieldServerSyncData(final ContractFieldMappingEntry mapping) {
        this.jiraFieldId = mapping.getLocalFieldId();
        this.jiraFieldType = SettingUtils.getFieldType(mapping.getLocalFieldId()).toString();
        this.name = mapping.getLocalFieldName();
    }
    
    public String getJiraFieldId() {
        return this.jiraFieldId;
    }
    
    public void setJiraFieldId(final String jiraFieldId) {
        this.jiraFieldId = jiraFieldId;
    }
    
    public String getJiraFieldType() {
        return this.jiraFieldType;
    }
    
    public void setJiraFieldType(final String jiraFieldType) {
        this.jiraFieldType = jiraFieldType;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
