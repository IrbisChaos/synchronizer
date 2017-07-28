// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.sql.Timestamp;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Comment;

public interface CommentService extends GenericService<Comment>
{
    List<Comment> findByIssue(final Long p0);
    
    Integer countComments(final Long p0);
    
    List<Comment> createInternalComment(final String p0, final String p1, final Long p2, final Long p3, final List<Contract> p4);
    
    List<Comment> createInternalComment(final String p0, final String p1, final Long p2, final Long p3, final List<Contract> p4, final Date p5);
    
    List<Comment> createExternalComment(final String p0, final String p1, final Long p2, final Timestamp p3, final Integer p4, final Long p5, final List<Contract> p6);
    
    Comment createExternalComment(final String p0, final String p1, final Long p2, final Timestamp p3, final Long p4, final Integer p5, final Integer p6);
    
    List<Comment> findExternalCommentsByIssue(final Long p0);
    
    List<Comment> findBuildInCommentsByIssue(final Long p0);
    
    List<Comment> findExternalByIssue(final Long p0);
}
