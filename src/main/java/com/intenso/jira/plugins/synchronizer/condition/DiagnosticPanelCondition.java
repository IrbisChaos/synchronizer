// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.condition;

import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;

public class DiagnosticPanelCondition extends AbstractWebCondition
{
    public boolean shouldDisplay(final ApplicationUser user, final JiraHelper jiraHelper) {
        final SynchronizerConfig config = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).getConfig();
        if (config != null && config.getGenDiagnosticPanel() != null && config.getGenDiagnosticPanel().equals(1)) {
            return false;
        }
        final Issue issue = jiraHelper.getContextParams().get("issue");
        if (issue == null) {
            return false;
        }
        final Long projectId = issue.getProjectId();
        final ContractService contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
        if (contractService == null) {
            return false;
        }
        final List<Contract> findByContext = contractService.findByContext(projectId, issue.getIssueTypeId());
        return findByContext.size() > 0;
    }
}
