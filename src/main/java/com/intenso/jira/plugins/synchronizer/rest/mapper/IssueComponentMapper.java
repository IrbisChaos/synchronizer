// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import org.boon.Boon;
import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.rest.api.issue.IssueFields;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.lang3.StringEscapeUtils;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import java.util.ArrayList;
import com.atlassian.jira.issue.Issue;
import java.util.List;

public class IssueComponentMapper extends AbstractIssueFieldsMapper<List<String>> implements IssueFieldMapper<List<String>>
{
    @Override
    public String getFieldId() {
        return "components";
    }
    
    @Override
    public String getIssueFieldObjectValue(final Issue issue) {
        final Collection<ProjectComponent> components = (Collection<ProjectComponent>)issue.getComponentObjects();
        final List<String> resources = new ArrayList<String>();
        for (final ProjectComponent p : components) {
            final List<String> resource = new ArrayList<String>();
            resource.add("\"" + StringEscapeUtils.escapeJson(p.getName()) + "\"");
            resource.add("\"" + StringEscapeUtils.escapeJson(p.getDescription()) + "\"");
            resource.add("\"" + StringEscapeUtils.escapeJson(p.getLead()) + "\"");
            resource.add(Long.toString(p.getAssigneeType()));
            resources.add(resource.toString());
        }
        return resources.toString();
    }
    
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final List<String> resourcesString) {
        final List<ResourceRef> resources = new ArrayList<ResourceRef>();
        for (final String res : resourcesString) {
            resources.add(ResourceRef.withName(res));
        }
        return fields.components((List)resources);
    }
    
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final Object value, final Long projectId) {
        final List<ResourceRef> resources = new ArrayList<ResourceRef>();
        if (value != null) {
            final List<List> resourcesString = (List<List>)Boon.fromJsonArray(value.toString(), List.class);
            final ProjectComponentManager pcm = (ProjectComponentManager)ComponentAccessor.getComponent((Class)ProjectComponentManager.class);
            for (final List res : resourcesString) {
                if (pcm.findByComponentName(projectId, res.get(0).toString()) == null) {
                    pcm.create(res.get(0).toString(), res.get(1).toString(), res.get(2).toString(), Long.parseLong(res.get(3).toString()), projectId);
                }
                resources.add(ResourceRef.withName(res.get(0).toString()));
            }
        }
        return fields.components((List)resources);
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        final List<String> resourcesString = Boon.fromJsonArray(value.toString(), String.class);
        final List<ResourceRef> resources = new ArrayList<ResourceRef>();
        for (final String res : resourcesString) {
            resources.add(ResourceRef.withName(res));
        }
        return fields.components((List)resources);
    }
}
