// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.entity.Comment;
import java.util.List;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.event.issue.IssueEvent;

public interface CommunicationService
{
    void sendTransition(final IssueEvent p0);
    
    boolean sendCreate(final Issue p0, final Integer p1, final ApplicationUser p2);
    
    void send(final IssueEvent p0);
    
    void send(final List<Comment> p0);
    
    void sendWorklog(final IssueEvent p0);
}
