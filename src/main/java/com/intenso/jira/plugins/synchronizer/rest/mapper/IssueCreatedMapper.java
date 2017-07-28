// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.issue.Issue;

public class IssueCreatedMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "created";
    }
    
    @Override
    public Object getIssueFieldObjectValue(final Issue is) {
        return is.getCreated();
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields;
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields;
    }
}
