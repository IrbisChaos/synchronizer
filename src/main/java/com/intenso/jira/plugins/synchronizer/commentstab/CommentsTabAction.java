// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.commentstab;

import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.CommentService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import java.sql.Timestamp;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;

public class CommentsTabAction extends AbstractIssueAction
{
    private Timestamp timePerformed;
    private IssueTabPanelModuleDescriptor descriptor;
    private Issue issue;
    private CommentService commentService;
    private PluginLicenseManager pluginLicenseManager;
    
    public CommentsTabAction(final IssueTabPanelModuleDescriptor descriptor, final Issue issue, final CommentService commentService, final PluginLicenseManager pluginLicenseManager) {
        super(descriptor);
        this.descriptor = descriptor;
        this.issue = issue;
        this.commentService = commentService;
        this.timePerformed = new Timestamp(Calendar.getInstance().getTimeInMillis());
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    public Date getTimePerformed() {
        return this.timePerformed;
    }
    
    protected void populateVelocityParams(final Map params) {
        final SimpleErrorCollection errorCollection = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (errorCollection.hasAnyErrors()) {
            params.put("licErrors", errorCollection);
        }
        if (this.issue != null) {
            params.put("issueId", this.issue.getId());
        }
    }
    
    public IssueTabPanelModuleDescriptor getDescriptor() {
        return this.descriptor;
    }
    
    public void setDescriptor(final IssueTabPanelModuleDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    public Issue getIssue() {
        return this.issue;
    }
    
    public void setIssue(final Issue issue) {
        this.issue = issue;
    }
    
    public CommentService getCommentService() {
        return this.commentService;
    }
    
    public void setCommentService(final CommentService commentService) {
        this.commentService = commentService;
    }
    
    public void setTimePerformed(final Timestamp timePerformed) {
        this.timePerformed = timePerformed;
    }
}
