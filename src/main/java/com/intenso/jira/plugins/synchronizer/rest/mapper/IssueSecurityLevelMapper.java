// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.issue.Issue;

public class IssueSecurityLevelMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "securitylevel";
    }
    
    @Override
    public Long getIssueFieldObjectValue(final Issue is) {
        if (is.getSecurityLevelId() != null) {
            return is.getSecurityLevelId();
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields.securityLevel(ResourceRef.withId(value));
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.securityLevel(ResourceRef.withId((value != null) ? value.toString() : null));
    }
}
