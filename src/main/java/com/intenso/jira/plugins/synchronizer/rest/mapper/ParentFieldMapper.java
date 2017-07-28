// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.issue.Issue;

public class ParentFieldMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "parent";
    }
    
    @Override
    public Issue getIssueFieldObjectValue(final Issue is) {
        if (is.getParentObject() != null) {
            return is.getParentObject();
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields.parent(ResourceRef.withKey(value));
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.parent(ResourceRef.withKey((value != null) ? value.toString() : null));
    }
}
