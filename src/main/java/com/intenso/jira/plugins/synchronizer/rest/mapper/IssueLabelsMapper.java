// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import org.boon.Boon;
import java.util.Arrays;
import com.atlassian.jira.rest.api.issue.IssueFields;
import java.util.Iterator;
import org.apache.commons.lang3.StringEscapeUtils;
import com.atlassian.jira.issue.label.Label;
import java.util.ArrayList;
import com.atlassian.jira.issue.Issue;
import java.util.List;

public class IssueLabelsMapper extends AbstractIssueFieldsMapper<List<String>> implements IssueFieldMapper<List<String>>
{
    @Override
    public String getFieldId() {
        return "labels";
    }
    
    @Override
    public String getIssueFieldObjectValue(final Issue is) {
        if (is.getLabels() != null) {
            final List<String> labels = new ArrayList<String>();
            for (final Label label : is.getLabels()) {
                labels.add("\"" + StringEscapeUtils.escapeJson(label.getLabel()) + "\"");
            }
            return labels.toString();
        }
        return null;
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final List<String> value) {
        return fields.labels((List)value);
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        List<String> labels = new ArrayList<String>();
        if (value instanceof Object[]) {
            final Object[] objectArray = (Object[])value;
            final String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, (Class<? extends String[]>)String[].class);
            labels = Arrays.asList(stringArray);
        }
        else if (value != null) {
            labels = Boon.fromJsonArray(value.toString(), String.class);
        }
        return fields.labels((List)labels);
    }
}
