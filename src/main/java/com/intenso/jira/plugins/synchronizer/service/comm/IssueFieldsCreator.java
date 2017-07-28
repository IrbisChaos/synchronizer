// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueParentMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueStatusMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueKeyMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueResolvedMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueCreatedMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueUpdatedMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueFixVersionsMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueAffectedVersionsMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueComponentMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueTypeMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueTimeTrackingMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueSummaryMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueSecurityLevelMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueResolutionMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueReporterMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueProjectMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssuePriorityMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueLabelsMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueEnvironmentMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueDueDateMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueDescriptionMapper;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueAssigneeMapper;
import java.util.HashSet;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.context.IssueContext;
import com.atlassian.jira.issue.context.IssueContextImpl;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import java.util.Collection;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.crowd.embedded.impl.ImmutableGroup;
import java.util.ArrayList;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import java.util.HashMap;
import com.atlassian.jira.issue.customfields.impl.ProjectCFType;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import com.atlassian.jira.component.ComponentAccessor;
import java.util.Iterator;
import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import java.util.Set;
import com.intenso.jira.plugins.synchronizer.rest.mapper.IssueFieldMapper;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.List;
import com.atlassian.jira.rest.api.issue.IssueFields;

public class IssueFieldsCreator
{
    private IssueFields fields;
    private List<Contract> contracts;
    public static Map<String, IssueFieldMapper> fieldMappersMap;
    public static List<IssueFieldMapper> fieldsMappersList;
    public static Set<String> textFields;
    public static String PARENT_FIELD_ID;
    public static String PROJECT_FIELD_ID;
    public static String SUMMARY_FIELD_ID;
    public static String ISSUETYPE_FIELD_ID;
    public static String ASSIGNEE_FIELD_ID;
    public static String REPORTER_FIELD_ID;
    public static String PRIORITY_FIELD_ID;
    public static String RESOLUTION_FIELD_ID;
    public static String LABELS_FIELD_ID;
    public static String TIMETRACKING_FIELD_ID;
    public static String SECURITYLEVEL_FIELD_ID;
    public static String VERSIONS_FIELD_ID;
    public static String ENVIRONMENT_FIELD_ID;
    public static String DESCRIPTION_FIELD_ID;
    public static String DUEDATE_FIELD_ID;
    public static String FIXVERSIONS_FIELD_ID;
    public static String COMPONENTS_FIELD_ID;
    public static int CUSTOMFIELD_ID_INDEX;
    public static String UPDATED_ID_INDEX;
    public static String CREATED_ID_INDEX;
    public static String RESOLVED_ID_INDEX;
    public static String KEY_ID;
    public static String STATUS_ID;
    
    public IssueFieldsCreator() {
        this.fields = new IssueFields();
        this.contracts = null;
    }
    
    public void build(final List<ContractChangeItem> updatedFields, final List<Contract> contracts, final Long parentIssueId) {
        this.contracts = contracts;
        for (final ContractChangeItem item : updatedFields) {
            this.mapField(item);
        }
        if (parentIssueId != null) {
            this.fields.parent(ResourceRef.withId(parentIssueId.toString()));
        }
    }
    
