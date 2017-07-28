// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import java.util.Arrays;
import com.atlassian.query.Query;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.user.ApplicationUser;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Collection;
import com.atlassian.jira.project.Project;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.intenso.jira.plugins.synchronizer.entity.ContractStatus;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.intenso.jira.plugins.synchronizer.action.WorkflowMapping;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowMappingT;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import com.intenso.jira.plugins.synchronizer.rest.model.ContractEventT;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import com.intenso.jira.plugins.synchronizer.entity.RemoteContract;
import javax.ws.rs.GET;
import java.util.Iterator;
import java.util.List;
import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.rest.model.ContractT;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.intenso.jira.plugins.synchronizer.service.RemoteFieldMappingService;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.security.PermissionManager;
import com.intenso.jira.plugins.synchronizer.service.RemoteContractService;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.project.ProjectManager;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/contract")
@Consumes({ "application/x-www-form-urlencoded", "application/json" })
@Produces({ "application/json" })
public class ContractResource
{
    private final ConnectionService connectionService;
    private final ContractService contractService;
    private final WorkflowSyncService workflowSyncService;
    private final ProjectManager projectManager;
    private final IssueTypeManager issueTypeManager;
    private final IssueManager issueManager;
    private final I18nHelper i18nHelper;
    private final RemoteContractService remoteContractService;
    private final PermissionManager permissionManager;
    private final UserManager userManager;
    private final ContractFieldMappingEntryService contractFieldMappingEntryService;
    private final IssueTypeSchemeManager issueTypeSchemeManager;
    private final RemoteFieldMappingService remoteFieldMappingService;
    private final JqlQueryParser jqlQueryParser;
    private final WorkflowManager workflowManager;
    
    public ContractResource(final RemoteContractService remoteContractService, final ContractService contractService, final ProjectManager projectManager, final IssueTypeManager issueTypeManager, final IssueManager issueManager, final ConnectionService connectionService, final PermissionManager permissionManager, final UserManager userManager, final ContractFieldMappingEntryService contractFieldMappingEntryService, final IssueTypeSchemeManager issueTypeSchemeManager, final RemoteFieldMappingService remoteFieldMappingService, final JqlQueryParser jqlQueryParser, final WorkflowSyncService workflowSyncService, final WorkflowManager workflowManager) {
        this.contractService = contractService;
        this.projectManager = projectManager;
        this.issueTypeManager = issueTypeManager;
        this.issueManager = issueManager;
        this.i18nHelper = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
        this.remoteContractService = remoteContractService;
        this.connectionService = connectionService;
        this.permissionManager = permissionManager;
        this.contractFieldMappingEntryService = contractFieldMappingEntryService;
        this.userManager = userManager;
        this.issueTypeSchemeManager = issueTypeSchemeManager;
        this.remoteFieldMappingService = remoteFieldMappingService;
        this.jqlQueryParser = jqlQueryParser;
        this.workflowSyncService = workflowSyncService;
        this.workflowManager = workflowManager;
    }
    
    @GET
    @Path("issue/{issueKey}")
    public Response getContractsByIssue(@PathParam("issueKey") final String issueKey) {
        final Issue issue = (Issue)this.issueManager.getIssueObject(issueKey);
        if (issue == null) {
            return Response.serverError().status(404).build();
        }
        List<Contract> contracts = this.contractService.findByContext(issue.getProjectObject().getId(), issue.getIssueTypeObject().getId());
        if (contracts == null) {
            contracts = new ArrayList<Contract>();
        }
        final List<ContractT> contractsDTO = new ArrayList<ContractT>();
        for (final Contract c : contracts) {
            contractsDTO.add(new ContractT(c));
        }
        return Response.ok((Object)contractsDTO).build();
    }
    
    @GET
    @Path("remote/{connection}")
    public Response getContractsFromRemote(@PathParam("connection") final Integer connectionId) {
        if (connectionId == null) {
            return Response.serverError().status(404).build();
        }
        final List<String> result = new ArrayList<String>();
        final List<RemoteContract> remoteContracts = this.remoteContractService.findByConnection(connectionId);
        if (remoteContracts != null) {
            for (final RemoteContract rc : remoteContracts) {
                result.add(rc.getContract());
            }
        }
        return Response.ok((Object)result).build();
    }
    
