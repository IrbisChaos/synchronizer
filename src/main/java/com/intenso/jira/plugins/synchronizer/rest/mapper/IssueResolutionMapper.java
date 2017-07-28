// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.issue.Issue;

public class IssueResolutionMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "resolution";
    }
    
    @Override
    public Resolution getIssueFieldObjectValue(final Issue is) {
        if (is.getResolutionObject() != null) {
            return is.getResolutionObject();
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields.resolution(ResourceRef.withId(value));
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.resolution(ResourceRef.withId((value != null) ? value.toString() : null));
    }
}
