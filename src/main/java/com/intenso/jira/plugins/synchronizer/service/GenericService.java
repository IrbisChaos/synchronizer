// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import net.java.ao.Entity;

public interface GenericService<T extends Entity>
{
    T get(final Integer p0);
    
    List<T> getAll();
    
    T update(final T p0);
    
    void delete(final Integer p0);
    
    void delete(final T p0);
    
    T find(final Integer p0);
}
