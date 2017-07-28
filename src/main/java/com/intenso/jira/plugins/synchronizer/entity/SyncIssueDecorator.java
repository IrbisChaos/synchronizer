// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

public class SyncIssueDecorator
{
    private SyncIssue syncIssue;
    
    public SyncIssueDecorator(final SyncIssue syncIssue) {
        this.syncIssue = syncIssue;
    }
    
    public QueueStatus determineQueueOutStatus() {
        if (this.syncIssue != null && this.syncIssue.getRemoteIssueId() == null) {
            return QueueStatus.PROCESSING;
        }
        return QueueStatus.NEW;
    }
}
