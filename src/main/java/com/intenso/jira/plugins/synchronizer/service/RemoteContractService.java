// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.RemoteContract;

public interface RemoteContractService extends GenericService<RemoteContract>
{
    void clearConfiguration(final Integer p0);
    
    RemoteContract create(final Integer p0, final String p1, final Integer p2, final Integer p3, final Integer p4, final Integer p5, final Integer p6, final Integer p7, final Integer p8);
    
    List<RemoteContract> findByConnection(final Integer p0);
    
    RemoteContract findByName(final String p0, final Integer p1);
}
