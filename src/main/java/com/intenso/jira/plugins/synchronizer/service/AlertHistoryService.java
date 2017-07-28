// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.AlertHistory;

public interface AlertHistoryService extends GenericService<AlertHistory>
{
    AlertHistory create(final String p0);
    
    List<AlertHistory> findByMessage(final String p0);
    
    void clean();
}
