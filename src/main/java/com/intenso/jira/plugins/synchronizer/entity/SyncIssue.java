// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.Indexed;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "issueId", "contractId", "remoteIssueId", "remoteIssueKey" })
public interface SyncIssue extends Entity
{
    @Indexed
    Long getIssueId();
    
    @Indexed
    Integer getContractId();
    
    Long getRemoteIssueId();
    
    String getRemoteIssueKey();
    
    void setIssueId(final Long p0);
    
    void setContractId(final Integer p0);
    
    void setRemoteIssueId(final Long p0);
    
    void setRemoteIssueKey(final String p0);
}