    @POST
    @Path("{contractId}/event/{eventId}/{eventType}")
    public Response setEvent(@PathParam("contractId") final Integer contractId, @PathParam("eventId") final Long eventId, @PathParam("eventType") final Integer eventType) {
        EventType type = null;
        if (eventType != null && eventType < EventType.values().length) {
            type = EventType.values()[eventType];
        }
        if (this.contractService.addContractEvent(contractId, eventId, type) != null) {
            return Response.ok().build();
        }
        return Response.status(210).build();
    }
    
    @DELETE
    @Path("{contractId}/event/{eventId}/{eventType}")
    public Response removeEvent(@PathParam("contractId") final Integer contractId, @PathParam("eventId") final Long eventId, @PathParam("eventType") final Integer eventType) {
        EventType type = null;
        if (eventType != null && eventType < EventType.values().length) {
            type = EventType.values()[eventType];
        }
        if (this.contractService.removeContractEvent(contractId, eventId, type) != null) {
            return Response.ok().build();
        }
        return Response.status(210).build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteContract(@PathParam("id") final Integer id) {
        if (id == null) {
            return Response.serverError().status(500).build();
        }
        final Contract contract = this.contractService.get(id);
        if (contract == null) {
            return Response.serverError().status(404).build();
        }
        this.contractFieldMappingEntryService.deleteByContract(id);
        this.contractService.delete(contract);
        return Response.ok().build();
    }
    
    @GET
    @Path("{id}")
    public Response getContract(@PathParam("id") final Integer id) {
        if (id == null) {
            return Response.serverError().status(404).build();
        }
        final Contract contract = this.contractService.get(id);
        if (contract == null) {
            return Response.serverError().status(404).build();
        }
        final List<ContractEventT> createList = new ArrayList<ContractEventT>();
        final List<ContractEventT> updateList = new ArrayList<ContractEventT>();
        final List<ContractEventT> deleteList = new ArrayList<ContractEventT>();
        final List<ContractEvents> createEventList = this.contractService.getEventsForContract(id, EventType.CREATE);
        final List<ContractEvents> updateEventList = this.contractService.getEventsForContract(id, EventType.UPDATE);
        final List<ContractEvents> deleteEventList = this.contractService.getEventsForContract(id, EventType.DELETE);
        if (createEventList != null) {
            for (final ContractEvents c : createEventList) {
                createList.add(new ContractEventT(c));
            }
        }
        if (updateEventList != null) {
            for (final ContractEvents c : updateEventList) {
                updateList.add(new ContractEventT(c));
            }
        }
        if (deleteEventList != null) {
            for (final ContractEvents c : deleteEventList) {
                deleteList.add(new ContractEventT(c));
            }
        }
        final String remoteName = contract.getRemoteContextName();
        RemoteContract remoteContract = null;
        if (remoteName != null) {
            remoteContract = this.getRemoteContractService().findByName(remoteName, contract.getConnectionId());
        }
        final List<ContractFieldMappingEntry> entries = this.contractFieldMappingEntryService.findByContract(contract.getID());
        final ContractT con = new ContractT(contract, createList, updateList, deleteList, remoteContract, entries);
        final Map<String, Object> validMap = new HashMap<String, Object>();
        validMap.put("warnings", this.validateRemote(contract, entries));
        con.setValidationMap(validMap);
        return Response.ok((Object)con).build();
    }
    
    @GET
    @Path("{id}/workflows")
    public Response getWorkflows(@PathParam("id") final Integer id) {
        final Map<String, Object> result = new HashMap<String, Object>();
        final List<WorkflowMappingT> workflows = new ArrayList<WorkflowMappingT>();
        if (id == null) {
            return Response.serverError().status(404).build();
        }
        final Contract contract = this.contractService.get(id);
        if (contract == null || contract.getProjectId() == null || contract.getIssueType() == null) {
            return Response.serverError().status(404).build();
        }
        final JiraWorkflow jw = this.workflowManager.getWorkflow(contract.getProjectId(), contract.getIssueType());
        String wName = "";
        if (jw != null) {
            wName = jw.getDisplayName();
        }
        for (final WorkflowMapping wm : this.workflowSyncService.getWorkflowMappingList()) {
            if (wm.getWorkflow() != null && wm.getWorkflow().equals(wName)) {
                final WorkflowMappingT wmt = new WorkflowMappingT();
                wmt.setWorkflowMappingId(Integer.parseInt(wm.getId()));
                wmt.setWorkflowMappingDisplayName(wm.getName());
                wmt.setWorkflowName(wm.getName());
                workflows.add(wmt);
            }
        }
        result.put("workflows", workflows);
        return Response.ok((Object)result).build();
    }
    
    private Map<String, String> validateRemote(final Contract contract, final List<ContractFieldMappingEntry> mapping) {
        final Map<String, String> validation = new HashMap<String, String>();
        if (contract.getStatus().equals(ContractStatus.ENABLED.ordinal())) {
            final Project project = this.projectManager.getProjectObj(contract.getProjectId());
            if (project == null) {
                validation.put("Project", "Project not exists");
            }
            if (project != null) {
                final IssueType issueType = this.issueTypeManager.getIssueType(contract.getIssueType());
                final Collection<IssueType> issueTypes = (Collection<IssueType>)this.issueTypeSchemeManager.getIssueTypesForProject(project);
                if (issueType == null) {
                    validation.put("Issue Type", "Issue type not exists.");
                }
                else {
                    boolean found = false;
                    for (final IssueType it : issueTypes) {
                        if (it.getId().equals(issueType.getId())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        validation.put("Issue Type", "Issue type not exists in selected project.");
                    }
                }
            }
            final RemoteContract remoteContract = this.remoteContractService.findByName(contract.getRemoteContextName(), contract.getConnectionId());
            if (remoteContract == null) {
                validation.put("Remote contract", "Remote contract probably not exists or configuration synchronization faild.");
            }
            final String validationError = FieldMappingUtils.validateFieldMapping(contract, mapping);
            if (validationError != null) {
                validation.put("Field Mapping", validationError);
            }
        }
        return validation;
    }
    
    @POST
    @Path("/status")
    public Response updateContractStatus(final ContractT contract) {
        if (contract.getId() == null) {
            return Response.serverError().status(404).build();
        }
        final Contract c = this.contractService.get(contract.getId());
        if (contract.getStatus() != null && contract.getStatus() == ContractStatus.ENABLED.ordinal()) {
            final Map<String, Object> errors = this.contractValidate(c);
            if (errors != null && ((errors.size() > 1 && errors.keySet().contains("warnings")) || (errors.size() > 0 && !errors.keySet().contains("warnings")))) {
                errors.put("warnings", this.validateRemote(this.contractService.get(c.getID()), this.contractFieldMappingEntryService.findByContract(c.getID())));
                return Response.serverError().status(400).entity((Object)errors).build();
            }
        }
        if (contract.getStatus() != null) {
            c.setStatus(contract.getStatus());
            this.contractService.save(c);
        }
        final ContractT result = new ContractT(c);
        final Map<String, Object> validMap = new HashMap<String, Object>();
        final List<ContractFieldMappingEntry> entries = this.contractFieldMappingEntryService.findByContract(c.getID());
        validMap.put("warnings", this.validateRemote(c, entries));
        result.setValidationMap(validMap);
        return Response.ok((Object)result).build();
    }
    
    private Map<String, Object> contractValidate(final Contract c) {
        final Map<String, Object> errors = new HashMap<String, Object>();
        final Integer connectionId = c.getConnectionId();
        final Connection connection = this.connectionService.get(connectionId);
        final String username = connection.getUsername();
        final ApplicationUser user = this.userManager.getUserByName(username);
        if (c.getProjectId() != null && c.getRemoteContextName() != null) {
            final RemoteContract rc = this.remoteContractService.findByName(c.getRemoteContextName(), c.getConnectionId());
            if (rc != null) {
                final Project project = this.projectManager.getProjectObj(c.getProjectId());
                final boolean issueBrowsePermission = this.permissionManager.hasPermission(10, project, user);
                if (!issueBrowsePermission) {
                    errors.put("Browse", "Technical user cannot browse issues in selected project!");
                }
                if (rc.getCreateEnabled() != null && rc.getCreateEnabled().equals(1)) {
                    final boolean issueCreatePermission = this.permissionManager.hasPermission(11, project, user);
                    if (!issueCreatePermission) {
                        errors.put("Create", "Technical user cannot create new issues in selected project!");
                    }
                }
                if (rc.getUpdateEnabled() != null && rc.getUpdateEnabled().equals(1)) {
                    final boolean issueUpdatePermission = this.permissionManager.hasPermission(12, project, user);
                    if (!issueUpdatePermission) {
                        errors.put("Update", "Technical user cannot modify existing issues in selected project!");
                    }
                }
                if (rc.getDeleteEnabled() != null && rc.getDeleteEnabled().equals(1)) {
                    final boolean issueDeletePermission = this.permissionManager.hasPermission(12, project, user);
                    if (!issueDeletePermission) {
                        errors.put("Delete", "Technical user cannot modify existing issues in selected project!");
                    }
                }
                if (rc.getCommentsEnabled() != null && rc.getCommentsEnabled().equals(1)) {
                    final boolean addCommentPermission = this.permissionManager.hasPermission(15, project, user);
                    if (!addCommentPermission) {
                        errors.put("Comments", "Technical user cannot write comments in selected project!");
                    }
                }
                if (rc.getAttachmentsEnabled() != null && rc.getAttachmentsEnabled().equals(1)) {
                    final boolean addAttachmentPermission = this.permissionManager.hasPermission(19, project, user);
                    if (!addAttachmentPermission) {
                        errors.put("Attachments", "Technical user cannot create attachment in this project!");
                    }
                }
                if (c.getWorkflowMapping() != null) {
                    final boolean issueTransitionPermission = this.permissionManager.hasPermission(46, project, user);
                    if (!issueTransitionPermission) {
                        errors.put("Transition", "Technical user cannot transition issues in selected project!");
                    }
                    final boolean issueResolvePermission = this.permissionManager.hasPermission(14, project, user);
                    if (!issueResolvePermission) {
                        errors.put("Resolve", "Technical user cannot resolve issues in selected project!");
                    }
                }
            }
        }
        if (c.getProjectId() == null) {
            errors.put("Project", "Project is required.");
        }
        if (c.getIssueType() == null || c.getIssueType().isEmpty()) {
            errors.put("Issue Type", "Issue type cannot be empty.");
        }
        if (c.getRemoteContextName() == null || c.getRemoteContextName().isEmpty()) {
            errors.put("Remote contract", "Remote contract field is required.");
        }
        final Integer contractFieldMappingSize = this.contractFieldMappingEntryService.countByContract(c.getID());
        if (contractFieldMappingSize == null || contractFieldMappingSize.equals(0)) {
            errors.put("Field Mapping", "Field mapping is empty.");
        }
        if (c.getProjectId() != null && c.getIssueType() != null && !c.getIssueType().isEmpty()) {
            final IssueType issueType = this.issueTypeManager.getIssueType(c.getIssueType());
            final Project project2 = this.projectManager.getProjectObj(c.getProjectId());
            final Collection<IssueType> typesAvailable = (Collection<IssueType>)this.issueTypeSchemeManager.getIssueTypesForProject(project2);
            if (!typesAvailable.contains(issueType)) {
                errors.put("Issue Type", "Issue type not available in selected project.");
            }
        }
        if (c.getWorkflowMapping() != null && !this.workflowSyncService.isValidMapping(c.getWorkflowMapping())) {
            errors.put("Workflow Mapping", "Workflow Mapping definition not found.");
        }
        return errors;
    }
    
    @POST
    @Path("/draft")
    public Response createDraftContract(final ContractT contract) {
        if (contract.getContractName() == null || contract.getContractName().isEmpty() || contract.getConnectionId() == null) {
            return Response.serverError().status(400).build();
        }
        final List<Contract> matchingContracts = this.contractService.findByConnectionAndName(contract.getConnectionId(), contract.getContractName());
        final int a = matchingContracts.size();
        if (a != 0) {
            return Response.status(400).build();
        }
        final Contract ct = this.contractService.createContract(contract.getConnectionId(), contract.getContractName(), ContractStatus.DISABLED);
        return Response.ok((Object)new ContractT(ct)).build();
    }
    
    private boolean isValidJql(final String jql) {
        if (jql != null && !jql.isEmpty()) {
            try {
                final Query q = this.jqlQueryParser.parseQuery(jql);
                if (q != null) {
                    return true;
                }
            }
            catch (JqlParseException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
    
    @POST
    public Response saveContract(final ContractT contract) {
        if (contract.getId() == null) {
            return Response.serverError().status(404).build();
        }
        Contract c = this.contractService.get(contract.getId());
        if (c == null) {
            return Response.serverError().status(404).build();
        }
        final List<ContractFieldMappingEntry> entries = this.contractFieldMappingEntryService.findByContract(contract.getId());
        boolean contractValidation = true;
        try {
            contractValidation = ComponentAccessor.getApplicationProperties().getOption("sync.contract.validation");
        }
        catch (Exception ex) {}
        if (contractValidation && contract.getConnectionId() != null && contract.getProjectId() != null && contract.getIssueTypeId() != null) {
            final List<Contract> contracts = this.contractService.findByContext(contract.getConnectionId(), contract.getProjectId(), contract.getIssueTypeId());
            if (contracts.size() > 0) {
                boolean alreadyExists = false;
                for (final Contract cont : contracts) {
                    if (cont.getID() != c.getID()) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (alreadyExists) {
                    final List<ContractEvents> createEventList = this.contractService.getEventsForContract(c.getID(), EventType.CREATE);
                    final List<ContractEvents> updateEventList = this.contractService.getEventsForContract(c.getID(), EventType.UPDATE);
                    final List<ContractEvents> deleteEventList = this.contractService.getEventsForContract(c.getID(), EventType.DELETE);
                    final List<ContractEventT> createList = new ArrayList<ContractEventT>();
                    final List<ContractEventT> updateList = new ArrayList<ContractEventT>();
                    final List<ContractEventT> deleteList = new ArrayList<ContractEventT>();
                    if (createEventList != null) {
                        for (final ContractEvents cc : createEventList) {
                            createList.add(new ContractEventT(cc));
                        }
                    }
                    if (updateEventList != null) {
                        for (final ContractEvents cc : updateEventList) {
                            updateList.add(new ContractEventT(cc));
                        }
                    }
                    if (deleteEventList != null) {
                        for (final ContractEvents cc : deleteEventList) {
                            deleteList.add(new ContractEventT(cc));
                        }
                    }
                    RemoteContract remoteContract = null;
                    if (c.getRemoteContextName() != null) {
                        remoteContract = this.remoteContractService.findByName(c.getRemoteContextName(), c.getConnectionId());
                    }
                    final ContractT ct = new ContractT(c, createList, updateList, deleteList, remoteContract, entries);
                    ct.setValidationMsg(new String[] { this.i18nHelper.getText("contract.context.not.unique") });
                    return Response.serverError().status(555).entity((Object)ct).build();
                }
            }
        }
        c.setStatus(contract.getStatus());
        c.setConnectionId(contract.getConnectionId());
        c.setProjectId(contract.getProjectId());
        c.setIssueType(contract.getIssueTypeId());
        c.setRemoteContextName(contract.getRemoteContractName());
        c.setAttachments(contract.getAttachments());
        c.setComments(contract.getComments());
        c.setWorklogs(contract.getWorklogs());
        c.setEnableExternalComment(contract.getEnableExternalComment());
        c.setSynchronizeAllComments(contract.getSynchronizeAllComments());
        c.setSynchronizeAllAttachments(contract.getSynchronizeAllAttachments());
        c.setWorkflowMapping(contract.getWorkflowMappingId());
        c.setAddPrefixToAttachments(contract.getAddPrefixToAttachments());
        c.setAllCommentRestrictions(contract.getAllCommentRestrictions());
        Map<String, Object> errors = null;
        if (contract.getJqlConstraints() != null && !contract.getJqlConstraints().isEmpty()) {
            if (!this.isValidJql(contract.getJqlConstraints())) {
                errors = new HashMap<String, Object>();
                errors.put("Jql Constraints", "JQL is invalid. Please enter valid JQL query.");
            }
            else {
                c.setJqlConstraints(contract.getJqlConstraints());
            }
        }
        else {
            c.setJqlConstraints(null);
        }
        if (c.getStatus() == ContractStatus.ENABLED.ordinal()) {
            if (errors != null) {
                errors.putAll(this.contractValidate(c));
            }
            else {
                errors = this.contractValidate(c);
            }
            if (errors != null && ((errors.size() > 1 && errors.keySet().contains("warnings")) || (errors.size() > 0 && !errors.keySet().contains("warnings")))) {
                errors.put("warnings", this.validateRemote(this.contractService.get(c.getID()), this.contractFieldMappingEntryService.findByContract(c.getID())));
                final List<ContractEvents> createEventList2 = this.contractService.getEventsForContract(c.getID(), EventType.CREATE);
                final List<ContractEvents> updateEventList2 = this.contractService.getEventsForContract(c.getID(), EventType.UPDATE);
                final List<ContractEvents> deleteEventList2 = this.contractService.getEventsForContract(c.getID(), EventType.DELETE);
                final List<ContractEventT> createList2 = new ArrayList<ContractEventT>();
                final List<ContractEventT> updateList2 = new ArrayList<ContractEventT>();
                final List<ContractEventT> deleteList2 = new ArrayList<ContractEventT>();
                if (createEventList2 != null) {
                    for (final ContractEvents cc2 : createEventList2) {
                        createList2.add(new ContractEventT(cc2));
                    }
                }
                if (updateEventList2 != null) {
                    for (final ContractEvents cc2 : updateEventList2) {
                        updateList2.add(new ContractEventT(cc2));
                    }
                }
                if (deleteEventList2 != null) {
                    for (final ContractEvents cc2 : deleteEventList2) {
                        deleteList2.add(new ContractEventT(cc2));
                    }
                }
                c = this.contractService.get(contract.getId());
                RemoteContract remoteContract2 = null;
                if (c.getRemoteContextName() != null) {
                    remoteContract2 = this.remoteContractService.findByName(c.getRemoteContextName(), c.getConnectionId());
                }
                final ContractT ct2 = new ContractT(c, createList2, updateList2, deleteList2, remoteContract2, entries);
                ct2.setValidationMap(errors);
                return Response.serverError().status(400).entity((Object)ct2).build();
            }
        }
        this.contractService.save(c);
        this.contractService.updateContractEvents(c.getID(), (contract.getCreateEvents() == null) ? new ArrayList<Long>() : Arrays.asList(contract.getCreateEvents()), EventType.CREATE);
        this.contractService.updateContractEvents(c.getID(), (contract.getUpdateEvents() == null) ? new ArrayList<Long>() : Arrays.asList(contract.getUpdateEvents()), EventType.UPDATE);
        this.contractService.updateContractEvents(c.getID(), (contract.getDeleteEvents() == null) ? new ArrayList<Long>() : Arrays.asList(contract.getDeleteEvents()), EventType.DELETE);
        final List<ContractEvents> createEventList2 = this.contractService.getEventsForContract(c.getID(), EventType.CREATE);
        final List<ContractEvents> updateEventList2 = this.contractService.getEventsForContract(c.getID(), EventType.UPDATE);
        final List<ContractEvents> deleteEventList2 = this.contractService.getEventsForContract(c.getID(), EventType.DELETE);
        final List<ContractEventT> createList2 = new ArrayList<ContractEventT>();
        final List<ContractEventT> updateList2 = new ArrayList<ContractEventT>();
        final List<ContractEventT> deleteList2 = new ArrayList<ContractEventT>();
        if (createEventList2 != null) {
            for (final ContractEvents cc2 : createEventList2) {
                createList2.add(new ContractEventT(cc2));
            }
        }
        if (updateEventList2 != null) {
            for (final ContractEvents cc2 : updateEventList2) {
                updateList2.add(new ContractEventT(cc2));
            }
        }
        if (deleteEventList2 != null) {
            for (final ContractEvents cc2 : deleteEventList2) {
                deleteList2.add(new ContractEventT(cc2));
            }
        }
        RemoteContract remoteContract2 = null;
        if (c.getRemoteContextName() != null) {
            remoteContract2 = this.remoteContractService.findByName(c.getRemoteContextName(), c.getConnectionId());
        }
        final ContractT result = new ContractT(c, createList2, updateList2, deleteList2, remoteContract2, entries);
        final Map<String, Object> validMap = new HashMap<String, Object>();
        validMap.put("warnings", this.validateRemote(c, entries));
        result.setValidationMap(validMap);
        return Response.ok((Object)result).build();
    }
    
    @GET
    @Path("list/{connection}")
    public Response getContractsForConnection(@PathParam("connection") final Integer connection) {
        if (connection == null) {
            return Response.serverError().status(500).build();
        }
        final Map<String, Object> result = new HashMap<String, Object>();
        final List<Contract> contracts = this.contractService.findByConnection(connection);
        final List<ContractT> cresult = new ArrayList<ContractT>();
        if (contracts != null) {
            for (final Contract ct : contracts) {
                final ContractT con = new ContractT(ct);
                final Map<String, String> warnings = this.validateRemote(ct, this.contractFieldMappingEntryService.findByContract(ct.getID()));
                con.setHasWarnings((warnings != null && warnings.size() != 0) ? 1 : 0);
                cresult.add(con);
            }
        }
        result.put("contracts", cresult);
        result.put("connectionId", connection);
        return Response.ok((Object)result).build();
    }
    
    public ProjectManager getProjectManager() {
        return this.projectManager;
    }
    
    public IssueTypeManager getIssueTypeManager() {
        return this.issueTypeManager;
    }
    
    public I18nHelper getI18nHelper() {
        return this.i18nHelper;
    }
    
    public RemoteContractService getRemoteContractService() {
        return this.remoteContractService;
    }
    
    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }
}
