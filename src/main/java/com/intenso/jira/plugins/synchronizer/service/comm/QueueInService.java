// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.service.GenericService;

public interface QueueInService extends GenericService<QueueIn>
{
    List<QueueIn> getAllResponses();
    
    QueueIn create(final Integer p0, final Integer p1, final MessageType p2, final String p3, final Integer p4);
    
    List<QueueIn> getAllBy(final MessageType p0);
    
    List<QueueIn> getAllBy(final MessageType p0, final QueueStatus p1);
    
    List<QueueIn> getAllByStatusAndMsgTypeNull(final QueueStatus p0);
    
    List<QueueIn> findByStatus(final QueueStatus p0);
    
    List<QueueIn> findByIssue(final Issue p0);
    
    void updateAll(final List<QueueIn> p0, final QueueStatus p1);
    
    void deleteAll(final List<QueueIn> p0);
}
