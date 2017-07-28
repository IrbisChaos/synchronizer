// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.sql.Timestamp;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.Statistic;

public interface StatisticService extends GenericService<Statistic>
{
    Statistic create(final Integer p0, final Integer p1, final MessageType p2, final QueueType p3, final Long p4, final Integer p5, final Long p6);
    
    Statistic create(final Timestamp p0, final Integer p1, final Integer p2, final MessageType p3, final QueueType p4, final Long p5, final Integer p6, final Long p7);
}
