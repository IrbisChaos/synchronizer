// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action.dialog;

import java.util.Iterator;
import java.util.Collection;
import com.atlassian.jira.workflow.JiraWorkflow;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class WorkflowMappingAction extends JiraWebActionSupport
{
    private static final long serialVersionUID = -5488825767545869881L;
    private final WorkflowManager workflowManager;
    private final WorkflowSyncService workflowSyncService;
    private String workflowMappingDisplayName;
    private String workflow;
    
    public WorkflowMappingAction(final WorkflowManager workflowManager, final WorkflowSyncService workflowSyncService) {
        this.workflowManager = workflowManager;
        this.workflowSyncService = workflowSyncService;
    }
    
    public String doDefault() throws Exception {
        return super.doDefault();
    }
    
    public String doInput() {
        return "input";
    }
    
    public String doExecute() {
        return this.returnCompleteWithInlineRedirect("/secure/admin/WorkflowMappingAction!mapping.jspa?workflowMappingId=" + this.workflowSyncService.getConfigurationId() + "&" + "workflowMappingDisplayName=" + this.workflowMappingDisplayName + "&" + "workflow=" + this.workflow);
    }
    
    protected void doValidation() {
        super.doValidation();
        final Map<String, String> errors = (Map<String, String>)this.getErrors();
        if (this.workflowMappingDisplayName == null || this.workflowMappingDisplayName.isEmpty()) {
            errors.put("workflowMappingDisplayName", this.getI18nHelper().getText("workflowmapping.empty.name"));
        }
        else if (this.workflowSyncService.isValidMappingName(this.workflowMappingDisplayName)) {
            errors.put("workflowMappingDisplayName", this.getI18nHelper().getText("workflowmapping.duplicated.name"));
        }
        if (this.workflow == null || this.workflow.isEmpty()) {
            errors.put("workflow", this.getI18nHelper().getText("workflowmapping.empty.workflow"));
        }
    }
    
    public List<String> getWorkflows() {
        final List<String> result = new ArrayList<String>();
        final List<JiraWorkflow> workflows = new ArrayList<JiraWorkflow>(this.workflowManager.getWorkflows());
        for (final JiraWorkflow w : workflows) {
            result.add(w.getDisplayName());
        }
        return result;
    }
    
    public String getWorkflowMappingDisplayName() {
        return this.workflowMappingDisplayName;
    }
    
    public void setWorkflowMappingDisplayName(final String workflowMappingDisplayName) {
        this.workflowMappingDisplayName = workflowMappingDisplayName;
    }
    
    public String getWorkflow() {
        return this.workflow;
    }
    
    public void setWorkflow(final String workflow) {
        this.workflow = workflow;
    }
}
