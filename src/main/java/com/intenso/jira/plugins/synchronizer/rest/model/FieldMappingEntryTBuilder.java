// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.Field;
import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.intenso.jira.plugins.synchronizer.utils.Builder;

public class FieldMappingEntryTBuilder implements Builder<FieldMappingEntryT>
{
    private Integer id;
    private Integer fieldMappingId;
    private String localFieldId;
    private String localFieldIdName;
    private String localFieldName;
    private String remoteFieldName;
    private CustomFieldManager customFieldManager;
    private FieldManager fieldManager;
    
    public FieldMappingEntryTBuilder(final FieldMappingEntry fme) {
        this.id(fme.getID()).fieldMappingId(fme.getFieldMappingId()).localFieldId(fme.getLocalFieldId()).localFieldIdName(this.getLocalFieldIdName(fme.getLocalFieldId())).localFieldName(fme.getLocalFieldName());
    }
    
    private String getLocalFieldIdName(final String localFieldId) {
        Field field = this.getFieldManager().getField(localFieldId);
        if (field == null) {
            field = (Field)this.getCustomFieldManager().getCustomFieldObject(localFieldId);
        }
        if (field != null) {
            return field.getName();
        }
        return localFieldId;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public FieldMappingEntryTBuilder id(final Integer id) {
        this.id = id;
        return this;
    }
    
    public Integer getFieldMappingId() {
        return this.fieldMappingId;
    }
    
    public FieldMappingEntryTBuilder fieldMappingId(final Integer fieldMappingId) {
        this.fieldMappingId = fieldMappingId;
        return this;
    }
    
    public String getLocalFieldId() {
        return this.localFieldId;
    }
    
    public FieldMappingEntryTBuilder localFieldId(final String localFieldId) {
        this.localFieldId = localFieldId;
        return this;
    }
    
    public String getLocalFieldIdName() {
        return this.localFieldIdName;
    }
    
    public FieldMappingEntryTBuilder localFieldIdName(final String localFieldIdName) {
        this.localFieldIdName = localFieldIdName;
        return this;
    }
    
    public String getLocalFieldName() {
        return this.localFieldName;
    }
    
    public FieldMappingEntryTBuilder localFieldName(final String localFieldName) {
        this.localFieldName = localFieldName;
        return this;
    }
    
    public String getRemoteFieldName() {
        return this.remoteFieldName;
    }
    
    public FieldMappingEntryTBuilder remoteFieldName(final String remoteFieldName) {
        this.remoteFieldName = remoteFieldName;
        return this;
    }
    
    public CustomFieldManager getCustomFieldManager() {
        if (this.customFieldManager == null) {
            this.customFieldManager = (CustomFieldManager)ComponentAccessor.getComponent((Class)CustomFieldManager.class);
        }
        return this.customFieldManager;
    }
    
    public FieldManager getFieldManager() {
        if (this.fieldManager == null) {
            this.fieldManager = (FieldManager)ComponentAccessor.getComponent((Class)FieldManager.class);
        }
        return this.fieldManager;
    }
    
    @Override
    public FieldMappingEntryT build() {
        return new FieldMappingEntryT(this);
    }
}
