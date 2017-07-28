// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import java.util.Map;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.rest.api.issue.IssueFields;

public interface IssueFieldMapper<T>
{
    IssueFields getIssueFields(final IssueFields p0, final Issue p1);
    
    IssueFields getIssueFields(final IssueFields p0, final Issue p1, final Map<String, String> p2);
    
    IssueFields getIssueFields(final IssueFields p0, final Issue p1, final Object p2);
    
    String getFieldId();
    
    Object getIssueFieldObjectValue(final Issue p0);
    
    @Deprecated
    IssueFields getIssueFields(final IssueFields p0, final T p1);
    
    IssueFields getIssueFieldsObj(final IssueFields p0, final Object p1);
    
    IssueFields getIssueFields(final IssueFields p0, final Object p1, final Long p2);
}
