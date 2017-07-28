// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.List;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.service.GenericService;

public interface QueueOutService extends GenericService<QueueOut>
{
    QueueOut create(final Contract p0, final MessageType p1, final QueueStatus p2, final String p3, final Long p4, final Integer p5);
    
    QueueOut create(final Integer p0, final Integer p1, final MessageType p2, final QueueStatus p3, final String p4, final Long p5, final Integer p6);
    
    QueueOut create(final Contract p0, final MessageType p1, final QueueStatus p2, final String p3, final Long p4, final Integer p5, final Date p6);
    
    QueueOut create(final Integer p0, final Integer p1, final MessageType p2, final String p3, final Long p4, final Integer p5);
    
    QueueOut create(final Integer p0, final Integer p1, final MessageType p2, final QueueStatus p3, final String p4, final Long p5, final Integer p6, final Date p7);
    
    List<QueueOut> findByOrderByEventDate(final QueueStatus p0, final List<MessageType> p1, final List<Connection> p2);
    
    List<QueueOut> findByIssue(final Issue p0);
    
    List<QueueOut> findByStatus(final QueueStatus p0);
    
    List<QueueOut> findBy(final QueueStatus p0, final MessageType p1);
    
    List<QueueOut> findBy(final QueueStatus p0, final List<MessageType> p1);
    
    List<QueueOut> findBy(final QueueStatus p0, final MessageType p1, final Long p2, final Integer p3);
    
    void updateAll(final List<QueueOut> p0, final QueueStatus p1);
    
    QueueOut update(final Integer p0, final QueueStatus p1);
    
    List<QueueOut> findBy(final QueueStatus p0, final List<MessageType> p1, final List<Connection> p2);
    
    List<QueueOut> findBy(final QueueStatus p0, final MessageType p1, final List<Connection> p2);
    
    void deleteAll(final List<QueueOut> p0);
}
