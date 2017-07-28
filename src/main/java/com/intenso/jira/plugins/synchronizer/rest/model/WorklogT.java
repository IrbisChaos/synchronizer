// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import com.atlassian.jira.security.roles.ProjectRole;
import org.apache.commons.lang.StringUtils;
import com.atlassian.jira.issue.fields.rest.json.UserBeanFactory;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.util.UserManager;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.jira.rest.Dates;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.worklog.Worklog;
import java.util.Date;
import com.atlassian.jira.issue.fields.rest.json.beans.VisibilityJsonBean;
import com.atlassian.jira.issue.fields.rest.json.beans.UserJsonBean;
import java.net.URI;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorklogT
{
    private URI self;
    private UserJsonBean author;
    private UserJsonBean updateAuthor;
    private String comment;
    private String created;
    private String updated;
    private VisibilityJsonBean visibility;
    private String started;
    private String timeSpent;
    private Long timeSpentSeconds;
    private String id;
    private Date date;
    
    public WorklogT() {
    }
    
    public WorklogT(final Worklog worklog, final ApplicationUser loggedInUser) {
        this.id = Long.toString(worklog.getId());
        this.timeSpentSeconds = worklog.getTimeSpent();
        this.created = Dates.asTimeString(worklog.getCreated());
        this.date = worklog.getUpdated();
        this.updated = Dates.asTimeString(worklog.getUpdated());
        this.started = Dates.asTimeString(worklog.getStartDate());
        this.self = URI.create(this.worklogSelf(worklog));
        this.author = this.getUserBean(worklog.getAuthorKey(), loggedInUser);
        this.updateAuthor = this.getUserBean(worklog.getUpdateAuthorKey(), loggedInUser);
        this.visibility = this.getVisibilityBean(worklog);
        this.comment = worklog.getComment();
    }
    
    public void addMetaDataToComment(final Connection connection, final Contract contract, final String remoteIssueKey) {
        final StringBuilder body = new StringBuilder("");
        if (remoteIssueKey != null) {
            body.append("*");
            body.append(remoteIssueKey);
            body.append("* ");
        }
        if (contract != null) {
            body.append(contract.getContractName());
        }
        if (connection != null) {
            body.append(" (");
            body.append(connection.getConnectionName());
            body.append(") ");
        }
        if (this.author != null) {
            body.append(this.author.getDisplayName());
            body.append(" logged work - ");
        }
        if (this.date != null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            calendar.setTimeInMillis(this.date.getTime());
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy hh:mm aaa", Locale.US);
            final String acknowledgeDate = sdf.format(calendar.getTime());
            body.append(acknowledgeDate + "\\\\");
        }
        if (this.comment != null) {
            body.append(this.comment);
        }
        this.comment = body.toString();
    }
    
    private String worklogSelf(final Worklog worklog) {
        return "issue/" + worklog.getIssue().getId().toString() + "/worklog/" + worklog.getId().toString();
    }
    
    private UserJsonBean getUserBean(final String userKey, final ApplicationUser loggedInUser) {
        final UserManager userManager = (UserManager)ComponentAccessor.getComponent((Class)UserManager.class);
        final ApplicationUser user = userManager.getUserByKey(userKey);
        if (user != null) {
            final UserBeanFactory userBeanFactory = (UserBeanFactory)ComponentAccessor.getComponent((Class)UserBeanFactory.class);
            return userBeanFactory.createBean(user, loggedInUser);
        }
        if (StringUtils.isNotBlank(userKey)) {
            final UserJsonBean userJsonBean = new UserJsonBean();
            userJsonBean.setName(userKey);
            return userJsonBean;
        }
        return null;
    }
    
    private VisibilityJsonBean getVisibilityBean(final Worklog worklog) {
        final String groupLevel = worklog.getGroupLevel();
        final ProjectRole roleLevel = worklog.getRoleLevel();
        if (groupLevel != null) {
            return new VisibilityJsonBean(VisibilityJsonBean.VisibilityType.group, groupLevel);
        }
        if (roleLevel != null) {
            return new VisibilityJsonBean(VisibilityJsonBean.VisibilityType.role, roleLevel.getName());
        }
        return null;
    }
    
    public URI getSelf() {
        return this.self;
    }
    
    public void setSelf(final URI self) {
        this.self = self;
    }
    
    public UserJsonBean getAuthor() {
        return this.author;
    }
    
    public void setAuthor(final UserJsonBean author) {
        this.author = author;
    }
    
    public UserJsonBean getUpdateAuthor() {
        return this.updateAuthor;
    }
    
    public void setUpdateAuthor(final UserJsonBean updateAuthor) {
        this.updateAuthor = updateAuthor;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public String getCreated() {
        return this.created;
    }
    
    public void setCreated(final String created) {
        this.created = created;
    }
    
    public String getUpdated() {
        return this.updated;
    }
    
    public void setUpdated(final String updated) {
        this.updated = updated;
    }
    
    public VisibilityJsonBean getVisibility() {
        return this.visibility;
    }
    
    public void setVisibility(final VisibilityJsonBean visibility) {
        this.visibility = visibility;
    }
    
    public String getStarted() {
        return this.started;
    }
    
    public void setStarted(final String started) {
        this.started = started;
    }
    
    public String getTimeSpent() {
        return this.timeSpent;
    }
    
    public void setTimeSpent(final String timeSpent) {
        this.timeSpent = timeSpent;
    }
    
    public Long getTimeSpentSeconds() {
        return this.timeSpentSeconds;
    }
    
    public void setTimeSpentSeconds(final Long timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
}
