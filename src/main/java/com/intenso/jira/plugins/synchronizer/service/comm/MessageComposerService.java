// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.atlassian.jira.issue.worklog.Worklog;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.atlassian.jira.event.issue.IssueEvent;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import java.util.List;

public interface MessageComposerService
{
    String buildJSONTransition(final String p0);
    
    String buildJSONTransition(final String p0, final String p1);
    
    String buildJSON(final IssueIntDTO p0, final List<ContractFieldMappingEntry> p1);
    
    String buildInternalJSON(final Contract p0, final IssueEvent p1, final MessageType p2, final List<ContractChangeItem> p3, final SyncIssue p4);
    
    String buildInternalJSON(final Contract p0, final Issue p1, final MessageType p2, final List<ContractChangeItem> p3, final SyncIssue p4);
    
    String buildInternalJSON(final Contract p0, final Comment p1, final MessageType p2, final List<SyncIssue> p3);
    
    String buildResponseInternalJSON(final Contract p0, final JIRAResponse p1, final Integer p2, final Integer p3);
    
    String buildResponseInternalJSON(final Contract p0, final JIRAResponse p1, final Comment p2);
    
    JIRAResponse parseJIRAResponse(final Response p0);
    
    String toJSONString(final Object p0);
    
    IssueIntDTO toIssueIntDTO(final String p0);
    
    List<AttachmentDTO> buildAttachments(final Contract p0, final QueueIn p1);
    
    List<JIRAAttachmentResponse> parse(final Response p0);
    
    String buildInternalJSON(final Contract p0, final Worklog p1, final Issue p2, final MessageType p3, final SyncIssue p4);
}
