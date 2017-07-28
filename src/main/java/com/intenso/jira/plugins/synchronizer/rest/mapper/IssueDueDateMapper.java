// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.IssueFields;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.atlassian.jira.issue.Issue;

public class IssueDueDateMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "duedate";
    }
    
    @Override
    public String getIssueFieldObjectValue(final Issue is) {
        if (is.getDueDate() != null) {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date(is.getDueDate().getTime()));
        }
        return "";
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields.dueDate(value);
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.dueDate((value == null) ? null : value.toString());
    }
}
