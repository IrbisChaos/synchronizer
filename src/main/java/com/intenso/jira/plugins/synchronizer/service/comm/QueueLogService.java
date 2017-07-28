// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.entity.QueueLogLevel;
import java.util.Map;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import net.java.ao.Entity;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.entity.QueueLog;
import com.intenso.jira.plugins.synchronizer.service.GenericService;

public interface QueueLogService extends GenericService<QueueLog>
{
    QueueLog createOutLog(final QueueOut p0);
    
    QueueLog createQueueLog(final Integer p0, final String p1, final QueueIn p2);
    
    QueueLog createQueueLog(final Integer p0, final String p1, final String p2, final QueueIn p3);
    
    QueueLog createQueueLog(final Integer p0, final String p1, final QueueOut p2);
    
    QueueLog createQueueLog(final Response p0, final Entity p1, final Integer p2, final Long p3);
    
    List<QueueLog> findByQueueType(final QueueType p0, final Integer p1, final Integer p2);
    
    Integer countByType(final QueueType p0);
    
    List<QueueLog> findAllByFilter(final Map<String, Object> p0, final Integer p1, final Integer p2);
    
    Integer countAllByFilter(final Map<String, Object> p0);
    
    QueueLog createQueueLog(final QueueType p0, final QueueLogLevel p1, final String p2, final String p3, final Integer p4, final Integer p5, final Long p6, final Integer p7);
    
    QueueLog createQueueLog(final Integer p0, final String p1);
    
    void clearOutdated(final Integer p0);
}
