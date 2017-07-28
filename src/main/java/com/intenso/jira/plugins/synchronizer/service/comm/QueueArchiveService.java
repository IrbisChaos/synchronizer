// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.QueueArchive;
import java.util.List;

public interface QueueArchiveService
{
    List<QueueArchive> getAll();
    
    QueueArchive create(final int p0, final Integer p1, final Integer p2, final Date p3, final Long p4, final String p5, final Integer p6, final Integer p7, final Integer p8, final Date p9);
    
    QueueArchive create(final int p0, final Integer p1, final Integer p2, final Date p3, final String p4, final Integer p5, final Integer p6, final Integer p7, final Date p8);
    
    void archiveAllFromQueueOut();
    
    void archiveAllFromQueueIn();
    
    void purge();
}
