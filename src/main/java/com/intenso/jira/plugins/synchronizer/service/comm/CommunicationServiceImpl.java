// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItemBuilder;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import java.util.Set;
import com.atlassian.jira.issue.resolution.Resolution;
import java.util.Collection;
import java.util.Arrays;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import com.intenso.jira.plugins.synchronizer.entity.ContractStatus;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.query.Query;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.web.bean.PagerFilter;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.ApplicationUser;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import com.atlassian.jira.issue.worklog.Worklog;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssueDecorator;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.jira.issue.attachment.Attachment;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.utils.ChangeHistoryHelper;
import java.util.ArrayList;
import java.util.List;
import org.ofbiz.core.entity.GenericValue;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.intenso.jira.plugins.synchronizer.service.BuildInCommentService;
import com.intenso.jira.plugins.synchronizer.service.CommentService;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.IssueManager;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public class CommunicationServiceImpl implements CommunicationService
{
    private static final ExtendedLogger log;
    private final QueueOutService queueOutService;
    private final QueueLogService queueLogService;
    private final MessageComposerService composer;
    private final ContractService contractService;
    private final ConnectionService connectionService;
    private final SynchronizedIssuesService syncIssuesService;
    private final IssueManager issueManager;
    private final AttachmentManager attachmentManager;
    private final CommentService commentService;
    private final BuildInCommentService buildInCommentService;
    private final JqlQueryParser jqlQueryParser;
    private final SearchProvider searchProvider;
    private final UserManager userManager;
    private final WorkflowSyncService workflowSyncService;
    private final CustomFieldManager customFieldManager;
    private final FieldManager fieldManager;
    
    public CommunicationServiceImpl(final QueueOutService queueOutService, final QueueLogService queueLogService, final MessageComposerService composer, final ContractService contractService, final SynchronizedIssuesService syncIssuesService, final ConnectionService connectionService, final IssueManager issueManager, final AttachmentManager attachmentManager, final CommentService commentService, final BuildInCommentService buildInCommentService, final JqlQueryParser jqlQueryParser, final SearchProvider searchProvider, final UserManager userManager, final WorkflowSyncService workflowSyncService, final CustomFieldManager customFieldManager, final FieldManager fieldManager) {
        this.queueOutService = queueOutService;
        this.queueLogService = queueLogService;
        this.composer = composer;
        this.contractService = contractService;
        this.connectionService = connectionService;
        this.syncIssuesService = syncIssuesService;
        this.issueManager = issueManager;
        this.attachmentManager = attachmentManager;
        this.commentService = commentService;
        this.buildInCommentService = buildInCommentService;
        this.jqlQueryParser = jqlQueryParser;
        this.searchProvider = searchProvider;
        this.userManager = userManager;
        this.workflowSyncService = workflowSyncService;
        this.customFieldManager = customFieldManager;
        this.fieldManager = fieldManager;
    }
    
    private List<AttachmentDTO> getNewAttachments(final GenericValue changelog) {
        final List<Long> attachments = new ArrayList<Long>();
        final List<GenericValue> changes = ChangeHistoryHelper.getChangeHistory(changelog);
        if (changes != null) {
            for (final GenericValue gv : changes) {
                final String field = gv.getString("field");
                final String fieldType = gv.getString("fieldtype");
                if ("jira".equals(fieldType) && field.equalsIgnoreCase("Attachment") && gv.getString("newvalue") != null) {
                    attachments.add(Long.valueOf(gv.getString("newvalue")));
                }
            }
        }
        final List<AttachmentDTO> attachmentObjects = new ArrayList<AttachmentDTO>();
        if (attachments != null) {
            for (final Long att : attachments) {
                final Attachment attachmentObject = this.attachmentManager.getAttachment(att);
                if (attachmentObject != null) {
                    attachmentObjects.add(new AttachmentDTO(attachmentObject));
                }
            }
        }
        return attachmentObjects;
    }
    
    private boolean sendMessageCreate(final Contract contract, final Issue issue) {
        final List<ContractChangeItem> changes = this.contractService.changes(contract, issue);
        if (changes.size() > 0) {
            final String jsonMsg = this.composer.buildInternalJSON(contract, issue, MessageType.CREATE, changes, null);
            final QueueOut entry = this.queueOutService.create(contract, MessageType.CREATE, new SyncIssueDecorator(null).determineQueueOutStatus(), jsonMsg, issue.getId(), null);
            this.queueLogService.createOutLog(entry);
            CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn038", String.valueOf(changes.size()), issue.getKey(), String.valueOf(entry.getID()));
            return true;
        }
        return false;
    }
    
    private boolean sendMessageDelete(final Contract contract, final Issue issue, final SyncIssue remoteIssue) {
        boolean result = false;
        if (remoteIssue != null) {
            if (remoteIssue.getRemoteIssueId() != null) {
                final List<ContractChangeItem> changes = new ArrayList<ContractChangeItem>();
                final String jsonMsg = this.composer.buildInternalJSON(contract, issue, MessageType.DELETE, changes, remoteIssue);
                final QueueOut entry = this.queueOutService.create(contract, MessageType.DELETE, new SyncIssueDecorator(remoteIssue).determineQueueOutStatus(), jsonMsg, issue.getId(), null);
                this.queueLogService.createOutLog(entry);
                CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn057", String.valueOf(changes.size()), issue.getKey(), String.valueOf(entry.getID()));
                result = true;
            }
            final SynchronizedIssuesService synchronizedIssuesService = (SynchronizedIssuesService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizedIssuesService.class);
            final List<SyncIssue> syncIssues = synchronizedIssuesService.findByIssue(issue.getId());
            for (final SyncIssue syncIssue : syncIssues) {
                if (syncIssue.getContractId().equals(remoteIssue.getContractId())) {
                    synchronizedIssuesService.delete(syncIssue);
                }
            }
        }
        return result;
    }
    
    private boolean sendMessage(final Contract contract, final IssueEvent issueEvent, final MessageType msgType, final SyncIssue remoteIssue) {
        if (msgType.equals(MessageType.WORKLOG)) {
            final Worklog worklog = issueEvent.getWorklog();
            if (worklog != null) {
                final String jsonMsg = this.composer.buildInternalJSON(contract, worklog, issueEvent.getIssue(), msgType, remoteIssue);
                if (jsonMsg != null) {
                    final QueueOut entry = this.queueOutService.create(contract, msgType, new SyncIssueDecorator(remoteIssue).determineQueueOutStatus(), jsonMsg, issueEvent.getIssue().getId(), null);
                    this.queueLogService.createOutLog(entry);
                    return true;
                }
            }
            return false;
        }
        if (msgType.equals(MessageType.ATTACHMENT)) {
            final List<AttachmentDTO> attachments = this.getNewAttachments(issueEvent.getChangeLog());
            if (attachments != null && attachments.size() > 0) {
                final List<ContractChangeItem> changes = new ArrayList<ContractChangeItem>();
                final ContractChangeItem cci = new ContractChangeItem();
                cci.setFieldName("Attachment");
                cci.setType(FieldType.TYPE_ATTACHMENT);
                cci.setValues(attachments.toArray());
                changes.add(cci);
                final String jsonMsg2 = this.composer.buildInternalJSON(contract, issueEvent, msgType, changes, remoteIssue);
                final QueueOut entry2 = this.queueOutService.create(contract, msgType, new SyncIssueDecorator(remoteIssue).determineQueueOutStatus(), jsonMsg2, issueEvent.getIssue().getId(), null);
                this.queueLogService.createOutLog(entry2);
                CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn037", String.valueOf(attachments.size()), issueEvent.getIssue().getKey(), String.valueOf(entry2.getID()));
                return true;
            }
            return false;
        }
        else {
            if (msgType.equals(MessageType.CREATE)) {
                return this.sendMessageCreate(contract, issueEvent.getIssue());
            }
            if (msgType.equals(MessageType.DELETE)) {
                return this.sendMessageDelete(contract, issueEvent.getIssue(), remoteIssue);
            }
            final List<ContractChangeItem> changes2 = this.contractService.changes(contract, issueEvent);
            if (changes2.size() > 0) {
                final String jsonMsg = this.composer.buildInternalJSON(contract, issueEvent, msgType, changes2, remoteIssue);
                final QueueOut entry = this.queueOutService.create(contract, msgType, new SyncIssueDecorator(remoteIssue).determineQueueOutStatus(), jsonMsg, issueEvent.getIssue().getId(), null);
                this.queueLogService.createOutLog(entry);
                CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn039", String.valueOf(changes2.size()), issueEvent.getIssue().getKey(), String.valueOf(entry.getID()));
                return true;
            }
            return false;
        }
    }
    
    private void sendMessage(final Contract contract, final Comment comment, final MessageType msgType, final List<SyncIssue> remoteIssues) {
        final String jsonMsg = this.composer.buildInternalJSON(contract, comment, msgType, remoteIssues);
        QueueStatus qs = QueueStatus.NEW;
        if (remoteIssues == null || remoteIssues.isEmpty() || (remoteIssues != null && remoteIssues.size() > 0 && remoteIssues.get(0).getRemoteIssueId() == null)) {
            qs = QueueStatus.PROCESSING;
        }
        final QueueOut entry = this.queueOutService.create(contract, msgType, qs, jsonMsg, comment.getIssueId(), null, comment.getDateInternal());
        this.queueLogService.createOutLog(entry);
    }
    
    private void sendWorkflowMessage(final Contract contract, final List<ContractChangeItem> changes, final Issue issue, final SyncIssue remoteIssue) {
        final String jsonMsg = this.composer.buildInternalJSON(contract, issue, MessageType.WORKFLOW, changes, remoteIssue);
        final QueueOut entry = this.queueOutService.create(contract, MessageType.WORKFLOW, new SyncIssueDecorator(remoteIssue).determineQueueOutStatus(), jsonMsg, issue.getId(), null);
        this.queueLogService.createOutLog(entry);
    }
    
    private boolean changedByTechnicalUser(final Contract contract, final ApplicationUser user) {
        return this.changedByTechnicalUser(contract, (user != null) ? user.getDirectoryUser() : null);
    }
    
    private boolean changedByTechnicalUser(final Contract contract, final User user) {
        if (user != null) {
            final Connection conn = this.connectionService.find(contract.getConnectionId());
            if (conn != null) {
                final ApplicationUser technicalUser = ((UserManager)ComponentAccessor.getComponent((Class)UserManager.class)).getUserByName(conn.getUsername());
                if (technicalUser != null && user.getName().equals(technicalUser.getName())) {
                    return true;
                }
            }
            else {
                CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn040");
            }
        }
        else {
            CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn041");
        }
        return false;
    }
    
    private boolean isJqlConstraintsFulfilled(final Contract contract, final Issue issue) {
        if (contract.getJqlConstraints() != null && !contract.getJqlConstraints().isEmpty()) {
            try {
                final Connection connection = this.connectionService.get(contract.getConnectionId());
                final String username = connection.getUsername();
                final ApplicationUser au = this.userManager.getUserByName(username);
                final StringBuilder completeQuery = new StringBuilder("(");
                completeQuery.append(contract.getJqlConstraints());
                completeQuery.append(") and issuekey= ");
                completeQuery.append(issue.getKey());
                final Query query = this.jqlQueryParser.parseQuery(completeQuery.toString());
                final SearchResults results = this.searchProvider.search(query, au, PagerFilter.getUnlimitedFilter());
                if (results != null && results.getIssues() != null && results.getIssues().size() > 0) {
                    return true;
                }
            }
            catch (JqlParseException e) {
                CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn042", contract.getJqlConstraints(), issue.getKey());
                e.printStackTrace();
            }
            catch (SearchException e2) {
                CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn043", contract.getJqlConstraints());
                e2.printStackTrace();
            }
            return false;
        }
        return true;
    }
    
    private MessageType contractCreate(final Contract contract, final Issue issue, final ApplicationUser user) {
        MessageType createType = null;
        if (contract.getStatus() == ContractStatus.ENABLED.ordinal()) {
            if (!this.isJqlConstraintsFulfilled(contract, issue)) {
                CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn044", issue.getKey(), contract.getContractName(), contract.getJqlConstraints());
                return null;
            }
            if (this.changedByTechnicalUser(contract, user)) {
                CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn045", issue.getKey());
                return null;
            }
            final List<SyncIssue> values = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
            if (values.size() == 0 && this.sendMessageCreate(contract, issue)) {
                this.syncIssuesService.save(contract.getID(), issue.getId(), null, null);
                createType = MessageType.CREATE;
                if (contract.getSynchronizeAllComments() != null && contract.getSynchronizeAllComments() == 1) {
                    this.pushAllComments(contract, issue);
                }
                if (contract.getSynchronizeAllAttachments() != null && contract.getSynchronizeAllAttachments() == 1) {
                    this.pushAllAttachments(contract, issue);
                }
            }
        }
        return createType;
    }
    
    private void contractDelete(final Contract contract, final Issue issue, final ApplicationUser user, final IssueEvent issueEvent) {
        if (contract.getStatus() == ContractStatus.ENABLED.ordinal()) {
            if (!this.isJqlConstraintsFulfilled(contract, issue)) {
                CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn044", issue.getKey(), contract.getContractName(), contract.getJqlConstraints());
                return;
            }
            if (this.changedByTechnicalUser(contract, user)) {
                CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn045 ", issue.getKey());
                return;
            }
            final List<SyncIssue> values = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
            if (values.size() == 0) {
                if (this.contractService.getEventsForContract(contract.getConnectionId(), EventType.CREATE).size() > 0) {}
            }
            else {
                this.sendMessage(contract, issueEvent, MessageType.DELETE, values.get(0));
            }
        }
    }
    
    private void contractUpdate(final Contract contract, final Issue issue, final ApplicationUser user, final IssueEvent issueEvent) {
        if (contract.getStatus() == ContractStatus.ENABLED.ordinal()) {
            if (!this.isJqlConstraintsFulfilled(contract, issue)) {
                CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn044", issue.getKey(), contract.getContractName(), contract.getJqlConstraints());
                return;
            }
            if (this.changedByTechnicalUser(contract, user)) {
                CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn045 ", issue.getKey());
                return;
            }
            final List<SyncIssue> values = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
            if (values.size() == 0) {
                if (this.contractService.getEventsForContract(contract.getConnectionId(), EventType.CREATE).size() > 0) {}
            }
            else {
                this.sendMessage(contract, issueEvent, MessageType.UPDATE, values.get(0));
            }
        }
    }
    
    private void contractAttachments(final List<Contract> contracts, final Issue issue, final IssueEvent issueEvent, final MessageType createType) {
        if (createType == null) {
            for (final Contract contract : contracts) {
                if (contract.getStatus() == ContractStatus.ENABLED.ordinal() && contract.getAttachments() != null && contract.getAttachments() == 1) {
                    if (!this.isJqlConstraintsFulfilled(contract, issue)) {
                        CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn044", issue.getKey(), contract.getContractName(), contract.getJqlConstraints());
                    }
                    else {
                        if (this.changedByTechnicalUser(contract, issueEvent.getUser())) {
                            continue;
                        }
                        final List<SyncIssue> values = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
                        if (values == null || values.size() <= 0) {
                            continue;
                        }
                        this.sendMessage(contract, issueEvent, MessageType.ATTACHMENT, values.get(0));
                    }
                }
            }
        }
    }
    
    private void contractComments(final List<Contract> contracts, final Issue issue, final IssueEvent issueEvent, final MessageType createType) {
        for (final Contract contract : contracts) {
            if (contract.getStatus() == ContractStatus.ENABLED.ordinal()) {
                final List<SyncIssue> syncs = this.syncIssuesService.findByContract(contract.getID(), issueEvent.getIssue().getId());
                if (syncs == null || syncs.size() <= 0 || contract.getComments() == null || contract.getComments() != 1) {
                    continue;
                }
                if (!this.isJqlConstraintsFulfilled(contract, issue)) {
                    CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn044", issue.getKey(), contract.getContractName(), contract.getJqlConstraints());
                }
                else {
                    if (this.changedByTechnicalUser(contract, issueEvent.getUser())) {
                        continue;
                    }
                    if (issueEvent.getEventTypeId().equals(14)) {
                        CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn046", issueEvent.getIssue().getKey());
                    }
                    else if (issueEvent.getEventTypeId().equals(17)) {
                        CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn047", issueEvent.getIssue().getKey());
                    }
                    else if (contract.getEnableExternalComment() == null || !contract.getEnableExternalComment().equals(1)) {
                        final com.atlassian.jira.issue.comments.Comment comment = issueEvent.getComment();
                        if (comment == null) {
                            continue;
                        }
                        final boolean allCommentRestrictions = contract.getAllCommentRestrictions() != null && contract.getAllCommentRestrictions() == 1;
                        final boolean synchronizeComment = allCommentRestrictions || comment.getRoleLevelId() == null;
                        if (!synchronizeComment) {
                            continue;
                        }
                        final List<Comment> commentsToSynchronize = this.commentService.createInternalComment((comment.getAuthorUser() != null) ? comment.getAuthorUser().getName() : null, comment.getBody(), issueEvent.getIssue().getId(), comment.getId(), Arrays.asList(contract), issueEvent.getComment().getCreated());
                        this.send(commentsToSynchronize);
                    }
                    else {
                        CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn048", contract.getContractName(), String.valueOf(contract.getID()));
                    }
                }
            }
        }
    }
    
    @Override
    public void send(final IssueEvent issueEvent) {
        if (issueEvent != null && issueEvent.getIssue() != null) {
            final Issue issue = issueEvent.getIssue();
            final List<Contract> cCreate = this.contractService.getContracts(issueEvent.getEventTypeId(), issue.getIssueTypeId(), issue.getProjectObject().getId(), EventType.CREATE);
            final List<Contract> cDelete = this.contractService.getContracts(issueEvent.getEventTypeId(), issue.getIssueTypeId(), issue.getProjectObject().getId(), EventType.DELETE);
            final List<Contract> cUpdate = this.contractService.getContracts(issueEvent.getEventTypeId(), issue.getIssueTypeId(), issue.getProjectObject().getId(), EventType.UPDATE);
            MessageType createType = null;
            if (cCreate.size() > 0) {
                for (final Contract contract : cCreate) {
                    createType = this.contractCreate(contract, issue, issueEvent.getUser());
                }
            }
            if (cDelete.size() > 0) {
                for (final Contract contract : cDelete) {
                    this.contractDelete(contract, issue, issueEvent.getUser(), issueEvent);
                }
            }
            if (cUpdate.size() > 0) {
                for (final Contract contract : cUpdate) {
                    this.contractUpdate(contract, issue, issueEvent.getUser(), issueEvent);
                }
            }
            final List<Contract> relevantContracts = this.contractService.findByContextAndStatus(issue.getProjectId(), issue.getIssueTypeId(), ContractStatus.ENABLED);
            this.contractAttachments(relevantContracts, issue, issueEvent, createType);
            this.contractComments(relevantContracts, issue, issueEvent, createType);
        }
    }
    
    @Override
    public boolean sendCreate(final Issue issue, final Integer contractId, final ApplicationUser user) {
        final Contract contract = this.contractService.find(contractId);
        return contract != null && contract.getStatus().equals(ContractStatus.ENABLED.ordinal()) && this.contractCreate(contract, issue, user) != null;
    }
    
    @Override
    public void sendTransition(final IssueEvent issueEvent) {
        if (issueEvent.getIssue() != null) {
            final Issue issue = issueEvent.getIssue();
            final List<Contract> cWorkflows = this.contractService.getContractsWithWorkflow(issue.getIssueTypeObject().getId(), issue.getProjectObject().getId(), issue.getWorkflowId());
            Integer[] statusChange = null;
            if (cWorkflows != null && cWorkflows.size() > 0) {
                final GenericValue changelog = issueEvent.getChangeLog();
                statusChange = ChangeHistoryHelper.getChangeHistoryStatusChange(changelog);
                if (statusChange == null || statusChange.length != 2 || statusChange[0] == null || statusChange[1] == null) {
                    return;
                }
            }
            for (final Contract contract : cWorkflows) {
                if (this.changedByTechnicalUser(contract, issueEvent.getUser())) {
                    CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn045 ", issue.getKey());
                }
                else {
                    if (contract.getStatus() != ContractStatus.ENABLED.ordinal()) {
                        continue;
                    }
                    if (!this.isJqlConstraintsFulfilled(contract, issue)) {
                        CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn044", issue.getKey(), contract.getContractName(), contract.getJqlConstraints());
                    }
                    else {
                        final String transitionText = this.workflowSyncService.getOutgoingTransitionText(contract.getWorkflowMapping(), statusChange[0], statusChange[1]);
                        if (transitionText == null) {
                            continue;
                        }
                        final List<ContractChangeItem> changes = new ArrayList<ContractChangeItem>();
                        final ContractChangeItem cci = new ContractChangeItem();
                        cci.setValue(transitionText);
                        cci.setType(FieldType.TYPE_STATUS);
                        changes.add(cci);
                        final boolean resolution = this.workflowSyncService.getOutgoingTransitionResolution(contract.getWorkflowMapping(), statusChange[0], statusChange[1]);
                        if (resolution) {
                            changes.addAll(this.handleResolution(issueEvent));
                        }
                        final List<SyncIssue> values = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
                        for (final SyncIssue remoteIssue : values) {
                            this.sendWorkflowMessage(contract, changes, issue, remoteIssue);
                        }
                    }
                }
            }
        }
    }
    
    private List<ContractChangeItem> handleResolution(final IssueEvent issueEvent) {
        final List<ContractChangeItem> result = new ArrayList<ContractChangeItem>();
        final Issue issue = issueEvent.getIssue();
        final Resolution resolution = issue.getResolution();
        if (resolution != null) {
            final String resolutionName = resolution.getName();
            final ContractChangeItem cci = new ContractChangeItem();
            cci.setValue(resolutionName);
            cci.setType(FieldType.TYPE_RESOLUTION);
            result.add(cci);
        }
        return result;
    }
    
    private void pushAllAttachments(final Contract contract, final Issue issue) {
        final List<Attachment> attachments = (List<Attachment>)this.getAttachmentManager().getAttachments(issue);
        for (final Attachment att : attachments) {
            final List<SyncIssue> values = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
            for (final SyncIssue si : values) {
                this.sendMessage(contract, att, issue, si);
            }
        }
    }
    
    private void sendMessage(final Contract contract, final Attachment att, final Issue issue, final SyncIssue remoteIssue) {
        final List<AttachmentDTO> attachments = new ArrayList<AttachmentDTO>();
        attachments.add(new AttachmentDTO(att));
        final List<ContractChangeItem> changes = new ArrayList<ContractChangeItem>();
        final ContractChangeItem cci = new ContractChangeItem();
        cci.setFieldName("Attachment");
        cci.setType(FieldType.TYPE_ATTACHMENT);
        cci.setValues(attachments.toArray());
        changes.add(cci);
        final String jsonMsg = this.composer.buildInternalJSON(contract, issue, MessageType.ATTACHMENT, changes, remoteIssue);
        final QueueOut entry = this.queueOutService.create(contract, MessageType.ATTACHMENT, new SyncIssueDecorator(remoteIssue).determineQueueOutStatus(), jsonMsg, issue.getId(), null);
        this.queueLogService.createOutLog(entry);
    }
    
    private void pushAllComments(final Contract contract, final Issue issue) {
        final List<com.atlassian.jira.issue.comments.Comment> comments = this.buildInCommentService.getAllBuildInComments(issue.getId());
        for (final com.atlassian.jira.issue.comments.Comment comment : comments) {
            this.commentService.createInternalComment(comment.getAuthorUser().getName(), comment.getBody(), issue.getId(), comment.getId(), Arrays.asList(contract));
        }
        if (contract.getEnableExternalComment() != null && contract.getEnableExternalComment() == 1) {
            final List<Comment> toBeSynchronized = this.commentService.findExternalCommentsByIssue(issue.getId());
            this.send(toBeSynchronized);
        }
        else {
            final List<Comment> toBeSynchronized = this.commentService.findBuildInCommentsByIssue(issue.getId());
            this.send(toBeSynchronized);
        }
    }
    
    @Override
    public void send(final List<Comment> comments) {
        for (final Comment comment : comments) {
            final Issue issue = (Issue)this.issueManager.getIssueObject(comment.getIssueId());
            if (comment.getContractId() != null) {
                final Contract contract = this.contractService.get(comment.getContractId());
                if (contract.getComments() != null && contract.getComments().equals(1)) {
                    final List<SyncIssue> remoteIssues = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
                    this.sendMessage(contract, comment, MessageType.COMMENT, remoteIssues);
                }
                else {
                    CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "No contract for comment synchronization " + comment.getID());
                }
            }
        }
    }
    
    public AttachmentManager getAttachmentManager() {
        return this.attachmentManager;
    }
    
    private List<ContractChangeItem> handleIssueUpdate(final List<GenericValue> changeItems, final Issue issue, final Set<String> mappedFields, final List<ContractFieldMappingEntry> contractfieldMappingEntries) {
        final List<ContractChangeItem> result = new ArrayList<ContractChangeItem>();
        for (final GenericValue changetemp : changeItems) {
            final String field = changetemp.getString("field");
            final String fieldType = changetemp.getString("fieldtype");
            final ContractChangeItem ci = new ContractChangeItemBuilder(issue, this.customFieldManager, this.fieldManager).fieldName(field).type(fieldType).build(contractfieldMappingEntries);
            if (ci != null && ci.getField() != null) {
                boolean exist = false;
                for (final ContractChangeItem item : result) {
                    if (item.getField().getId().equals(ci.getField().getId())) {
                        exist = true;
                        break;
                    }
                }
                if (exist || (!mappedFields.contains(ci.getField().getId()) && !mappedFields.contains(ChangeHistoryHelper.fieldName2FieldId(ci.getFieldName())))) {
                    continue;
                }
                result.add(ci);
            }
        }
        return result;
    }
    
    @Override
    public void sendWorklog(final IssueEvent issueEvent) {
        if (issueEvent.getIssue() != null) {
            final Issue issue = issueEvent.getIssue();
            final List<Contract> contracts = this.contractService.findByContext(issue.getProjectId(), issue.getIssueTypeId());
            for (final Contract contract : contracts) {
                this.contractWorklog(contract, issue, issueEvent.getUser(), issueEvent);
            }
        }
    }
    
    private void contractWorklog(final Contract contract, final Issue issue, final ApplicationUser user, final IssueEvent issueEvent) {
        if (contract.getWorklogs() == null || contract.getWorklogs() != 1) {
            return;
        }
        if (contract.getStatus() == ContractStatus.ENABLED.ordinal()) {
            if (!this.isJqlConstraintsFulfilled(contract, issue)) {
                CommunicationServiceImpl.log.debug(ExtendedLoggerMessageType.EVENT, "syn044", issue.getKey(), contract.getContractName(), contract.getJqlConstraints());
                return;
            }
            if (this.changedByTechnicalUser(contract, user)) {
                CommunicationServiceImpl.log.warn(ExtendedLoggerMessageType.EVENT, "syn045 ", issue.getKey());
                return;
            }
            final List<SyncIssue> values = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
            if (!values.isEmpty()) {
                this.sendMessage(contract, issueEvent, MessageType.WORKLOG, values.get(0));
            }
        }
    }
    
    static {
        log = ExtendedLoggerFactory.getLogger(CommunicationServiceImpl.class);
    }
}
