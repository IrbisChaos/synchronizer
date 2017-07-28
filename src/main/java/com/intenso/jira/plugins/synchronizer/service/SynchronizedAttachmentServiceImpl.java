// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Map;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.SyncAttachment;

public class SynchronizedAttachmentServiceImpl extends GenericServiceImpl<SyncAttachment> implements SynchronizedAttachmentService
{
    public static final String COL_SYNC_ISSUE_ID = "SYNC_ISSUE";
    public static final String COL_LOCAL_ATTACHMENT_ID = "LOCAL_ATTACHMENT_ID";
    public static final String COL_REMOTE_ATTACHMENT_ID = "REMOTE_ATTACHMENT_ID";
    public static final String COL_RESPONSE_MESSAGE = "RESPONSE_MESSAGE";
    
    public SynchronizedAttachmentServiceImpl(final ActiveObjects dao) {
        super(dao, SyncAttachment.class);
    }
    
    @Override
    public SyncAttachment save(final Integer syncIssueId, final String localAttachmentId, final Long remoteAttachmentId, final String attachmentResponse) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("SYNC_ISSUE", syncIssueId);
        params.put("LOCAL_ATTACHMENT_ID", localAttachmentId);
        params.put("REMOTE_ATTACHMENT_ID", remoteAttachmentId);
        params.put("RESPONSE_MESSAGE", attachmentResponse);
        return (SyncAttachment)this.getDao().create((Class)SyncAttachment.class, (Map)params);
    }
}
