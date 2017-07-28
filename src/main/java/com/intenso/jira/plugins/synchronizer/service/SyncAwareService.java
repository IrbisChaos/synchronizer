// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import net.java.ao.Entity;

public interface SyncAwareService<T extends Entity> extends GenericService<T>
{
    void makeConnectionOutOfSync(final Integer p0);
    
    void makeConnectionInSync(final Integer p0);
}
