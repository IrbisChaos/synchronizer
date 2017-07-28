// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.intenso.jira.plugins.synchronizer.entity.QueueLogLevel;
import java.text.DateFormat;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.service.JobStatisticService;
import org.quartz.CronExpression;
import org.quartz.Calendar;
import org.quartz.CronTrigger;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.jiraservice.TasksSchedulerImpl;
import java.util.LinkedHashMap;
import com.intenso.jira.plugins.synchronizer.service.RemoteResponse;
import com.intenso.jira.plugins.synchronizer.service.RemoteJiraType;
import com.intenso.jira.plugins.synchronizer.service.ConfigurationJIRAClient;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowMappingT;
import com.atlassian.jira.workflow.WorkflowManager;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserManager;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.util.I18nHelper;
import java.util.HashMap;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.entity.QueueLog;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Map;

public class HealthBoardAction extends LicenseAwareAction
{
    private static final long serialVersionUID = 9012624815235700162L;
    private Integer connectionStatus;
    private Integer contractStatus;
    private Integer jobStatus;
    private Boolean unauthorizedStatus;
    private Map<Connection, String> connectionsMap;
    private Map<Contract, String> contracts;
    private List<QueueLog> qlogs;
    private Map<Integer, Contract> contractsMap;
    private Map<Integer, Connection> contractsConnection;
    
    public HealthBoardAction(final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.connectionStatus = 2;
        this.contractStatus = 2;
        this.jobStatus = 2;
        this.connectionsMap = new HashMap<Connection, String>();
        this.contracts = new HashMap<Contract, String>();
        this.contractsMap = new HashMap<Integer, Contract>();
        this.contractsConnection = new HashMap<Integer, Connection>();
    }
    
    public boolean isHealthBoard() {
        return true;
    }
    
    public I18nHelper getI18nHelper() {
        return ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
    }
    
    @Override
    public String doDefault() throws Exception {
        final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        final List<Connection> connections = connectionService.getAll();
        for (final Connection connection : connections) {
            this.connectionsMap.put(connection, this.validateConnection(connection));
        }
        final QueueLogService queueLogService = (QueueLogService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueLogService.class);
        final Integer offset = 0;
        final Map<String, Object> filters = this.prepareFilters();
        final Integer limit = 10;
        this.qlogs = queueLogService.findAllByFilter(filters, offset, limit);
        final ContractService contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
        final List<Contract> contractsL = contractService.getAll();
        for (final Contract contract : contractsL) {
            this.contracts.put(contract, this.validateContract(contract));
            this.contractsMap.put(contract.getID(), contract);
            if (!this.contractsConnection.containsKey(contract.getID())) {
                this.contractsConnection.put(contract.getID(), connectionService.get(contract.getConnectionId()));
            }
        }
        return super.doDefault();
    }
    
    private String validateContract(final Contract contract) {
        String result = "";
        if (contract.getStatus() == null || contract.getStatus() != 0) {
            result = "disabled";
            if (this.contractStatus != 0) {
                this.contractStatus = 1;
            }
        }
        else {
            result += this.validatePermissions(contract);
            result += this.validateWorkflows(contract);
            if (!result.isEmpty()) {
                this.contractStatus = 0;
            }
        }
        return result;
    }
    
