// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.issue.Issue;

public class IssueAssigneeMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "assignee";
    }
    
    @Override
    public String[] getIssueFieldObjectValue(final Issue is) {
        if (is.getAssignee() != null) {
            return new String[] { is.getAssignee().getName(), is.getAssignee().getDisplayName(), is.getAssignee().getEmailAddress() };
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields.assignee(ResourceRef.withName(value));
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.assignee(ResourceRef.withName((value != null) ? value.toString() : ""));
    }
}
