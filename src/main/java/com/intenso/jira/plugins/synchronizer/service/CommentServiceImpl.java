// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Iterator;
import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.CommentType;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.ArrayList;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.Comment;

public class CommentServiceImpl extends GenericServiceImpl<Comment> implements CommentService
{
    public static final String COL_ISSUE_ID = "ISSUE_ID";
    public static final String COL_AUTHOR = "AUTHOR";
    public static final String COL_COMMENT_TYPE = "COMMENT_TYPE";
    public static final String COL_SYNC_ISSUE_ID = "SYNC_ISSUE_ID";
    public static final String COL_DATE_INTERNAL = "DATE_INTERNAL";
    public static final String COL_DATE_EXTERNAL = "DATE_EXTERNAL";
    public static final String COL_COMMENT = "COMMENT";
    public static final String COL_REMOTE_COMMENT_ID = "REMOTE_COMMENT_ID";
    public static final String COL_CONTRACT_ID = "CONTRACT_ID";
    public static final String COL_BUILD_IN_COMMENT_ID = "BUILD_IN_COMMENT_ID";
    
    public CommentServiceImpl(final ActiveObjects dao) {
        super(dao, Comment.class);
    }
    
    @Override
    public List<Comment> findByIssue(final Long issueId) {
        final Comment[] comments = (Comment[])this.getDao().find((Class)Comment.class, Query.select().where("ISSUE_ID = ?", new Object[] { issueId }));
        return (comments != null) ? Arrays.asList(comments) : new ArrayList<Comment>();
    }
    
    @Override
    public List<Comment> findExternalByIssue(final Long issueId) {
        final Comment[] comments = (Comment[])this.getDao().find((Class)Comment.class, Query.select().where("ISSUE_ID = ? AND BUILD_IN_COMMENT_ID IS NULL", new Object[] { issueId }));
        return (comments != null) ? Arrays.asList(comments) : new ArrayList<Comment>();
    }
    
    @Override
    public Integer countComments(final Long issueId) {
        final Integer count = this.getDao().count((Class)Comment.class, Query.select().where("ISSUE_ID = ?", new Object[] { issueId }));
        return count;
    }
    
