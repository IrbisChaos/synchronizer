// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "name" })
@Table("FIELD_MAPPING")
public interface FieldMapping extends Entity
{
    @Indexed
    String getName();
    
    void setName(final String p0);
}