    private void mapField(final ContractChangeItem item) {
        if (item.isCustomField()) {
            final CustomField field = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(item.getFieldName());
            if (field != null) {
                final FieldType itemType = item.getType();
                if (itemType == null) {
                    this.fields = this.fields.customField(field.getIdAsLong(), item.getValueGeneral());
                    return;
                }
                if (itemType.equals(FieldType.TYPE_PROJECT) || field.getCustomFieldType() instanceof ProjectCFType) {
                    if (field.getCustomFieldType() instanceof ProjectCFType) {
                        String projectName = item.getText();
                        if (itemType.equals(FieldType.TYPE_SELECT)) {
                            final Map map = (HashMap)item.getValueGeneral();
                            projectName = map.get("value").toString();
                        }
                        final ProjectManager projectManager = (ProjectManager)ComponentAccessor.getComponent((Class)ProjectManager.class);
                        final List<Project> projects = (List<Project>)projectManager.getProjectObjects();
                        Project projectObj = null;
                        for (final Project project : projects) {
                            if (project.getName().equals(projectName)) {
                                projectObj = project;
                                break;
                            }
                        }
                        String value = "";
                        if (projectObj != null) {
                            value = projectObj.getId().toString();
                        }
                        this.fields = this.fields.customField(field.getIdAsLong(), (Object)ResourceRef.withId(value));
                    }
                    else if (field.getCustomFieldType() instanceof SelectCFType) {
                        this.fields = this.fields.customField(field.getIdAsLong(), (Object)this.convertToOption(item.getText(), item.getFieldName(), field, true));
                    }
                    else {
                        this.fields = this.fields.customField(field.getIdAsLong(), item.getValueGeneral());
                    }
                    return;
                }
                final GroupManager groupManager = ComponentAccessor.getGroupManager();
                final UserManager userManager = ComponentAccessor.getUserManager();
                final QueueLogService queueLogService = (QueueLogService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueLogService.class);
                switch (itemType) {
                    case TYPE_CASCADING_SELECT: {
                        final Object[] cascadeArr = (Object[])item.getValueGeneral();
                        if (cascadeArr != null) {
                            final Map<String, Object> builtCascade = this.getBuiltCascade(cascadeArr, field);
                            if (builtCascade.size() > 0) {
                                this.fields = this.fields.customField(field.getIdAsLong(), (Object)builtCascade);
                            }
                            break;
                        }
                        break;
                    }
                    case TYPE_MULTI_SELECT:
                    case TYPE_SELECT: {
                        if (item.getValueGeneral() instanceof Object[]) {
                            final Object[] valueArr = (Object[])item.getValueGeneral();
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)this.convertToOptions(valueArr, item.getFieldName(), field, false));
                            break;
                        }
                        if (item.getValueGeneral() instanceof Map) {
                            final Map map2 = (HashMap)item.getValueGeneral();
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)this.convertToOption(map2.get("value"), item.getFieldName(), field, false));
                            break;
                        }
                        break;
                    }
                    case TYPE_MULTI_GROUP: {
                        if (item.getValueGeneral() instanceof Object[]) {
                            final Object[] valueArr = (Object[])item.getValueGeneral();
                            final Collection<ImmutableGroup> groups = new ArrayList<ImmutableGroup>();
                            final List<String> groupsNotFound = new ArrayList<String>();
                            for (final Object value2 : valueArr) {
                                if (groupManager.getGroup((String)value2) != null) {
                                    groups.add(new ImmutableGroup((String)value2));
                                }
                                else {
                                    groupsNotFound.add((String)value2);
                                }
                            }
                            if (groupsNotFound.size() > 0) {
                                queueLogService.createQueueLog(399, "Not modified: Could not synchronize groups '" + String.join(", ", groupsNotFound) + "' because they do not exist in remote.");
                            }
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)groups.toArray());
                            break;
                        }
                        break;
                    }
                    case TYPE_SINGLE_GROUP: {
                        if (item.getValueGeneral() instanceof Object[]) {
                            ImmutableGroup group = null;
                            final Object[] valueArr2 = (Object[])item.getValueGeneral();
                            if (valueArr2.length >= 1) {
                                if (groupManager.getGroup((String)valueArr2[0]) != null) {
                                    group = new ImmutableGroup((String)valueArr2[0]);
                                }
                                else {
                                    queueLogService.createQueueLog(399, "Not modified: Could not synchronize group '" + (String)valueArr2[0] + "' because it does not exist in remote.");
                                }
                            }
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)group);
                            break;
                        }
                        break;
                    }
                    case TYPE_MULTI_USER: {
                        if (item.getValueGeneral() instanceof Object[]) {
                            final Object[] valueArr = (Object[])item.getValueGeneral();
                            final Collection<ApplicationUser> users = new ArrayList<ApplicationUser>();
                            final List<String> usersNotFound = new ArrayList<String>();
                            for (final Object value2 : valueArr) {
                                final String userName = value2.toString();
                                ApplicationUser user = userManager.getUserByName(userName);
                                Label_1032: {
                                    if (user == null) {
                                        user = userManager.getUserByKey(userName);
                                        if (user == null) {
                                            usersNotFound.add(userName);
                                            break Label_1032;
                                        }
                                    }
                                    users.add(user);
                                }
                            }
                            if (usersNotFound.size() > 0) {
                                queueLogService.createQueueLog(399, "Not modified: Could not synchronize users '" + String.join(", ", usersNotFound) + "' because they do not exist in remote.");
                            }
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)users.toArray());
                            break;
                        }
                        break;
                    }
                    case TYPE_SINGLE_USER: {
                        if (item.getValueGeneral() instanceof Object) {
                            final String userName2 = item.getValueGeneral().toString();
                            ApplicationUser user2 = userManager.getUserByName(userName2);
                            if (user2 == null) {
                                user2 = userManager.getUserByKey(userName2);
                                if (user2 == null) {
                                    queueLogService.createQueueLog(399, "Not modified: Could not synchronize user '" + userName2 + "' because it does not exist in remote.");
                                    break;
                                }
                            }
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)user2);
                            break;
                        }
                        break;
                    }
                    case TYPE_NATIVE: {
                        final FieldType type = FieldType.getCustomFieldTypeFromClass((Field)field);
                        if (type.equals(FieldType.TYPE_LIMITED_TEXT) || type.equals(FieldType.TYPE_UNLIMITED_TEXT)) {
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)item.getText());
                            break;
                        }
                        if (type.equals(FieldType.TYPE_DATE_TIME) || type.equals(FieldType.TYPE_DATE)) {
                            this.fields = this.fields.customField(field.getIdAsLong(), (Object)item.getDate());
                            break;
                        }
                        this.fields = this.fields.customField(field.getIdAsLong(), item.getValue());
                        break;
                    }
                    default: {
                        this.fields = this.fields.customField(field.getIdAsLong(), item.getValueGeneral());
                        break;
                    }
                }
            }
        }
        else {
            Object valueGeneral = item.getValueGeneral();
            if (item.getFieldName().equals("assignee") || item.getFieldName().equals("reporter")) {
                if (item.getType().equals(FieldType.TYPE_NATIVE)) {
                    valueGeneral = item.getValue();
                }
                else {
                    final Iterator<ApplicationUser> iterator2 = (Iterator<ApplicationUser>)((UserSearchService)ComponentAccessor.getComponent((Class)UserSearchService.class)).findUsersByFullName((String)valueGeneral).iterator();
                    if (iterator2.hasNext()) {
                        final ApplicationUser au = iterator2.next();
                        valueGeneral = au.getKey();
                    }
                }
            }
            final IssueFieldMapper mapper = IssueFieldsCreator.fieldMappersMap.get(item.getFieldName());
            if (mapper != null) {
                Long projectId = -1L;
                if (this.contracts != null && this.contracts.size() > 0) {
                    projectId = this.contracts.get(0).getProjectId();
                }
                this.fields = mapper.getIssueFields(this.fields, valueGeneral, projectId);
            }
        }
    }
    
    private List<ResourceRef> convertToOptions(final Object[] valueArr, final String fieldName) {
        final List<ResourceRef> selected = new ArrayList<ResourceRef>();
        for (final Object o : valueArr) {
            final OptionsManager optionsManager = (OptionsManager)ComponentAccessor.getComponentOfType((Class)OptionsManager.class);
            final List<Option> options = (List<Option>)optionsManager.findByOptionValue(o.toString());
            for (final Option option : options) {
                if (option.getRelatedCustomField().getCustomField().getId().equals(fieldName)) {
                    selected.add(ResourceRef.withId(option.getOptionId().toString()));
                }
            }
        }
        return selected;
    }
    
    private List<ResourceRef> convertToOptions(final Object[] valueArr, final String fieldName, final CustomField field, final boolean create) {
        if (field != null && this.contracts != null && this.contracts.size() > 0) {
            final List<ResourceRef> selected = new ArrayList<ResourceRef>();
            final Project project = ((ProjectManager)ComponentAccessor.getComponentOfType((Class)ProjectManager.class)).getProjectObj(this.contracts.get(0).getProjectId());
            final IssueType issueType = ((IssueTypeManager)ComponentAccessor.getComponentOfType((Class)IssueTypeManager.class)).getIssueType(this.contracts.get(0).getIssueType());
            final IssueContextImpl issueContext = new IssueContextImpl(project, issueType);
            final FieldConfig fieldConfig = field.getRelevantConfig((IssueContext)issueContext);
            final OptionsManager optionsManager = (OptionsManager)ComponentManager.getComponentInstanceOfType((Class)OptionsManager.class);
            final Options options = optionsManager.getOptions(fieldConfig);
            for (final Object value : valueArr) {
                boolean exist = false;
                for (final Option option : options.getRootOptions()) {
                    if (option.getValue().equals(value.toString())) {
                        selected.add(ResourceRef.withId(option.getOptionId().toString()));
                        exist = true;
                        break;
                    }
                }
                if (!exist && create) {
                    final Option option2 = optionsManager.createOption(fieldConfig, (Long)null, (Long)null, value.toString());
                    if (option2 != null) {
                        selected.add(ResourceRef.withId(option2.getOptionId().toString()));
                    }
                }
            }
            if (selected.size() == valueArr.length) {
                return selected;
            }
        }
        return this.convertToOptions(valueArr, fieldName);
    }
    
    private ResourceRef convertToOption(final Object value, final String fieldName) {
        final OptionsManager optionsManager = (OptionsManager)ComponentAccessor.getComponentOfType((Class)OptionsManager.class);
        final List<Option> options = (List<Option>)optionsManager.findByOptionValue(value.toString());
        for (final Option option : options) {
            if (option.getRelatedCustomField().getCustomField().getId().equals(fieldName)) {
                return ResourceRef.withId(option.getOptionId().toString());
            }
        }
        return ResourceRef.withKey("");
    }
    
    private ResourceRef convertToOption(final Object value, final String fieldName, final CustomField field, final boolean create) {
        if (field != null && this.contracts != null && this.contracts.size() > 0) {
            final Project project = ((ProjectManager)ComponentAccessor.getComponentOfType((Class)ProjectManager.class)).getProjectObj(this.contracts.get(0).getProjectId());
            final IssueType issueType = ((IssueTypeManager)ComponentAccessor.getComponentOfType((Class)IssueTypeManager.class)).getIssueType(this.contracts.get(0).getIssueType());
            final IssueContextImpl issueContext = new IssueContextImpl(project, issueType);
            final FieldConfig fieldConfig = field.getRelevantConfig((IssueContext)issueContext);
            final OptionsManager optionsManager = (OptionsManager)ComponentManager.getComponentInstanceOfType((Class)OptionsManager.class);
            final Options options = optionsManager.getOptions(fieldConfig);
            for (final Option option : options.getRootOptions()) {
                if (option.getValue().equals(value.toString())) {
                    return ResourceRef.withId(option.getOptionId().toString());
                }
            }
            if (create) {
                final Option option2 = optionsManager.createOption(fieldConfig, (Long)null, (Long)null, value.toString());
                if (option2 != null) {
                    return ResourceRef.withId(option2.getOptionId().toString());
                }
            }
        }
        return this.convertToOption(value, fieldName);
    }
    
    private Map<String, Object> getBuiltCascade(final Object[] valueArr, final CustomField field) {
        final Map<String, Object> result = new HashMap<String, Object>();
        if (field != null && this.contracts != null && this.contracts.size() > 0) {
            final Project project = ((ProjectManager)ComponentAccessor.getComponentOfType((Class)ProjectManager.class)).getProjectObj(this.contracts.get(0).getProjectId());
            final IssueType issueType = ((IssueTypeManager)ComponentAccessor.getComponentOfType((Class)IssueTypeManager.class)).getIssueType(this.contracts.get(0).getIssueType());
            final IssueContextImpl issueContext = new IssueContextImpl(project, issueType);
            final FieldConfig fieldConfig = field.getRelevantConfig((IssueContext)issueContext);
            final OptionsManager optionsManager = (OptionsManager)ComponentManager.getComponentInstanceOfType((Class)OptionsManager.class);
            final Options options = optionsManager.getOptions(fieldConfig);
            boolean existParent = false;
            if (valueArr.length > 0) {
                for (final Option option : options.getRootOptions()) {
                    if (option.getValue().equals(valueArr[0])) {
                        result.put("value", valueArr[0]);
                        existParent = true;
                        break;
                    }
                }
                if (existParent && valueArr.length > 1) {
                    final List<Option> optionsByOptionValue = (List<Option>)optionsManager.findByOptionValue((String)valueArr[1]);
                    if (optionsByOptionValue.size() > 0) {
                        final Map<String, Object> child = new HashMap<String, Object>();
                        child.put("value", valueArr[1]);
                        result.put("child", child);
                    }
                }
            }
        }
        return result;
    }
    
    public IssueFields getIssueFields() {
        return this.fields;
    }
    
    static {
        IssueFieldsCreator.fieldMappersMap = new HashMap<String, IssueFieldMapper>();
        IssueFieldsCreator.fieldsMappersList = new ArrayList<IssueFieldMapper>();
        IssueFieldsCreator.textFields = new HashSet<String>();
        IssueFieldsCreator.PARENT_FIELD_ID = "parent";
        IssueFieldsCreator.PROJECT_FIELD_ID = "project";
        IssueFieldsCreator.SUMMARY_FIELD_ID = "summary";
        IssueFieldsCreator.ISSUETYPE_FIELD_ID = "issuetype";
        IssueFieldsCreator.ASSIGNEE_FIELD_ID = "assignee";
        IssueFieldsCreator.REPORTER_FIELD_ID = "reporter";
        IssueFieldsCreator.PRIORITY_FIELD_ID = "priority";
        IssueFieldsCreator.RESOLUTION_FIELD_ID = "resolution";
        IssueFieldsCreator.LABELS_FIELD_ID = "labels";
        IssueFieldsCreator.TIMETRACKING_FIELD_ID = "timetracking";
        IssueFieldsCreator.SECURITYLEVEL_FIELD_ID = "securitylevel";
        IssueFieldsCreator.VERSIONS_FIELD_ID = "versions";
        IssueFieldsCreator.ENVIRONMENT_FIELD_ID = "environment";
        IssueFieldsCreator.DESCRIPTION_FIELD_ID = "description";
        IssueFieldsCreator.DUEDATE_FIELD_ID = "duedate";
        IssueFieldsCreator.FIXVERSIONS_FIELD_ID = "fixVersions";
        IssueFieldsCreator.COMPONENTS_FIELD_ID = "components";
        IssueFieldsCreator.CUSTOMFIELD_ID_INDEX = "customfield_".length();
        IssueFieldsCreator.UPDATED_ID_INDEX = "updated";
        IssueFieldsCreator.CREATED_ID_INDEX = "created";
        IssueFieldsCreator.RESOLVED_ID_INDEX = "resolutiondate";
        IssueFieldsCreator.KEY_ID = "issuekey";
        IssueFieldsCreator.STATUS_ID = "status";
        IssueFieldsCreator.fieldsMappersList.add(new IssueAssigneeMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueDescriptionMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueDueDateMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueEnvironmentMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueLabelsMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssuePriorityMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueProjectMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueReporterMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueResolutionMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueSecurityLevelMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueSummaryMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueTimeTrackingMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueTypeMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueComponentMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueAffectedVersionsMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueFixVersionsMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueUpdatedMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueCreatedMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueResolvedMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueKeyMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueStatusMapper());
        IssueFieldsCreator.fieldsMappersList.add(new IssueParentMapper());
        for (final IssueFieldMapper mapper : IssueFieldsCreator.fieldsMappersList) {
            IssueFieldsCreator.fieldMappersMap.put(mapper.getFieldId(), mapper);
        }
        IssueFieldsCreator.textFields.add(IssueFieldsCreator.PRIORITY_FIELD_ID);
        IssueFieldsCreator.textFields.add(IssueFieldsCreator.VERSIONS_FIELD_ID);
        IssueFieldsCreator.textFields.add(IssueFieldsCreator.FIXVERSIONS_FIELD_ID);
        IssueFieldsCreator.textFields.add(IssueFieldsCreator.ENVIRONMENT_FIELD_ID);
        IssueFieldsCreator.textFields.add(IssueFieldsCreator.COMPONENTS_FIELD_ID);
        IssueFieldsCreator.textFields.add(IssueFieldsCreator.LABELS_FIELD_ID);
    }
}
