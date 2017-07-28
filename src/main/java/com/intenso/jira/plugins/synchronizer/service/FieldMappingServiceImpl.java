// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Map;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;

public class FieldMappingServiceImpl extends GenericServiceImpl<FieldMapping> implements FieldMappingService
{
    public FieldMappingServiceImpl(final ActiveObjects dao) {
        super(dao, FieldMapping.class);
    }
    
    @Override
    public FieldMapping save(final String name) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("NAME", name);
        final FieldMapping fm = (FieldMapping)this.getDao().create((Class)FieldMapping.class, (Map)params);
        return fm;
    }
    
    @Override
    public void delete(final Integer id) {
        super.delete(id);
    }
    
    @Override
    public void save(final FieldMapping result) {
        result.save();
    }
}
