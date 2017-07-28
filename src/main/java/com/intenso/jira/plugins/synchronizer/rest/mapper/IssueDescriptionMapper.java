// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.IssueFields;
import org.apache.commons.lang3.StringEscapeUtils;
import com.atlassian.jira.issue.Issue;

public class IssueDescriptionMapper extends AbstractIssueFieldsMapper<String> implements IssueFieldMapper<String>
{
    @Override
    public String getFieldId() {
        return "description";
    }
    
    @Override
    public String getIssueFieldObjectValue(final Issue is) {
        if (is.getDescription() != null) {
            return StringEscapeUtils.escapeJson(is.getDescription());
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final String value) {
        return fields.description(value);
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.description((value == null) ? "" : value.toString());
    }
}
