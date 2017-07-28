// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import java.util.Map;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.rest.api.issue.IssueFields;

public abstract class AbstractIssueFieldsMapper<T> implements IssueFieldMapper<T>
{
    protected static final String REST_FILED_MAPPING_KEY = "key";
    protected static final String REST_FILED_MAPPING_NAME = "name";
    
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final Issue is) {
        final Object issueFieldObjectValue = this.getIssueFieldObjectValue(is);
        if (issueFieldObjectValue != null) {
            return this.getIssueFields(fields, is, issueFieldObjectValue);
        }
        return fields;
    }
    
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final Issue is, final Object value) {
        if (value != null) {
            return this.getIssueFieldsObj(fields, value);
        }
        return fields;
    }
    
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final Object value, final Long projectId) {
        if (value != null) {
            return this.getIssueFieldsObj(fields, value);
        }
        return fields;
    }
    
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final Issue is, final Map mapping) {
        if (mapping.containsKey("parent")) {
            return this.getIssueFields(fields, is, mapping.get("parent").toString());
        }
        return fields;
    }
}
