// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Connection;

public interface ConnectionService extends SyncAwareService<Connection>
{
    Connection saveConnection(final String p0, final String p1, final String p2, final String p3, final String p4, final Integer p5, final String p6, final String p7, final Integer p8, final String p9);
    
    Connection get(final String p0);
    
    Connection findByUrl(final String p0);
    
    Connection findByAppKey(final String p0);
    
    Connection getConnectionForContract(final Integer p0);
    
    Integer countByAppKey(final String p0);
    
    List<Connection> findAllByAppKey(final String p0);
    
    List<Connection> getActiveModeConnectionsList();
    
    List<Connection> getPassiveModeConnectionList();
    
    int countAll();
}
