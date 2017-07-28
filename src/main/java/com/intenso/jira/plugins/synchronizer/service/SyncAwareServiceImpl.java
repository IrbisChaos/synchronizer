// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Entity;

public abstract class SyncAwareServiceImpl<T extends Entity> extends GenericServiceImpl<T> implements SyncAwareService<T>
{
    private static Integer OUT_OF_SYNC;
    private static Integer ON_SYNC;
    
    public SyncAwareServiceImpl(final ActiveObjects dao, final Class<T> type) {
        super(dao, type);
    }
    
    @Override
    public void makeConnectionOutOfSync(final Integer connectionId) {
        if (connectionId == null) {
            this.getLogger().warn(ExtendedLoggerMessageType.CFG, "Trying to make connection out of sync but such connection not exist!");
            return;
        }
        final Connection connection = (Connection)this.getDao().get((Class)Connection.class, (Object)connectionId);
        connection.setOutOfSync(SyncAwareServiceImpl.OUT_OF_SYNC);
        connection.save();
    }
    
    @Override
    public void makeConnectionInSync(final Integer connectionId) {
        if (connectionId == null) {
            this.getLogger().warn(ExtendedLoggerMessageType.CFG, "Trying to make not existing connection synchronized!");
            return;
        }
        final Connection connection = (Connection)this.getDao().get((Class)Connection.class, (Object)connectionId);
        connection.setOutOfSync(SyncAwareServiceImpl.ON_SYNC);
        connection.save();
    }
    
    static {
        SyncAwareServiceImpl.OUT_OF_SYNC = 1;
        SyncAwareServiceImpl.ON_SYNC = 0;
    }
}
