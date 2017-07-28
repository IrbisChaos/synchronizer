// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.Issue;

public class IssueTypeMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "issuetype";
    }
    
    @Override
    public IssueType getIssueFieldObjectValue(final Issue is) {
        if (is.getIssueTypeObject() != null) {
            return is.getIssueTypeObject();
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields.issueType(ResourceRef.withId(value));
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.issueType(ResourceRef.withId((value != null) ? value.toString() : null));
    }
}
