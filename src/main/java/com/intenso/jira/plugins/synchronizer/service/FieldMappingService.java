// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;

public interface FieldMappingService extends GenericService<FieldMapping>
{
    FieldMapping save(final String p0);
    
    void save(final FieldMapping p0);
}