    @Override
    public List<Comment> createInternalComment(final String author, final String comment, final Long issueId, final Long buildInCommentId, final List<Contract> contracts) {
        final List<Comment> comments = new ArrayList<Comment>();
        for (final Contract c : contracts) {
            if (buildInCommentId != null && this.getDao().count((Class)Comment.class, Query.select().where("CONTRACT_ID = ? AND BUILD_IN_COMMENT_ID = ? ", new Object[] { c.getID(), buildInCommentId })) > 0) {
                continue;
            }
            final SyncIssue[] remoteIssues = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("ISSUE_ID = ? AND CONTRACT_ID= ?", new Object[] { issueId, c.getID() }));
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("ISSUE_ID", issueId);
            params.put("AUTHOR", author);
            params.put("COMMENT_TYPE", CommentType.INTERNAL.ordinal());
            params.put("DATE_INTERNAL", new Timestamp(new Date().getTime()));
            params.put("COMMENT", comment);
            params.put("CONTRACT_ID", c.getID());
            params.put("BUILD_IN_COMMENT_ID", buildInCommentId);
            if (remoteIssues != null && remoteIssues.length > 0) {
                params.put("SYNC_ISSUE_ID", remoteIssues[0].getID());
            }
            comments.add((Comment)this.getDao().create((Class)Comment.class, (Map)params));
        }
        return comments;
    }
    
    @Override
    public List<Comment> createInternalComment(final String author, final String comment, final Long issueId, final Long buildInCommentId, final List<Contract> contracts, final Date commentDate) {
        final List<Comment> comments = new ArrayList<Comment>();
        for (final Contract c : contracts) {
            if (buildInCommentId != null && this.getDao().count((Class)Comment.class, Query.select().where("CONTRACT_ID = ? AND BUILD_IN_COMMENT_ID = ? ", new Object[] { c.getID(), buildInCommentId })) > 0) {
                continue;
            }
            final SyncIssue[] remoteIssues = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("ISSUE_ID = ? AND CONTRACT_ID= ?", new Object[] { issueId, c.getID() }));
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("ISSUE_ID", issueId);
            params.put("AUTHOR", author);
            params.put("COMMENT_TYPE", CommentType.INTERNAL.ordinal());
            params.put("DATE_INTERNAL", new Timestamp(commentDate.getTime()));
            params.put("COMMENT", comment);
            params.put("CONTRACT_ID", c.getID());
            params.put("BUILD_IN_COMMENT_ID", buildInCommentId);
            if (remoteIssues != null && remoteIssues.length > 0) {
                params.put("SYNC_ISSUE_ID", remoteIssues[0].getID());
            }
            comments.add((Comment)this.getDao().create((Class)Comment.class, (Map)params));
        }
        return comments;
    }
    
    @Override
    public List<Comment> createExternalComment(final String authorDisplayName, final String comment, final Long issueId, final Timestamp dateInternal, final Integer remoteCommentId, final Long buildInCommentId, final List<Contract> contracts) {
        final List<Comment> comments = new ArrayList<Comment>();
        for (final Contract c : contracts) {
            final SyncIssue[] remoteIssues = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("ISSUE_ID = ? AND CONTRACT_ID= ?", new Object[] { issueId, c.getID() }));
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("ISSUE_ID", issueId);
            params.put("AUTHOR", authorDisplayName);
            params.put("COMMENT_TYPE", CommentType.EXTERNAL.ordinal());
            params.put("DATE_EXTERNAL", dateInternal);
            params.put("COMMENT", comment);
            params.put("DATE_INTERNAL", new Timestamp(new Date().getTime()));
            params.put("REMOTE_COMMENT_ID", remoteCommentId);
            params.put("CONTRACT_ID", c.getID());
            params.put("BUILD_IN_COMMENT_ID", buildInCommentId);
            if (remoteIssues != null && remoteIssues.length > 0) {
                params.put("SYNC_ISSUE_ID", remoteIssues[0].getID());
            }
            comments.add((Comment)this.getDao().create((Class)Comment.class, (Map)params));
        }
        return comments;
    }
    
    @Override
    public Comment createExternalComment(final String authorDisplayName, final String comment, final Long issueId, final Timestamp dateInternal, final Long buildInCommentId, final Integer remoteCommentId, final Integer contractId) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("ISSUE_ID", issueId);
        params.put("AUTHOR", authorDisplayName);
        params.put("COMMENT_TYPE", CommentType.EXTERNAL.ordinal());
        params.put("DATE_EXTERNAL", dateInternal);
        params.put("COMMENT", comment);
        params.put("DATE_INTERNAL", new Timestamp(new Date().getTime()));
        params.put("REMOTE_COMMENT_ID", remoteCommentId);
        params.put("CONTRACT_ID", contractId);
        params.put("BUILD_IN_COMMENT_ID", buildInCommentId);
        final SyncIssue[] remoteIssues = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("ISSUE_ID = ? AND CONTRACT_ID= ?", new Object[] { issueId, contractId }));
        if (remoteIssues != null && remoteIssues.length > 0) {
            params.put("SYNC_ISSUE_ID", remoteIssues[0].getID());
        }
        return (Comment)this.getDao().create((Class)Comment.class, (Map)params);
    }
    
    @Override
    public List<Comment> findExternalCommentsByIssue(final Long issueId) {
        final Comment[] comments = (Comment[])this.getDao().find((Class)Comment.class, Query.select().where("ISSUE_ID = ? AND BUILD_IN_COMMENT_ID IS NULL ", new Object[] { issueId }));
        return (comments == null) ? new ArrayList<Comment>() : Arrays.asList(comments);
    }
    
    @Override
    public List<Comment> findBuildInCommentsByIssue(final Long issueId) {
        final Comment[] comments = (Comment[])this.getDao().find((Class)Comment.class, Query.select().where("ISSUE_ID = ? AND BUILD_IN_COMMENT_ID IS NOT NULL ", new Object[] { issueId }));
        return (comments == null) ? new ArrayList<Comment>() : Arrays.asList(comments);
    }
}
