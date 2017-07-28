// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;

public interface FieldMappingEntryService extends GenericService<FieldMappingEntry>
{
    FieldMappingEntry save(final Integer p0, final String p1, final String p2, final String p3);
    
    void save(final FieldMappingEntry p0);
    
    List<FieldMappingEntry> findByMapping(final Integer p0);
}
