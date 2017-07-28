// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.listener;

import java.text.DateFormat;
import com.atlassian.jira.project.ProjectImpl;
import java.util.HashMap;
import java.util.Map;
import com.atlassian.jira.issue.customfields.option.LazyLoadedOption;
import com.atlassian.jira.user.DelegatingApplicationUser;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.crowd.embedded.impl.ImmutableGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import com.atlassian.jira.issue.IssueManager;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.atlassian.jira.component.ComponentAccessor;
import java.util.Iterator;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueFieldMapper;
import com.intenso.jira.plugins.synchronizer.utils.ChangeHistoryHelper;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.comm.IssueFieldsCreator;
import org.apache.commons.lang.StringEscapeUtils;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public class ContractChangeItemBuilder
{
    private ExtendedLogger logger;
    private CustomFieldManager customFieldManager;
    private FieldManager fieldManager;
    private String type;
    private String fieldName;
    private Issue issue;
    
    public ContractChangeItemBuilder(final Issue issue, final CustomFieldManager customFieldManager, final FieldManager fieldManager) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.issue = issue;
        this.customFieldManager = customFieldManager;
        this.fieldManager = fieldManager;
    }
    
    private void setSafeValue(final String fieldName, final ContractChangeItem ci, final Object value) {
        Label_0073: {
            if (value != null) {
                if (!fieldName.equals("assignee")) {
                    if (!fieldName.equals("reporter")) {
                        break Label_0073;
                    }
                }
                try {
                    ci.setValue(StringEscapeUtils.escapeJava(((Object[])value)[0].toString()));
                    ci.setText(StringEscapeUtils.escapeJava(((Object[])value)[1].toString()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (value != null && (fieldName.equals("created") || fieldName.equals("updated") || fieldName.equals("resolutiondate"))) {
            this.timestampToText(ci, value);
            this.timestampToDateTime(ci, value);
            ci.setValue(value);
        }
        else if (value != null && (fieldName.equals("issuekey") || fieldName.equals("status"))) {
            ci.setText(value.toString());
        }
        else if (value != null & IssueFieldsCreator.textFields.contains(fieldName)) {
            final String safeValue = StringEscapeUtils.escapeJava(value.toString());
            ci.setValue(safeValue);
        }
        else {
            ci.setValue(value);
        }
    }
    
    public ContractChangeItem build(final List<ContractFieldMappingEntry> contractFieldMappingEntries) {
        ContractChangeItem ci = new ContractChangeItem();
        ci.setFieldName(this.fieldName = ChangeHistoryHelper.fieldName2FieldId(this.fieldName));
        Object value = null;
        Field f = null;
        if (this.type.equals("jira")) {
            if (this.fieldName != null) {
                if (this.fieldManager.isNavigableField(this.fieldName)) {
                    f = (Field)this.fieldManager.getNavigableField(this.fieldName);
                    ci.setField(f);
                }
                else {
                    f = this.fieldManager.getField(this.fieldName);
                    ci.setField(f);
                }
            }
            if (f != null) {
                final IssueFieldMapper ifm = IssueFieldsCreator.fieldMappersMap.get(f.getId());
                if (ifm == null) {
                    this.logger.warn(ExtendedLoggerMessageType.EVENT, "Unsupported field type detected. Field " + f.getName() + " (" + f.getId() + ") has no field mapper!");
                    return null;
                }
                value = ifm.getIssueFieldObjectValue(this.issue);
                this.setSafeValue(this.fieldName, ci, value);
            }
            else {
                final IssueFieldMapper ifm = IssueFieldsCreator.fieldMappersMap.get(this.fieldName);
                if (ifm != null) {
                    value = ifm.getIssueFieldObjectValue(this.issue);
                    this.setSafeValue(this.fieldName, ci, value);
                }
            }
            ci.setType(FieldType.TYPE_NATIVE);
        }
        else {
            CustomField field = this.customFieldManager.getCustomFieldObject(this.fieldName);
            if (field == null) {
                field = FieldMappingUtils.getCustomFieldByNameAndContext(this.fieldName, this.issue);
                this.logger.warn(ExtendedLoggerMessageType.EVENT, "ContractChangeItemBuilder.build(): Could not find field " + this.fieldName);
                return ci;
            }
            ci.setField((Field)field);
            ci.setTypeInString(field.getCustomFieldType().getName());
            ci.setFieldName(field.getId());
            final FieldType type = FieldType.getCustomFieldTypeFromClass((Field)field);
            ci.setType(type);
            value = this.issue.getCustomFieldValue(field);
            this.setValue(ci, value, ci.getType());
        }
        for (final ContractFieldMappingEntry fme : contractFieldMappingEntries) {
            if (fme.getLocalFieldId().equals(ci.getFieldName())) {
                ci.setFieldName(fme.getRemoteFieldName());
                break;
            }
        }
        if (ci.getFieldName().isEmpty()) {
            ci = null;
        }
        if (ci != null && ci.getField() != null) {
            ci.setJiraFieldId(ci.getField().getId());
        }
        return ci;
    }
    
    private String getRemoteIssueKey(final Object value) {
        String result = null;
        if (value != null) {
            final IssueManager issueManager = ComponentAccessor.getIssueManager();
            final Issue issue = (Issue)issueManager.getIssueObject(value.toString());
            if (issue != null) {
                final Long issueId = issue.getId();
                final SynchronizedIssuesService synchronizedIssuesService = (SynchronizedIssuesService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizedIssuesService.class);
                final List<SyncIssue> syncIssues = synchronizedIssuesService.findByIssue(issueId);
                if (!syncIssues.isEmpty()) {
                    result = syncIssues.get(0).getRemoteIssueKey();
                }
            }
        }
        return result;
    }
    
    private Long getRemoteIssueId(final Object value) {
        Long result = null;
        try {
            if (value != null) {
                final IssueManager issueManager = ComponentAccessor.getIssueManager();
                final Issue issue = (Issue)issueManager.getIssueObject((Long)value);
                if (issue != null) {
                    final Long issueId = issue.getId();
                    final SynchronizedIssuesService synchronizedIssuesService = (SynchronizedIssuesService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizedIssuesService.class);
                    final List<SyncIssue> syncIssues = synchronizedIssuesService.findByIssue(issueId);
                    if (!syncIssues.isEmpty()) {
                        result = syncIssues.get(0).getRemoteIssueId();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private void setValue(final ContractChangeItem ci, final Object value, final FieldType type) {
        if (value == null) {
            return;
        }
        switch (type) {
            case TYPE_DATE: {
                ci.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(((Timestamp)value).getTime())));
                break;
            }
            case TYPE_DATE_TIME: {
                if (ci.getTypeInString().equals("Date Time Picker")) {
                    this.timestampToDateTime(ci, value);
                    break;
                }
                break;
            }
            case TYPE_DECIMAL: {
                ci.setNumber(Double.valueOf(value.toString()));
                break;
            }
            case TYPE_LABELS: {
                final String string = value.toString().replace("[", "").replace("]", "");
                final String[] stringArray = string.split(",");
                ci.setValues(stringArray);
                break;
            }
            case TYPE_MULTI_GROUP:
            case TYPE_SINGLE_GROUP: {
                final Collection groupCollection = (Collection)value;
                if (groupCollection != null && !groupCollection.isEmpty()) {
                    final Collection<String> result = new ArrayList<String>();
                    final Iterator it = groupCollection.iterator();
                    if (groupCollection.iterator().next() instanceof ImmutableGroup) {
                        while (it.hasNext()) {
                            final ImmutableGroup group = it.next();
                            result.add(StringEscapeUtils.escapeJava(group.getName()));
                        }
                    }
                    ci.setValues(result.toArray());
                    break;
                }
                ci.setValues(new Object[0]);
                break;
            }
            case TYPE_SINGLE_USER: {
                final ApplicationUser singleUser = (ApplicationUser)value;
                ci.setValue(StringEscapeUtils.escapeJava(singleUser.getName()));
                break;
            }
            case TYPE_MULTI_USER: {
                final Collection userCollection = (Collection)value;
                if (userCollection != null && !userCollection.isEmpty()) {
                    final Collection<String> result2 = new ArrayList<String>();
                    final Iterator it2 = userCollection.iterator();
                    if (userCollection.iterator().next() instanceof DelegatingApplicationUser) {
                        while (it2.hasNext()) {
                            final DelegatingApplicationUser user = it2.next();
                            result2.add(StringEscapeUtils.escapeJava(user.getName()));
                        }
                    }
                    ci.setValues(result2.toArray());
                    break;
                }
                ci.setValues(new Object[0]);
                break;
            }
            case TYPE_MULTI_SELECT: {
                final Collection optionCollection = (Collection)value;
                if (optionCollection != null && !optionCollection.isEmpty()) {
                    final Collection<String> result3 = new ArrayList<String>();
                    final Iterator it3 = optionCollection.iterator();
                    if (optionCollection.iterator().next() instanceof LazyLoadedOption) {
                        while (it3.hasNext()) {
                            final LazyLoadedOption option = it3.next();
                            result3.add(StringEscapeUtils.escapeJava(option.getValue()));
                        }
                    }
                    ci.setValues(result3.toArray());
                    break;
                }
                ci.setValues(new Object[0]);
                break;
            }
            case TYPE_CASCADING_SELECT: {
                final Map map = (Map)value;
                if (map == null || map.isEmpty()) {
                    ci.setValues(new Object[0]);
                    break;
                }
                if (map.values().iterator().next() instanceof LazyLoadedOption) {
                    final Collection<String> result4 = new ArrayList<String>();
                    for (final LazyLoadedOption option2 : map.values()) {
                        result4.add(StringEscapeUtils.escapeJava(option2.getValue()));
                    }
                    ci.setValues(result4.toArray());
                    break;
                }
                ci.setValues(map.values().toArray());
                break;
            }
            case TYPE_SELECT: {
                final Map<String, String> result5 = new HashMap<String, String>();
                result5.put("value", StringEscapeUtils.escapeJava(value.toString()));
                ci.setValuesMap(result5);
                break;
            }
            case TYPE_LIMITED_TEXT:
            case TYPE_UNLIMITED_TEXT: {
                ci.setText(StringEscapeUtils.escapeJava(value.toString()));
                break;
            }
            case TYPE_PROJECT: {
                String projectName = "";
                try {
                    if (value instanceof ProjectImpl) {
                        projectName = ((ProjectImpl)value).getName();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                ci.setText(projectName);
                break;
            }
            case TYPE_EPIC_LINK: {
                ci.setText(this.getRemoteIssueKey(value));
                break;
            }
        }
    }
    
    private void timestampToText(final ContractChangeItem ci, final Object value) {
        final DateFormat format2 = new SimpleDateFormat("dd/MMM/yy hh:mm aaa");
        final String date = format2.format((java.util.Date)value);
        ci.setText(date);
    }
    
    private void timestampToDateTime(final ContractChangeItem ci, final Object value) {
        final DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        String date = format2.format((java.util.Date)value);
        final DateFormat df = DateFormat.getDateInstance(0, ComponentAccessor.getJiraAuthenticationContext().getLocale());
        final int offset = df.getTimeZone().getOffset(((Timestamp)value).getTime()) / 1000 / 60 / 60;
        String timeZone = Integer.toString(offset);
        if (offset >= 0) {
            if (timeZone.length() > 1) {
                timeZone = "+" + timeZone;
            }
            else {
                timeZone = "+0" + timeZone;
            }
        }
        else if (timeZone.length() <= 2) {
            timeZone = timeZone.replace("-", "-0");
        }
        date = date.replace("Z", timeZone + "00");
        ci.setDate(date);
    }
    
    public String getType() {
        return this.type;
    }
    
    public ContractChangeItemBuilder type(final String type) {
        this.type = type;
        return this;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public ContractChangeItemBuilder fieldName(final String fieldName) {
        this.fieldName = fieldName;
        return this;
    }
    
    public CustomFieldManager getCustomFieldManager() {
        return this.customFieldManager;
    }
    
    public void setCustomFieldManager(final CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
    
    public FieldManager getFieldManager() {
        return this.fieldManager;
    }
    
    public void setFieldManager(final FieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }
    
    public Issue getIssue() {
        return this.issue;
    }
    
    public void setIssue(final Issue issue) {
        this.issue = issue;
    }
}
