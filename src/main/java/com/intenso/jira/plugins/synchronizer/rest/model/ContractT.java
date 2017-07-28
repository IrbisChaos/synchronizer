// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.HashMap;
import java.util.Collection;
import com.atlassian.jira.event.type.EventType;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.groups.GroupManager;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.entity.ContractStatus;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.ProjectManager;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.RemoteContract;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractT
{
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String contractName;
    @JsonProperty
    private String displayContractName;
    @JsonProperty
    private Integer connectionId;
    @JsonProperty
    private Long projectId;
    @JsonProperty
    private String project;
    @JsonProperty
    private String issueTypeId;
    @JsonProperty
    private String issueType;
    @JsonProperty
    private Integer workflowMappingId;
    @JsonProperty
    private String workflowMapping;
    @JsonProperty
    private String remoteContractName;
    @JsonProperty
    private Integer status;
    @JsonProperty
    private String statusName;
    @JsonProperty
    private Long[] createEvents;
    @JsonProperty
    private Long[] updateEvents;
    @JsonProperty
    private Long[] deleteEvents;
    @JsonProperty
    private Integer comments;
    @JsonProperty
    private Integer attachments;
    @JsonProperty
    private String updateEventsName;
    @JsonProperty
    private String updateEventsIds;
    @JsonProperty
    private String createEventsName;
    @JsonProperty
    private String createEventsIds;
    @JsonProperty
    private String deleteEventsName;
    @JsonProperty
    private String deleteEventsIds;
    @JsonProperty
    private String connectionName;
    @JsonProperty
    private Integer enableExternalComment;
    @JsonProperty
    private Integer synchronizeAllComments;
    @JsonProperty
    private Integer allCommentRestrictions;
    @JsonProperty
    private Integer synchronizeAllAttachments;
    @JsonProperty
    private Integer addPrefixToAttachments;
    @JsonProperty
    private List<ContractFieldMappingEntryT> fieldMappingEntries;
    @JsonProperty
    private String[] validationMsg;
    @JsonProperty
    private Map<String, Object> validationMap;
    @JsonProperty
    private Integer hasWarnings;
    @JsonProperty
    private Integer remoteCreate;
    @JsonProperty
    private Integer remoteUpdate;
    @JsonProperty
    private Integer remoteDelete;
    @JsonProperty
    private Integer remoteComments;
    @JsonProperty
    private Integer remoteAttachments;
    @JsonProperty
    private String jqlConstraints;
    @JsonProperty
    private Integer worklogs;
    @JsonProperty
    private Integer remoteWorklogs;
    
    public ContractT() {
        this.fieldMappingEntries = new ArrayList<ContractFieldMappingEntryT>();
    }
    
    public ContractT(final Contract contract, final List<ContractEventT> createEvents, final List<ContractEventT> updateEvents, final List<ContractEventT> deleteEvents, final RemoteContract remoteContract) {
        this(contract);
        final List<Long> createList = new ArrayList<Long>();
        if (createEvents != null) {
            for (final ContractEventT c : createEvents) {
                createList.add(c.getEventId());
            }
        }
        final List<Long> updateList = new ArrayList<Long>();
        if (updateEvents != null) {
            for (final ContractEventT c2 : updateEvents) {
                updateList.add(c2.getEventId());
            }
        }
        final List<Long> deleteList = new ArrayList<Long>();
        if (deleteEvents != null) {
            for (final ContractEventT c3 : deleteEvents) {
                deleteList.add(c3.getEventId());
            }
        }
        this.createEvents = createList.toArray(new Long[createList.size()]);
        this.updateEvents = updateList.toArray(new Long[updateList.size()]);
        this.deleteEvents = deleteList.toArray(new Long[deleteList.size()]);
        final Map<Long, String> etypes = this.getEventTypes();
        final StringBuilder updateEventsNameBuilder = new StringBuilder();
        final StringBuilder updateEventsIdsBuilder = new StringBuilder();
        if (updateEvents != null) {
            int i = 0;
            for (final ContractEventT ue : updateEvents) {
                if (i != 0) {
                    updateEventsNameBuilder.append(", ");
                    updateEventsIdsBuilder.append(",");
                }
                updateEventsNameBuilder.append(etypes.get(ue.getEventId()));
                updateEventsIdsBuilder.append(ue.getEventId());
                ++i;
            }
        }
        this.updateEventsIds = updateEventsIdsBuilder.toString();
        this.updateEventsName = updateEventsNameBuilder.toString();
        final StringBuilder createEventsNameBuilder = new StringBuilder();
        final StringBuilder createEventsIdsBuilder = new StringBuilder();
        if (createEvents != null) {
            int j = 0;
            for (final ContractEventT ce : createEvents) {
                if (j != 0) {
                    createEventsNameBuilder.append(", ");
                    createEventsIdsBuilder.append(",");
                }
                createEventsNameBuilder.append(etypes.get(ce.getEventId()));
                createEventsIdsBuilder.append(ce.getEventId());
                ++j;
            }
        }
        this.createEventsIds = createEventsIdsBuilder.toString();
        this.createEventsName = createEventsNameBuilder.toString();
        final StringBuilder deleteEventsNameBuilder = new StringBuilder();
        final StringBuilder deleteEventsIdsBuilder = new StringBuilder();
        if (deleteEvents != null) {
            int k = 0;
            for (final ContractEventT de : deleteEvents) {
                if (k != 0) {
                    deleteEventsNameBuilder.append(", ");
                    deleteEventsIdsBuilder.append(",");
                }
                deleteEventsNameBuilder.append(etypes.get(de.getEventId()));
                deleteEventsIdsBuilder.append(de.getEventId());
                ++k;
            }
        }
        this.deleteEventsIds = deleteEventsIdsBuilder.toString();
        this.deleteEventsName = deleteEventsNameBuilder.toString();
        if (remoteContract != null) {
            this.remoteCreate = remoteContract.getCreateEnabled();
            this.remoteUpdate = remoteContract.getUpdateEnabled();
            this.remoteDelete = remoteContract.getDeleteEnabled();
            this.remoteComments = remoteContract.getCommentsEnabled();
            this.remoteAttachments = remoteContract.getAttachmentsEnabled();
            this.remoteWorklogs = remoteContract.getWorklogsEnabled();
        }
    }
    
    public ContractT(final Contract contract) {
        this.fieldMappingEntries = new ArrayList<ContractFieldMappingEntryT>();
        this.setConnectionId(contract.getConnectionId());
        this.setContractName(contract.getContractName());
        this.setId(contract.getID());
        this.setProjectId(contract.getProjectId());
        this.setEnableExternalComment(contract.getEnableExternalComment());
        this.setSynchronizeAllComments(contract.getSynchronizeAllComments());
        this.setSynchronizeAllAttachments(contract.getSynchronizeAllAttachments());
        this.setAllCommentRestrictions(contract.getAllCommentRestrictions());
        this.setAddPrefixToAttachments(contract.getAddPrefixToAttachments());
        this.setIssueTypeId(contract.getIssueType());
        this.setWorkflowMappingId(contract.getWorkflowMapping());
        this.setRemoteContractName(contract.getRemoteContextName());
        this.setJqlConstraints(contract.getJqlConstraints());
        if (contract.getProjectId() != null) {
            final ProjectManager pm = (ProjectManager)ComponentAccessor.getComponent((Class)ProjectManager.class);
            final Project p = pm.getProjectObj(contract.getProjectId());
            if (p != null) {
                this.setProject(p.getName());
            }
        }
        if (contract.getIssueType() != null && !contract.getIssueType().isEmpty()) {
            final IssueTypeManager itm = (IssueTypeManager)ComponentAccessor.getComponent((Class)IssueTypeManager.class);
            final IssueType it = itm.getIssueType(contract.getIssueType());
            if (it != null) {
                this.setIssueType(it.getNameTranslation());
            }
        }
        if (this.getWorkflowMappingId() != null) {
            this.setWorkflowMapping(((WorkflowSyncService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)WorkflowSyncService.class)).getMappingDisplayName(this.getWorkflowMappingId()));
        }
        this.setStatus(contract.getStatus());
        if (contract.getStatus() != null && ContractStatus.values().length > contract.getStatus()) {
            this.setStatusName(ContractStatus.values()[contract.getStatus()].getName());
        }
        this.setComments(contract.getComments());
        this.setAttachments(contract.getAttachments());
        this.setWorklogs(contract.getWorklogs());
        final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        final Connection connection = connectionService.get(this.getConnectionId());
        this.connectionName = connection.getConnectionName();
        final ApplicationUser au = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        final GroupManager userManager = (GroupManager)ComponentAccessor.getComponent((Class)GroupManager.class);
        final boolean isAdmin = userManager.isUserInGroup(au.getName(), "jira-administrators");
        final String remoteIssue = "";
        this.setDisplayContractName(remoteIssue);
        if (isAdmin) {
            this.setDisplayContractName(this.getDisplayContractName() + " " + contract.getContractName() + " (" + this.connectionName + ")");
        }
    }
    
    public ContractT(final Contract contract, final List<ContractEventT> createList, final List<ContractEventT> updateList, final List<ContractEventT> deleteList, final RemoteContract remoteContract, final List<ContractFieldMappingEntry> entries) {
        this(contract, createList, updateList, deleteList, remoteContract);
        final List<ContractFieldMappingEntryT> tr = new ArrayList<ContractFieldMappingEntryT>();
        for (final ContractFieldMappingEntry e : entries) {
            tr.add(new ContractFieldMappingEntryT(e));
        }
        this.fieldMappingEntries = tr;
    }
    
    public Map<Long, String> getEventTypes() {
        final List<EventType> eventTypes = new ArrayList<EventType>(ComponentAccessor.getEventTypeManager().getEventTypes());
        final Map<Long, String> map = new HashMap<Long, String>();
        if (eventTypes != null) {
            for (final EventType et : eventTypes) {
                map.put(et.getId(), et.getName());
            }
        }
        return map;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getContractName() {
        return this.contractName;
    }
    
    public void setContractName(final String contractName) {
        this.contractName = contractName;
    }
    
    public Integer getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final Integer connectionId) {
        this.connectionId = connectionId;
    }
    
    public Long getProjectId() {
        return this.projectId;
    }
    
    public void setProjectId(final Long projectId) {
        this.projectId = projectId;
    }
    
    public String getProject() {
        return this.project;
    }
    
    public void setProject(final String project) {
        this.project = project;
    }
    
    public String getIssueTypeId() {
        return this.issueTypeId;
    }
    
    public void setIssueTypeId(final String issueTypeId) {
        this.issueTypeId = issueTypeId;
    }
    
    public String getIssueType() {
        return this.issueType;
    }
    
    public void setIssueType(final String issueType) {
        this.issueType = issueType;
    }
    
    public String getRemoteContractName() {
        return this.remoteContractName;
    }
    
    public void setRemoteContractName(final String remoteContractName) {
        this.remoteContractName = remoteContractName;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public String getStatusName() {
        return this.statusName;
    }
    
    public void setStatusName(final String statusName) {
        this.statusName = statusName;
    }
    
    public Long[] getCreateEvents() {
        return this.createEvents;
    }
    
    public void setCreateEvents(final Long[] createEvents) {
        this.createEvents = createEvents;
    }
    
    public Long[] getUpdateEvents() {
        return this.updateEvents;
    }
    
    public void setUpdateEvents(final Long[] updateEvents) {
        this.updateEvents = updateEvents;
    }
    
    public Integer getComments() {
        return this.comments;
    }
    
    public void setComments(final Integer comments) {
        this.comments = comments;
    }
    
    public Integer getAllCommentRestrictions() {
        return this.allCommentRestrictions;
    }
    
    public void setAllCommentRestrictions(final Integer allCommentRestrictions) {
        this.allCommentRestrictions = allCommentRestrictions;
    }
    
    public Integer getAttachments() {
        return this.attachments;
    }
    
    public void setAttachments(final Integer attachments) {
        this.attachments = attachments;
    }
    
    public String getUpdateEventsName() {
        return this.updateEventsName;
    }
    
    public void setUpdateEventsName(final String updateEventsName) {
        this.updateEventsName = updateEventsName;
    }
    
    public String getUpdateEventsIds() {
        return this.updateEventsIds;
    }
    
    public void setUpdateEventsIds(final String updateEventsIds) {
        this.updateEventsIds = updateEventsIds;
    }
    
    public String getCreateEventsName() {
        return this.createEventsName;
    }
    
    public void setCreateEventsName(final String createEventsName) {
        this.createEventsName = createEventsName;
    }
    
    public String getCreateEventsIds() {
        return this.createEventsIds;
    }
    
    public void setCreateEventsIds(final String createEventsIds) {
        this.createEventsIds = createEventsIds;
    }
    
    public String getConnectionName() {
        return this.connectionName;
    }
    
    public void setConnectionName(final String connectionName) {
        this.connectionName = connectionName;
    }
    
    public String getDisplayContractName() {
        return this.displayContractName;
    }
    
    public void setDisplayContractName(final String displayContractName) {
        this.displayContractName = displayContractName;
    }
    
    public Integer getEnableExternalComment() {
        return this.enableExternalComment;
    }
    
    public void setEnableExternalComment(final Integer enableExternalComment) {
        this.enableExternalComment = enableExternalComment;
    }
    
    public String[] getValidationMsg() {
        return this.validationMsg;
    }
    
    public void setValidationMsg(final String[] validationMsg) {
        this.validationMsg = validationMsg;
    }
    
    public Map<String, Object> getValidationMap() {
        return this.validationMap;
    }
    
    public void setValidationMap(final Map<String, Object> validationMap) {
        this.validationMap = validationMap;
    }
    
    public List<ContractFieldMappingEntryT> getFieldMappingEntries() {
        return this.fieldMappingEntries;
    }
    
    public void setFieldMappingEntries(final List<ContractFieldMappingEntryT> fieldMappingEntries) {
        this.fieldMappingEntries = fieldMappingEntries;
    }
    
    public Integer getSynchronizeAllComments() {
        return this.synchronizeAllComments;
    }
    
    public void setSynchronizeAllComments(final Integer synchronizeAllComments) {
        this.synchronizeAllComments = synchronizeAllComments;
    }
    
    public Integer getSynchronizeAllAttachments() {
        return this.synchronizeAllAttachments;
    }
    
    public void setSynchronizeAllAttachments(final Integer synchronizeAllAttachments) {
        this.synchronizeAllAttachments = synchronizeAllAttachments;
    }
    
    public Integer getAddPrefixToAttachments() {
        return this.addPrefixToAttachments;
    }
    
    public void setAddPrefixToAttachments(final Integer addPrefixToAttachments) {
        this.addPrefixToAttachments = addPrefixToAttachments;
    }
    
    public Integer getHasWarnings() {
        return this.hasWarnings;
    }
    
    public void setHasWarnings(final Integer hasWarnings) {
        this.hasWarnings = hasWarnings;
    }
    
    public String getJqlConstraints() {
        return this.jqlConstraints;
    }
    
    public void setJqlConstraints(final String jqlConstraints) {
        this.jqlConstraints = jqlConstraints;
    }
    
    public Integer getWorkflowMappingId() {
        return this.workflowMappingId;
    }
    
    public void setWorkflowMappingId(final Integer workflowMappingId) {
        this.workflowMappingId = workflowMappingId;
    }
    
    public String getWorkflowMapping() {
        return this.workflowMapping;
    }
    
    public void setWorkflowMapping(final String workflowMapping) {
        this.workflowMapping = workflowMapping;
    }
    
    public Long[] getDeleteEvents() {
        return this.deleteEvents;
    }
    
    public void setDeleteEvents(final Long[] deleteEvents) {
        this.deleteEvents = deleteEvents;
    }
    
    public String getDeleteEventsName() {
        return this.deleteEventsName;
    }
    
    public void setDeleteEventsName(final String deleteEventsName) {
        this.deleteEventsName = deleteEventsName;
    }
    
    public String getDeleteEventsIds() {
        return this.deleteEventsIds;
    }
    
    public void setDeleteEventsIds(final String deleteEventsIds) {
        this.deleteEventsIds = deleteEventsIds;
    }
    
    public Integer getWorklogs() {
        return this.worklogs;
    }
    
    public void setWorklogs(final Integer worklogs) {
        this.worklogs = worklogs;
    }
    
    public Integer getRemoteWorklogs() {
        return this.remoteWorklogs;
    }
    
    public void setRemoteWorklogs(final Integer remoteWorklogs) {
        this.remoteWorklogs = remoteWorklogs;
    }
}
