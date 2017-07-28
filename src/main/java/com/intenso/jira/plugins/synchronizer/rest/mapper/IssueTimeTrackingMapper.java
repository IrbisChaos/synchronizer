// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.rest.api.issue.TimeTracking;

public class IssueTimeTrackingMapper extends AbstractIssueFieldsMapper<TimeTracking> implements IssueFieldMapper<TimeTracking>
{
    @Override
    public String getFieldId() {
        return "timetracking";
    }
    
    @Override
    public TimeTracking getIssueFieldObjectValue(final Issue is) {
        if (is.getOriginalEstimate() != null || is.getEstimate() != null) {
            final String oe = (is.getOriginalEstimate() != null) ? is.getOriginalEstimate().toString() : "0";
            final String re = (is.getEstimate() != null) ? is.getEstimate().toString() : "0";
            return new TimeTracking(oe, re);
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final TimeTracking value) {
        return fields.timeTracking(value);
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        return fields.timeTracking((TimeTracking)value);
    }
}
