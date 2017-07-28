// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.Table;
import net.java.ao.Entity;

@Table("FIELD_MAPPING_ENTRY")
public interface FieldMappingEntry extends Entity
{
    Integer getFieldMappingId();
    
    String getLocalFieldId();
    
    String getLocalFieldName();
    
    @Deprecated
    String getRemoteFieldName();
    
    void setFieldMappingId(final Integer p0);
    
    void setLocalFieldId(final String p0);
    
    void setLocalFieldName(final String p0);
    
    @Deprecated
    void setRemoteFieldName(final String p0);
}
