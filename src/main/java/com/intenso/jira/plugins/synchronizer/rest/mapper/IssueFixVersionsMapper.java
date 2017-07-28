// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.project.version.Version;
import java.util.Collection;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.rest.api.issue.ResourceRef;
import java.util.List;
import com.atlassian.jira.rest.api.issue.IssueFields;

public class IssueFixVersionsMapper extends IssueAffectedVersionsMapper
{
    @Override
    public String getFieldId() {
        return "fixVersions";
    }
    
    @Override
    protected IssueFields setValue(final IssueFields fields, final List<ResourceRef> resources) {
        return fields.fixVersions((List)resources);
    }
    
    @Override
    protected Collection<Version> getValue(final Issue issue) {
        return (Collection<Version>)issue.getFixVersions();
    }
}
