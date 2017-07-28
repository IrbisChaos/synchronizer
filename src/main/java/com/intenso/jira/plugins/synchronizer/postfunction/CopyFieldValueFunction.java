// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.postfunction;

import com.opensymphony.workflow.WorkflowException;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import com.atlassian.jira.component.ComponentAccessor;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Map;
import org.apache.log4j.Logger;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;

public class CopyFieldValueFunction extends AbstractJiraFunctionProvider
{
    private Logger logger;
    
    public CopyFieldValueFunction() {
        this.logger = Logger.getLogger((Class)this.getClass());
    }
    
    public void execute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
        final MutableIssue issue = this.getIssue(transientVars);
        final ApplicationUser caller = this.getCaller(transientVars, args);
        final FieldManager fieldManager = ComponentAccessor.getFieldManager();
        final CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        final String from = (args.containsKey("source") && args.get("source") != null) ? args.get("source").toString() : null;
        final String to = (args.containsKey("destination") && args.get("destination") != null) ? args.get("destination").toString() : null;
        final CustomField fromCF = FieldMappingUtils.getCustomFieldByNameAndContext(from, (Issue)issue);
        final CustomField toCF = (fieldManager.getCustomField(to) == null) ? customFieldManager.getCustomFieldObjectByName(to) : fieldManager.getCustomField(to);
        if (toCF != null) {
            if (from.equals("assignee")) {
                final ApplicationUser user = issue.getAssignee();
                if (user != null) {
                    issue.setCustomFieldValue(toCF, (Object)user.getDisplayName());
                }
            }
            else if (from.equals("reporter")) {
                final ApplicationUser user = issue.getReporterUser();
                if (user != null) {
                    issue.setCustomFieldValue(toCF, (Object)user.getDisplayName());
                }
            }
            else if (fromCF != null) {
                issue.setCustomFieldValue(toCF, fromCF.getValue((Issue)issue));
            }
            final IssueManager im = ComponentAccessor.getIssueManager();
            im.updateIssue(caller, issue, EventDispatchOption.ISSUE_UPDATED, (boolean)Boolean.FALSE);
        }
    }
}