    private String validatePermissions(final Contract contract) {
        String result = "";
        final Long projectId = contract.getProjectId();
        final Integer connectionId = contract.getConnectionId();
        final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        final Connection connection = connectionService.find(connectionId);
        final String technicalUserName = connection.getUsername();
        final UserManager userManager = (UserManager)ComponentAccessor.getComponent((Class)UserManager.class);
        final ApplicationUser technicalUser = userManager.getUserByName(technicalUserName);
        if (technicalUser != null) {
            final ProjectManager projectManager = (ProjectManager)ComponentAccessor.getComponent((Class)ProjectManager.class);
            final Project project = projectManager.getProjectObj(projectId);
            if (project != null) {
                final PermissionManager permissionManager = (PermissionManager)ComponentAccessor.getComponent((Class)PermissionManager.class);
                if (!permissionManager.hasPermission(ProjectPermissions.CREATE_ISSUES, project, technicalUser)) {
                    result = "Insufficient Technical User permissions";
                }
                final ContractService contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
                final List<ContractEvents> updateEventList = contractService.getEventsForContract(contract.getID(), EventType.UPDATE);
                if (!updateEventList.isEmpty() && !permissionManager.hasPermission(ProjectPermissions.EDIT_ISSUES, project, technicalUser)) {
                    result = "Insufficient Technical User permissions";
                }
                final List<ContractEvents> deleteEventList = contractService.getEventsForContract(contract.getID(), EventType.DELETE);
                if (!deleteEventList.isEmpty() && !permissionManager.hasPermission(ProjectPermissions.DELETE_ISSUES, project, technicalUser)) {
                    result = "Insufficient Technical User permissions";
                }
                if (contract.getComments() == 1 && !permissionManager.hasPermission(ProjectPermissions.ADD_COMMENTS, project, technicalUser)) {
                    result = "Insufficient Technical User permissions";
                }
                if (contract.getAttachments() == 1 && !permissionManager.hasPermission(ProjectPermissions.CREATE_ATTACHMENTS, project, technicalUser)) {
                    result = "Insufficient Technical User permissions";
                }
                if (contract.getWorkflowMapping() != null) {
                    if (!permissionManager.hasPermission(ProjectPermissions.TRANSITION_ISSUES, project, technicalUser)) {
                        result = "Insufficient Technical User permissions";
                    }
                    if (!permissionManager.hasPermission(ProjectPermissions.RESOLVE_ISSUES, project, technicalUser)) {
                        result = "Insufficient Technical User permissions";
                    }
                }
                boolean assignee = false;
                boolean reporter = false;
                final ContractFieldMappingEntryService contractFieldMappingEntryService = (ContractFieldMappingEntryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractFieldMappingEntryService.class);
                final List<ContractFieldMappingEntry> contractFieldMappingEntrys = contractFieldMappingEntryService.getAll();
                for (final ContractFieldMappingEntry contractFieldMappingEntry : contractFieldMappingEntrys) {
                    if (contractFieldMappingEntry.getLocalFieldId().equals("assignee")) {
                        assignee = true;
                    }
                    else {
                        if (!contractFieldMappingEntry.getLocalFieldId().equals("reporter")) {
                            continue;
                        }
                        reporter = true;
                    }
                }
                if (assignee && !permissionManager.hasPermission(ProjectPermissions.ASSIGN_ISSUES, project, technicalUser)) {
                    result = "Insufficient Technical User permissions";
                }
                if (reporter && !permissionManager.hasPermission(ProjectPermissions.MODIFY_REPORTER, project, technicalUser)) {
                    result = "Insufficient Technical User permissions";
                }
            }
        }
        return result;
    }
    
