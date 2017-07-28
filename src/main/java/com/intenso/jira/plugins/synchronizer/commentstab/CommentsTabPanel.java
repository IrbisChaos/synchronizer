// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.commentstab;

import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.ContractStatus;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import java.util.ArrayList;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import java.util.List;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.Issue;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.CommentService;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;

public class CommentsTabPanel extends AbstractIssueTabPanel
{
    protected IssueTabPanelModuleDescriptor descriptor;
    private CommentService commentService;
    private ContractService contractService;
    private PluginLicenseManager pluginLicenseManager;
    
    public CommentsTabPanel(final CommentService commentService, final ContractService contractService, final PluginLicenseManager pluginLicenseManager) {
        this.commentService = commentService;
        this.contractService = contractService;
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    public void init(final IssueTabPanelModuleDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    public List<IssueAction> getActions(final Issue issue, final ApplicationUser user) {
        final List<IssueAction> actionList = new ArrayList<IssueAction>();
        actionList.add((IssueAction)new CommentsTabAction(this.descriptor, issue, this.commentService, this.pluginLicenseManager));
        return actionList;
    }
    
    public boolean showPanel(final Issue issue, final ApplicationUser user) {
        final Boolean canSeeTab = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).canExternalComments();
        if (canSeeTab == Boolean.FALSE) {
            return Boolean.FALSE;
        }
        final List<Contract> contracts = this.contractService.findByContextAndStatus(issue.getProjectObject().getId(), issue.getIssueTypeObject().getId(), ContractStatus.ENABLED);
        if (contracts != null) {
            for (final Contract c : contracts) {
                if (c.getComments() != null && c.getComments().equals(1)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
    
    public CommentService getCommentService() {
        return this.commentService;
    }
    
    public void setCommentService(final CommentService commentService) {
        this.commentService = commentService;
    }
    
    public IssueTabPanelModuleDescriptor getDescriptor() {
        return this.descriptor;
    }
    
    public void setDescriptor(final IssueTabPanelModuleDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    public ContractService getContractService() {
        return this.contractService;
    }
    
    public void setContractService(final ContractService contractService) {
        this.contractService = contractService;
    }
}
