// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.atlassian.jira.component.ComponentAccessor;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.customfield.SynchronizedIssueCFType;
import java.util.Collection;
import java.util.ArrayList;
import com.atlassian.jira.util.BuildUtilsInfoImpl;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;

public class ContractConfigAction extends GenericConfigAction
{
    private static final long serialVersionUID = -5834335936444033124L;
    private ContractService contractService;
    private ConnectionService connectionService;
    private WorkflowSyncService workflowSyncService;
    private List<Connection> connections;
    private List<WorkflowMapping> workflowMappings;
    private List<IssueType> issueTypes;
    private List<Project> projects;
    private List<CustomField> relationalCFs;
    private List<EventType> eventTypes;
    private ProjectManager projectManager;
    private IssueTypeManager issueTypeManager;
    private CustomFieldManager customFieldManager;
    private Integer id;
    private Integer connectionId;
    private String contractName;
    private Long project;
    private String issueType;
    private String remoteContractName;
    private Integer fieldMapping;
    private String relationalCF;
    private Integer[] updateEvents;
    private Integer[] createEvents;
    private Integer contractStatus;
    private Integer attachments;
    private Integer comments;
    private Integer disableComments;
    private Boolean jira5;
    private String workflow;
    private Integer scontract;
    private Integer sconnection;
    
    public ContractConfigAction(final ContractService contractService, final ConnectionService connectionService, final WorkflowSyncService workflowSyncService, final ProjectManager projectManager, final IssueTypeManager issueTypeManager, final CustomFieldManager customFieldManager, final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.jira5 = Boolean.FALSE;
        this.setContractService(contractService);
        this.setConnectionService(connectionService);
        this.setProjectManager(projectManager);
        this.setIssueTypeManager(issueTypeManager);
        this.setCustomFieldManager(customFieldManager);
        this.workflowSyncService = workflowSyncService;
    }
    
