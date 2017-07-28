// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.user.ApplicationUser;
import java.util.Date;
import com.atlassian.jira.util.BuildUtilsInfo;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.util.UserManager;
import com.intenso.jira.plugins.synchronizer.entity.CommentType;
import java.text.SimpleDateFormat;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import java.sql.Timestamp;
import org.codehaus.jackson.annotate.JsonProperty;

public class CommentT
{
    @JsonProperty
    private String author;
    @JsonProperty
    private String authorDisplayName;
    @JsonProperty
    private Long authorAvatar;
    @JsonProperty
    private String avatarUrl;
    @JsonProperty
    private Timestamp dateInternal;
    @JsonProperty
    private String dateInternalString;
    @JsonProperty
    private Timestamp dateExternal;
    @JsonProperty
    private String dateExternalString;
    @JsonProperty
    private Integer id;
    @JsonProperty
    private Long issueId;
    @JsonProperty
    private Integer commentType;
    @JsonProperty
    private Integer syncIssueId;
    @JsonProperty
    private String comment;
    @JsonProperty
    private Boolean allowRemove;
    @JsonProperty
    private Integer contractId;
    
    public CommentT() {
    }
    
    public CommentT(final Comment c, final Boolean allowRemove) {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy hh:mm aaa");
        this.setAllowRemove((allowRemove == null) ? Boolean.FALSE : allowRemove);
        this.setAuthor(c.getAuthor());
        if (c.getCommentType() != null && c.getCommentType().equals(CommentType.INTERNAL.ordinal()) && this.author != null) {
            final UserManager um = (UserManager)ComponentAccessor.getComponent((Class)UserManager.class);
            final ApplicationUser au = um.getUserByName(c.getAuthor());
            if (au != null) {
                this.authorDisplayName = au.getDisplayName();
            }
            final AvatarService avatarService = ComponentAccessor.getAvatarService();
            final ApplicationUser loggedIn = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
            if (au != null) {
                final Avatar avatar = avatarService.getAvatar(loggedIn, au.getKey());
                this.authorAvatar = avatar.getId();
                this.avatarUrl = avatarService.getAvatarURL(loggedIn, au.getKey()).toString();
                if (((BuildUtilsInfo)ComponentAccessor.getComponent((Class)BuildUtilsInfo.class)).getVersionNumbers()[0] == 5) {
                    this.avatarUrl += "&size=small";
                }
                else {
                    if (this.avatarUrl != null && this.avatarUrl.contains("?")) {
                        this.avatarUrl += "&";
                    }
                    else if (this.avatarUrl != null) {
                        this.avatarUrl += "?";
                    }
                    this.avatarUrl += "d=mm&amp;s=16";
                }
            }
        }
        this.setDateInternal(c.getDateInternal());
        if (c.getDateInternal() != null) {
            final Date internalDate = new Date(c.getDateInternal().getTime());
            this.setDateInternalString(sdf.format(internalDate));
        }
        this.setDateExternal(c.getDateExternal());
        if (c.getDateExternal() != null) {
            final Date externalDate = new Date(c.getDateExternal().getTime());
            this.setDateExternalString(sdf.format(externalDate));
        }
        this.setId(c.getID());
        this.setIssueId(c.getIssueId());
        this.setCommentType(c.getCommentType());
        this.setSyncIssueId(c.getSyncIssueId());
        this.setComment(c.getComment());
        this.setContractId(c.getContractId());
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(final String author) {
        this.author = author;
    }
    
    public Timestamp getDateInternal() {
        return this.dateInternal;
    }
    
    public void setDateInternal(final Timestamp dateInternal) {
        this.dateInternal = dateInternal;
    }
    
    public Timestamp getDateExternal() {
        return this.dateExternal;
    }
    
    public void setDateExternal(final Timestamp dateExternal) {
        this.dateExternal = dateExternal;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Long getIssueId() {
        return this.issueId;
    }
    
    public void setIssueId(final Long issueId) {
        this.issueId = issueId;
    }
    
    public Integer getCommentType() {
        return this.commentType;
    }
    
    public void setCommentType(final Integer commentType) {
        this.commentType = commentType;
    }
    
    public Integer getSyncIssueId() {
        return this.syncIssueId;
    }
    
    public void setSyncIssueId(final Integer syncIssueId) {
        this.syncIssueId = syncIssueId;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public String getAuthorDisplayName() {
        return this.authorDisplayName;
    }
    
    public void setAuthorDisplayName(final String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }
    
    public String getDateInternalString() {
        return this.dateInternalString;
    }
    
    public void setDateInternalString(final String dateInternalString) {
        this.dateInternalString = dateInternalString;
    }
    
    public String getDateExternalString() {
        return this.dateExternalString;
    }
    
    public void setDateExternalString(final String dateExternalString) {
        this.dateExternalString = dateExternalString;
    }
    
    public Long getAuthorAvatar() {
        return this.authorAvatar;
    }
    
    public void setAuthorAvatar(final Long authorAvatar) {
        this.authorAvatar = authorAvatar;
    }
    
    public String getAvatarUrl() {
        return this.avatarUrl;
    }
    
    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public Boolean getAllowRemove() {
        return this.allowRemove;
    }
    
    public void setAllowRemove(final Boolean allowRemove) {
        this.allowRemove = allowRemove;
    }
    
    public Integer getContractId() {
        return this.contractId;
    }
    
    public void setContractId(final Integer contractId) {
        this.contractId = contractId;
    }
    
    public String getBuildInCommentBody(final Connection connection, final Contract contract, final String remoteIssueKey) {
        final StringBuilder body = new StringBuilder("");
        body.append("*");
        body.append(remoteIssueKey);
        body.append("* ");
        body.append(contract.getContractName());
        body.append(" (");
        body.append(connection.getConnectionName());
        body.append(") ");
        body.append(this.getAuthorDisplayName());
        body.append(" added comment - ");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(this.dateInternal.getTime());
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy hh:mm aaa", Locale.US);
        final String acknowledgeDate = sdf.format(calendar.getTime());
        body.append(acknowledgeDate + "\\\\");
        body.append(this.comment);
        return body.toString();
    }
}
