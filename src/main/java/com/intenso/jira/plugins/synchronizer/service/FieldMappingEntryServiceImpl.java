// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import net.java.ao.Entity;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;
import java.util.Map;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;

public class FieldMappingEntryServiceImpl extends GenericServiceImpl<FieldMappingEntry> implements FieldMappingEntryService
{
    public static final String COL_FIELD_MAPPING_ID = "FIELD_MAPPING_ID";
    public static final String COL_LOCAL_FIELD_ID = "LOCAL_FIELD_ID";
    public static final String COL_LOCAL_FIELD_NAME = "LOCAL_FIELD_NAME";
    
    public FieldMappingEntryServiceImpl(final ActiveObjects dao) {
        super(dao, FieldMappingEntry.class);
    }
    
    @Override
    public FieldMappingEntry save(final Integer fieldMappingId, final String localFieldId, final String localFieldName, final String remoteFieldName) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("FIELD_MAPPING_ID", fieldMappingId);
        params.put("LOCAL_FIELD_ID", localFieldId);
        params.put("LOCAL_FIELD_NAME", localFieldName);
        final FieldMappingEntry entry = (FieldMappingEntry)this.getDao().create((Class)FieldMappingEntry.class, (Map)params);
        return entry;
    }
    
    @Override
    public void save(final FieldMappingEntry mapping) {
        mapping.save();
        final FieldMapping fm = (FieldMapping)this.getDao().get((Class)FieldMapping.class, (Object)mapping.getFieldMappingId());
    }
    
    @Override
    public List<FieldMappingEntry> findByMapping(final Integer fieldMappingId) {
        final FieldMappingEntry[] entries = (FieldMappingEntry[])this.getDao().find((Class)FieldMappingEntry.class, Query.select().where("FIELD_MAPPING_ID = ?", new Object[] { fieldMappingId }));
        return Arrays.asList(entries);
    }
    
    @Override
    public void delete(final FieldMappingEntry entity) {
        super.delete(entity);
    }
    
    @Override
    public void delete(final Integer id) {
        super.delete(id);
    }
}