    protected void doValidation() {
        super.doValidation();
        if (this.connectionId == null) {
            this.getErrors().put("connectionId", "Connection required");
        }
        if (this.contractName == null || this.contractName.isEmpty()) {
            this.getErrors().put("contractName", "Contract name is required");
        }
        if (this.project == null) {
            this.getErrors().put("project", "Project is required");
        }
        if (this.issueType == null || this.issueType.isEmpty()) {
            this.getErrors().put("issueType", "Issue type is required");
        }
        if (this.fieldMapping == null) {
            this.getErrors().put("fieldMapping", "Field mapping is required");
        }
        if (this.getErrors().size() > 0) {
            try {
                this.doDefault();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String doDefault() throws Exception {
        final BuildUtilsInfoImpl info = new BuildUtilsInfoImpl();
        final Integer major = info.getVersionNumbers()[0];
        if (major != null && major.equals(5)) {
            this.jira5 = Boolean.TRUE;
        }
        this.connections = this.connectionService.getAll();
        this.projects = (List<Project>)this.projectManager.getProjectObjects();
        (this.issueTypes = new ArrayList<IssueType>()).addAll(this.issueTypeManager.getIssueTypes());
        this.workflowMappings = this.workflowSyncService.getWorkflowMappingList();
        final List<CustomField> cfields = (List<CustomField>)this.customFieldManager.getCustomFieldObjects();
        final List<CustomField> filteredFields = new ArrayList<CustomField>();
        for (final CustomField cf : cfields) {
            if (cf.getCustomFieldType().getClass().equals(SynchronizedIssueCFType.class)) {
                filteredFields.add(cf);
            }
        }
        this.relationalCFs = filteredFields;
        return super.doDefault();
    }
    
    @Override
    public boolean isContractConfig() {
        return true;
    }
    
    public ContractService getContractService() {
        return this.contractService;
    }
    
    public void setContractService(final ContractService contractService) {
        this.contractService = contractService;
    }
    
    public List<Connection> getConnections() {
        return this.connections;
    }
    
    public void setConnections(final List<Connection> connections) {
        this.connections = connections;
    }
    
    public ConnectionService getConnectionService() {
        return this.connectionService;
    }
    
    public void setConnectionService(final ConnectionService connectionService) {
        this.connectionService = connectionService;
    }
    
    public List<Project> getProjects() {
        return this.projects;
    }
    
    public void setProjects(final List<Project> projects) {
        this.projects = projects;
    }
    
    public List<IssueType> getIssueTypes() {
        return this.issueTypes;
    }
    
    public void setIssueTypes(final List<IssueType> issueTypes) {
        this.issueTypes = issueTypes;
    }
    
    public ProjectManager getProjectManager() {
        return this.projectManager;
    }
    
    public void setProjectManager(final ProjectManager projectManager) {
        this.projectManager = projectManager;
    }
    
    public IssueTypeManager getIssueTypeManager() {
        return this.issueTypeManager;
    }
    
    public void setIssueTypeManager(final IssueTypeManager issueTypeManager) {
        this.issueTypeManager = issueTypeManager;
    }
    
    public List<CustomField> getRelationalCFs() {
        return this.relationalCFs;
    }
    
    public void setRelationalCFs(final List<CustomField> relationalCFs) {
        this.relationalCFs = relationalCFs;
    }
    
    public Integer getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final Integer connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getContractName() {
        return this.contractName;
    }
    
    public void setContractName(final String contractName) {
        this.contractName = contractName;
    }
    
    public Long getProject() {
        return this.project;
    }
    
    public void setProject(final Long project) {
        this.project = project;
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
    
    public Integer getFieldMapping() {
        return this.fieldMapping;
    }
    
    public void setFieldMapping(final Integer fieldMapping) {
        this.fieldMapping = fieldMapping;
    }
    
    public String getRelationalCF() {
        return this.relationalCF;
    }
    
    public void setRelationalCF(final String relationalCF) {
        this.relationalCF = relationalCF;
    }
    
    public CustomFieldManager getCustomFieldManager() {
        return this.customFieldManager;
    }
    
    public void setCustomFieldManager(final CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer[] getUpdateEvents() {
        return this.updateEvents;
    }
    
    public void setUpdateEvents(final Integer[] updateEvents) {
        this.updateEvents = updateEvents;
    }
    
    public Integer[] getCreateEvents() {
        return this.createEvents;
    }
    
    public void setCreateEvents(final Integer[] createEvents) {
        this.createEvents = createEvents;
    }
    
    public Integer getContractStatus() {
        return this.contractStatus;
    }
    
    public void setContractStatus(final Integer contractStatus) {
        this.contractStatus = contractStatus;
    }
    
    public Integer getComments() {
        return this.comments;
    }
    
    public void setComments(final Integer comments) {
        this.comments = comments;
    }
    
    public Integer getAttachments() {
        return this.attachments;
    }
    
    public void setAttachments(final Integer attachments) {
        this.attachments = attachments;
    }
    
    public List<EventType> getEventTypes() {
        if (this.eventTypes == null) {
            this.eventTypes = new ArrayList<EventType>(ComponentAccessor.getEventTypeManager().getEventTypes());
        }
        return this.eventTypes;
    }
    
    public void setEventTypes(final List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }
    
    public Integer getDisableComments() {
        return this.disableComments;
    }
    
    public void setDisableComments(final Integer disableComments) {
        this.disableComments = disableComments;
    }
    
    public Boolean getJira5() {
        return this.jira5;
    }
    
    public void setJira5(final Boolean jira5) {
        this.jira5 = jira5;
    }
    
    public Integer getScontract() {
        return this.scontract;
    }
    
    public void setScontract(final Integer scontract) {
        this.scontract = scontract;
    }
    
    public Integer getSconnection() {
        return this.sconnection;
    }
    
    public void setSconnection(final Integer sconnection) {
        this.sconnection = sconnection;
    }
    
    public List<WorkflowMapping> getWorkflowMappings() {
        return this.workflowMappings;
    }
    
    public void setWorkflowMappings(final List<WorkflowMapping> workflowMappings) {
        this.workflowMappings = workflowMappings;
    }
}
