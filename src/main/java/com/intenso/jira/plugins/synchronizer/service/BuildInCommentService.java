// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.jira.issue.comments.Comment;
import java.util.List;

public interface BuildInCommentService
{
    List<Comment> getAllBuildInComments(final Long p0);
    
    Comment createComment(final Long p0, final Contract p1, final CommentT p2);
    
    Comment updateBuildComment(final Long p0, final String p1);
}
