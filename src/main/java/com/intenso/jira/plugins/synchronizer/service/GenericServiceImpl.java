// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import net.java.ao.RawEntity;
import java.util.Arrays;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Entity;

public abstract class GenericServiceImpl<T extends Entity> implements GenericService<T>
{
    private ActiveObjects dao;
    private final Class<T> type;
    private ExtendedLogger logger;
    
    public GenericServiceImpl(final ActiveObjects dao, final Class<T> type) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.dao = dao;
        this.type = type;
    }
    
    @Override
    public T get(final Integer id) {
        return (T)this.dao.get((Class)this.type, (Object)id);
    }
    
    @Override
    public List<T> getAll() {
        return Arrays.asList((T[])this.dao.find((Class)this.type));
    }
    
    @Override
    public T update(final T entity) {
        entity.save();
        return entity;
    }
    
    @Override
    public void delete(final Integer id) {
        final Entity e = (Entity)this.dao.get((Class)this.type, (Object)id);
        if (e != null) {
            this.dao.delete(new RawEntity[] { e });
            this.dao.flush(new RawEntity[] { e });
        }
    }
    
    @Override
    public void delete(final T entity) {
        this.dao.delete(new RawEntity[] { entity });
    }
    
    @Override
    public T find(final Integer id) {
        return (T)this.dao.get((Class)this.type, (Object)id);
    }
    
    public ActiveObjects getDao() {
        return this.dao;
    }
    
    public void setDao(final ActiveObjects dao) {
        this.dao = dao;
    }
    
    public Class<T> getType() {
        return this.type;
    }
    
    public ExtendedLogger getLogger() {
        return this.logger;
    }
}
