// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.entity.SyncAttachment;

public interface SynchronizedAttachmentService extends GenericService<SyncAttachment>
{
    SyncAttachment save(final Integer p0, final String p1, final Long p2, final String p3);
}
