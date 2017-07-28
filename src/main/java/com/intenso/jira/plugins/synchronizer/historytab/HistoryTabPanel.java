// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.historytab;

import java.util.ArrayList;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import java.util.List;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;

public class HistoryTabPanel extends AbstractIssueTabPanel
{
    protected IssueTabPanelModuleDescriptor descriptor;
    private ContractService contractService;
    private PluginLicenseManager pluginLicenseManager;
    private QueueInService queueInService;
    private QueueOutService queueOutService;
    
    public HistoryTabPanel(final ContractService contractService, final PluginLicenseManager pluginLicenseManager, final QueueInService queueInService, final QueueOutService queueOutService) {
        this.contractService = contractService;
        this.pluginLicenseManager = pluginLicenseManager;
        this.queueInService = queueInService;
        this.queueOutService = queueOutService;
    }
    
    public void init(final IssueTabPanelModuleDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    public List<IssueAction> getActions(final Issue issue, final ApplicationUser user) {
        final List<IssueAction> actionList = new ArrayList<IssueAction>();
        actionList.add((IssueAction)new HistoryTabAction(this.descriptor, issue, this.pluginLicenseManager, this.queueInService, this.queueOutService, this.contractService));
        return actionList;
    }
    
    public boolean showPanel(final Issue issue, final ApplicationUser user) {
        return Boolean.TRUE;
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
    
    public QueueInService getQueueInService() {
        return this.queueInService;
    }
    
    public void setQueueInService(final QueueInService queueInService) {
        this.queueInService = queueInService;
    }
    
    public QueueOutService getQueueOutService() {
        return this.queueOutService;
    }
    
    public void setQueueOutService(final QueueOutService queueOutService) {
        this.queueOutService = queueOutService;
    }
}
