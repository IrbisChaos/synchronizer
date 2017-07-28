// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.StringLength;
import net.java.ao.schema.Indexed;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "syncIssue", "localAttachmentId", "remoteAttachmentId" })
public interface SyncAttachment extends Entity
{
    @Indexed
    Integer getSyncIssue();
    
    @Indexed
    String getLocalAttachmentId();
    
    @Indexed
    Long getRemoteAttachmentId();
    
    @StringLength(-1)
    String getResponseMessage();
    
    void setSyncIssue(final Integer p0);
    
    void setLocalAttachmentId(final Long p0);
    
    void setRemoteAttachmentId(final Long p0);
    
    void setResponseMessage(final String p0);
}