    private String validateWorkflows(final Contract contract) {
        String result = "";
        final Integer workflowMappingId = contract.getWorkflowMapping();
        if (workflowMappingId != null) {
            final WorkflowSyncService workflowSyncService = (WorkflowSyncService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)WorkflowSyncService.class);
            final WorkflowMappingT workflowMappingT = workflowSyncService.getWorkflowMapping(workflowMappingId);
            if (workflowMappingT != null) {
                final WorkflowManager workflowManager = (WorkflowManager)ComponentAccessor.getComponent((Class)WorkflowManager.class);
                final JiraWorkflow jiraWorkflow = workflowManager.getWorkflow(contract.getProjectId(), contract.getIssueType());
                final String workflowName = workflowMappingT.getWorkflowName();
                if (!jiraWorkflow.getName().equals(workflowName)) {
                    result = "Wrong workflow mapping";
                }
            }
            else {
                result = "Can not find workflow mapping";
            }
        }
        return result;
    }
    
    private String validateConnection(final Connection connection) {
        String result = "";
        final ConfigurationJIRAClient configurationJIRAClient = (ConfigurationJIRAClient)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConfigurationJIRAClient.class);
        if (connection.getPassive() == null || connection.getPassive() == 0) {
            final RemoteResponse remoteResponse = configurationJIRAClient.testRemoteConnection(connection.getRemoteJiraURL(), connection.getProxy(), connection.getRemoteAuthKey());
            final int type = remoteResponse.getResult();
            this.unauthorizedStatus = remoteResponse.getUnauthorizedStatus();
            if (type != RemoteJiraType.UNKNOWN.ordinal()) {
                boolean success = false;
                if (type == RemoteJiraType.SERVER.ordinal()) {
                    success = configurationJIRAClient.testRemoteAuthenticationKey(connection.getRemoteJiraURL(), connection.getProxy(), connection.getRemoteAuthKey());
                }
                else if (type == RemoteJiraType.CLOUD.ordinal()) {
                    success = configurationJIRAClient.testRemoteAuthenticationKeyCloud(connection.getRemoteJiraURL(), connection.getProxy(), connection.getRemoteAuthKey()).getKeyValid();
                }
                if (!success) {
                    result = result + this.getI18nHelper().getText("connection.authentication.error") + " ";
                }
            }
            else {
                result = result + this.getI18nHelper().getText("connection.error", "Remote") + " ";
            }
        }
        boolean success2 = configurationJIRAClient.testLocalConnection(connection.getAlterBaseUrl(), connection.getUsername(), connection.getPassword());
        if (!success2) {
            final String localUrl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
            success2 = configurationJIRAClient.testLocalConnection(localUrl, connection.getUsername(), connection.getPassword());
            if (success2) {
                result += this.getI18nHelper().getText("connection.local.baseurl.error", "Local");
            }
            else {
                result += this.getI18nHelper().getText("connection.error", "Local");
            }
        }
        if (!result.isEmpty()) {
            this.connectionStatus = 0;
        }
        return result;
    }
    
    public Map<String, String> getJobs() {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("Outgoing", this.validateService(TasksSchedulerImpl.OUTGOING_JOB_NAME));
        result.put("Outgoing Response", this.validateService(TasksSchedulerImpl.OUTGOING_RESPONSE_JOB_NAME));
        result.put("Incoming", this.validateService(TasksSchedulerImpl.INCOMING_JOB_NAME));
        result.put("Incoming Response", this.validateService(TasksSchedulerImpl.INCOMING_RESPONSE_JOB_NAME));
        result.put("Pull", this.validateService(TasksSchedulerImpl.PULL_JOB_NAME));
        result.put("Pull Response", this.validateService(TasksSchedulerImpl.PULL_RESPONSE_JOB_NAME));
        result.put("Pull Configuration", this.validateService(TasksSchedulerImpl.PULL_CONFIGURATION_JOB_NAME));
        result.put("Archive Queues", this.validateArchiveQueuesService(TasksSchedulerImpl.ARCHIVIZATION_JOB_NAME));
        return result;
    }
    
    private String validateArchiveQueuesService(final String jobName) {
        String result = "";
        final int queueIn = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).getConfig().getQueueIn();
        final int queueOut = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).getConfig().getQueueOut();
        if (queueIn != 1 && queueOut != 1) {
            result = "notSet";
        }
        else if (queueIn != 1) {
            result = this.validateService(jobName);
        }
        return result;
    }
    
    private String validateService(final String jobName) {
        String result = "";
        try {
            final String cronExpr = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).getCronByJobName(jobName);
            if (cronExpr != null && !cronExpr.toString().isEmpty()) {
                final CronTrigger cronTrigger = new CronTrigger();
                cronTrigger.setCronExpression(cronExpr.toString());
                cronTrigger.setName(jobName.toString());
                cronTrigger.triggered(null);
                final CronExpression cronExpression = new CronExpression(cronTrigger.getCronExpression());
                final JobStatisticService jobStatisticService = (JobStatisticService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)JobStatisticService.class);
                final Timestamp last = jobStatisticService.getLastRunDate(jobName);
                final Date next = cronExpression.getNextValidTimeAfter(last);
                final java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(next);
                cal.add(12, 5);
                if (cal.getTime().before(new Date())) {
                    final Date lastDate = new Date(last.getTime());
                    final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    result = "Does not run since " + df.format(lastDate);
                }
            }
            else {
                result = "disabled";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "disabled";
        }
        if (!result.isEmpty() && !jobName.equals(TasksSchedulerImpl.ARCHIVIZATION_JOB_NAME)) {
            if (result.equals("disabled") && this.jobStatus != 0) {
                this.jobStatus = 1;
            }
            else {
                this.jobStatus = 0;
            }
        }
        return result;
    }
    
    private Map<String, Object> prepareFilters() {
        final Map<String, Object> filters = new HashMap<String, Object>();
        final Integer status = 2;
        if (status != null) {
            QueueLogLevel queueLogLevel = null;
            if (status < QueueLogLevel.values().length) {
                queueLogLevel = QueueLogLevel.values()[status];
            }
            if (queueLogLevel != null) {
                filters.put("LOG_LEVEL", queueLogLevel);
            }
        }
        return filters;
    }
    
    public String getLogLevel(final int level) {
        if (level > QueueLogLevel.values().length - 1) {
            return new Integer(level).toString();
        }
        return QueueLogLevel.values()[level].toString();
    }
    
    public Map<Connection, String> getConnectionsMap() {
        return this.connectionsMap;
    }
    
    public void setConnectionsMap(final Map<Connection, String> connectionsMap) {
        this.connectionsMap = connectionsMap;
    }
    
    public Map<Contract, String> getContracts() {
        return this.contracts;
    }
    
    public void setContracts(final Map<Contract, String> contracts) {
        this.contracts = contracts;
    }
    
    public List<QueueLog> getQlogs() {
        return this.qlogs;
    }
    
    public void setQlogs(final List<QueueLog> qlogs) {
        this.qlogs = qlogs;
    }
    
    public Map<Integer, Contract> getContractsMap() {
        return this.contractsMap;
    }
    
    public void setContractsMap(final Map<Integer, Contract> contractsMap) {
        this.contractsMap = contractsMap;
    }
    
    public Map<Integer, Connection> getContractsConnection() {
        return this.contractsConnection;
    }
    
    public void setContractsConnection(final Map<Integer, Connection> contractsConnection) {
        this.contractsConnection = contractsConnection;
    }
    
    public Integer getConnectionStatus() {
        return this.connectionStatus;
    }
    
    public void setConnectionStatus(final Integer connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    public Integer getContractStatus() {
        return this.contractStatus;
    }
    
    public void setContractStatus(final Integer contractStatus) {
        this.contractStatus = contractStatus;
    }
    
    public Integer getJobStatus() {
        return this.jobStatus;
    }
    
    public void setJobStatus(final Integer jobStatus) {
        this.jobStatus = jobStatus;
    }
    
    public Boolean getUnauthorizedStatus() {
        return this.unauthorizedStatus;
    }
    
    public void setUnauthorizedStatus(final Boolean unauthorizedStatus) {
        this.unauthorizedStatus = unauthorizedStatus;
    }
}
