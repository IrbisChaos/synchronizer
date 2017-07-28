// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutResponseDTO;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import java.util.List;

public interface SynchronizedIssuesService
{
    List<SyncIssue> findByContract(final Integer p0);
    
    List<SyncIssue> findByContract(final Integer p0, final Long p1);
    
    List<SyncIssue> findByIssue(final Long p0);
    
    SyncIssue findByContractSingleResult(final Integer p0, final Long p1);
    
    SyncIssue findByContractAndRemoteIssueIdSingleResult(final Integer p0, final Long p1);
    
    SyncIssue save(final Integer p0, final Long p1, final Long p2, final String p3);
    
    SyncIssue update(final QueueOutResponseDTO p0);
    
    SyncIssue update(final Integer p0, final Long p1, final Long p2, final String p3);
    
    void delete(final SyncIssue p0);
    
    void delete(final Integer p0, final Long p1);
}
