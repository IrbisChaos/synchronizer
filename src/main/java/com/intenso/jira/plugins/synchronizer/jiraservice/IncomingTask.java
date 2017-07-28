// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.JIRAAttachmentResponse;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.workflow.TransitionOptions;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.config.ResolutionManager;
import com.atlassian.jira.issue.IssueInputParametersImpl;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import java.io.IOException;
import java.io.FileNotFoundException;
import com.intenso.jira.plugins.synchronizer.service.comm.AttachmentDTO;
import com.intenso.jira.plugins.synchronizer.service.RemoteJiraType;
import com.intenso.jira.plugins.synchronizer.rest.model.WorklogT;
import org.codehaus.jackson.map.ObjectMapper;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.atlassian.jira.issue.IssueManager;
import com.intenso.jira.plugins.synchronizer.service.comm.JIRAResponse;
import com.intenso.jira.plugins.synchronizer.service.comm.Response;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexingService;
import com.atlassian.jira.component.ComponentAccessor;
import net.java.ao.Entity;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorServiceImpl;
import com.atlassian.jira.issue.fields.Field;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import com.intenso.jira.plugins.synchronizer.service.comm.IssueFieldsCreator;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.service.comm.IssueIntDTO;
import com.atlassian.jira.issue.Issue;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.google.gson.JsonParser;
import com.atlassian.activeobjects.tx.Transactional;
import java.util.Iterator;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import java.util.List;
import java.util.Map;

public class IncomingTask extends AbstractTask
{
    private Map<Integer, QueueOutTransactionalObject> queueOutMap;
    
    @Transactional
    protected void process(final List<QueueIn> queue) {
        this.queueOutMap = new HashMap<Integer, QueueOutTransactionalObject>();
        for (final QueueIn entry : queue) {
            try {
                final long startTime = System.nanoTime();
                final QueueInConfig qic = new QueueInConfigBuilder(entry).build();
                if (qic == null) {
                    this.log.error(ExtendedLoggerMessageType.JOB, "Invalid entry QueueIn: " + entry.getID());
                    entry.setStatus(QueueStatus.ERROR.ordinal());
                    this.getQueueInService().update(entry);
                }
                else {
                    if (qic.getContract() != null && qic.getContract().getStatus() == 1) {
                        continue;
                    }
                    entry.setStatus(QueueStatus.PROCESSING.ordinal());
                    this.getQueueInService().update(entry);
                    this.updateQueueInData(entry, qic);
                    final Integer msgType = qic.getDto().getMsgType();
                    if (msgType != null && msgType.equals(MessageType.COMMENT.ordinal())) {
                        this.handleComment(entry, qic.getDto(), qic.getContract());
                    }
                    else if (msgType != null && msgType.equals(MessageType.ATTACHMENT.ordinal())) {
                        this.handleAttachment(entry, qic.getDto(), qic.getContract(), qic.getConnection());
                    }
                    else if (msgType != null && msgType.equals(MessageType.WORKFLOW.ordinal())) {
                        this.handleWorkflow(entry, qic.getDto(), qic.getContract(), qic.getConnection());
                    }
                    else if (msgType != null && msgType.equals(MessageType.DELETE.ordinal())) {
                        this.handleDelete(entry, qic.getDto(), qic.getContract(), qic.getConnection());
                    }
                    else if (msgType != null && msgType.equals(MessageType.WORKLOG.ordinal())) {
                        this.handleWorklog(entry, qic.getDto(), qic.getContract(), qic.getConnection());
                    }
                    else {
                        this.handleIssue(entry, qic.getDto(), qic.getContract(), qic.getConnection());
                    }
                    final long processingTime = (System.nanoTime() - startTime) / 1000000L;
                    this.createStatistic(entry, processingTime);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                this.log.error(ExtendedLoggerMessageType.JOB, "Incoming Job broken " + e.getMessage());
                this.getQueueLogService().createQueueLog(Integer.valueOf(597), e.getMessage(), entry);
                entry.setStatus(QueueStatus.ERROR.ordinal());
                entry.setUpdateDate(new Date());
                this.getQueueInService().update(entry);
            }
        }
    }
    
    private void createStatistic(final QueueIn entry, final long processingTime) {
        Long attSize = 0L;
        if (entry.getMsgType() != null && entry.getMsgType().equals(MessageType.ATTACHMENT.ordinal())) {
            final JsonParser gson = new JsonParser();
            final JsonObject je = gson.parse(entry.getJsonMsg()).getAsJsonObject();
            final JsonArray ja = je.get("changes").getAsJsonArray();
            for (int i = 0; i < ja.size(); ++i) {
                final JsonObject jo = ja.get(i).getAsJsonObject();
                final JsonArray attachmentsArr = jo.get("values").getAsJsonArray();
                if (attachmentsArr != null) {
                    for (int j = 0; j < attachmentsArr.size(); ++j) {
                        try {
                            if (attachmentsArr.get(j).isJsonObject()) {
                                final JsonObject attachment = attachmentsArr.get(j).getAsJsonObject();
                                attSize = (attachment.has("filesize") ? Long.parseLong(attachment.get("filesize").toString()) : 0L);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        this.getStatisticService().create(entry.getConnectionId(), entry.getContractId(), MessageType.getByOrdinal(entry.getMsgType()), QueueType.IN, processingTime, entry.getJsonMsg().length() * 2, attSize);
    }
    
    private void updateQueueInData(final QueueIn entry, final QueueInConfig qic) {
        entry.setMsgType(qic.getDto().getMsgType());
        entry.setContractId(qic.getContract().getID());
        entry.setIssueId(qic.getDto().getIssueId());
        if (qic.getDto().getIssueId() != null) {
            final Issue issue = (Issue)this.getIssueManager().getIssueObject(qic.getDto().getIssueId());
            if (issue != null) {
                entry.setIssueKey(issue.getKey());
            }
        }
    }
    
    private void handleIssue(final QueueIn entry, final IssueIntDTO dto, final Contract contract, final Connection connection) {
        SyncIssueTransactionalObject syncIssue = null;
        boolean flag = false;
        boolean isCreate = false;
        try {
            if (dto.getMsgType() == MessageType.CREATE.ordinal()) {
                isCreate = true;
                dto.getChanges().add(new ContractChangeItem(null, IssueFieldsCreator.PROJECT_FIELD_ID, contract.getProjectId().toString(), FieldType.TYPE_NATIVE));
                dto.getChanges().add(new ContractChangeItem(null, IssueFieldsCreator.ISSUETYPE_FIELD_ID, contract.getIssueType(), FieldType.TYPE_NATIVE));
            }
            final List<ContractFieldMappingEntry> fieldMappingEntries = this.getContractFieldMappingEntryService().findByContract(contract.getID());
            if (isCreate) {
                if (dto.getRemoteParentIssueId() != null && this.issueTypeManager.getIssueType(contract.getIssueType()).isSubTask()) {
                    final List<Contract> connectionContracts = this.getContractService().findByConnection(connection.getID());
                    for (final Contract connectionContract : connectionContracts) {
                        final SyncIssue parentSyncIssue = this.getSynchronizedIssuesService().findByContractAndRemoteIssueIdSingleResult(connectionContract.getID(), dto.getRemoteParentIssueId());
                        if (parentSyncIssue != null) {
                            dto.setParentIssueId(parentSyncIssue.getIssueId());
                            break;
                        }
                    }
                }
                else {
                    dto.setRemoteParentIssueId(null);
                }
            }
            final String json = this.getMessageComposerService().buildJSON(dto, fieldMappingEntries);
            final String uri = "/issue" + ((dto.getIssueId() != null) ? ("/" + dto.getIssueId()) : "");
            CommunicatorServiceImpl.HttpRequestMethod method = CommunicatorServiceImpl.HttpRequestMethod.PUT;
            if (isCreate && dto.getIssueId() == null) {
                method = CommunicatorServiceImpl.HttpRequestMethod.POST;
            }
            final Response response = this.getCommunicator().callInternalRest(connection, method, uri, json);
            if (response.getStatus() >= 300) {
                this.getQueueLogService().createQueueLog(response, (Entity)entry, entry.getContractId(), entry.getIssueId());
                entry.setStatus(QueueStatus.ERROR.ordinal());
                this.sentResponse(this.getContractService().get(entry.getContractId()), dto.getIssueId(), entry, Integer.valueOf(7));
            }
            else {
                JIRAResponse respObj;
                if (response.getJson() == null) {
                    respObj = this.sentResponse(contract, dto.getIssueId(), entry);
                }
                else {
                    respObj = this.sentResponse(contract, response, entry);
                }
                syncIssue = new SyncIssueTransactionalObject(contract.getID(), respObj.getIssueIdAsLong(), dto.getRemoteIssueId(), dto.getRemoteIssueKey());
                entry.setIssueId(respObj.getIssueIdAsLong());
                if (respObj.getIssueIdAsLong() != null) {
                    final Issue issueObj = (Issue)this.getIssueManager().getIssueObject(respObj.getIssueIdAsLong());
                    if (issueObj != null) {
                        entry.setIssueKey(issueObj.getKey());
                    }
                }
                this.getQueueLogService().createQueueLog(response.getStatus(), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Response: " + response.getJson(), entry);
                entry.setStatus(QueueStatus.DONE.ordinal());
            }
            entry.setUpdateDate(new Date());
            flag = true;
        }
        catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        finally {
            if (flag) {
                if (syncIssue != null && isCreate) {
                    this.getSynchronizedIssuesService().update(syncIssue.contractId, syncIssue.issueId, syncIssue.remoteIssueId, syncIssue.remoteIssueKey);
                    final IssueManager issueManager = ComponentAccessor.getIssueManager();
                    final Issue issue = (Issue)issueManager.getIssueObject(syncIssue.issueId);
                    final IssueIndexingService issueIndexManager = (IssueIndexingService)ComponentAccessor.getComponent((Class)IssueIndexingService.class);
                    try {
                        issueIndexManager.reIndex(issue);
                    }
                    catch (IndexException e2) {
                        e2.printStackTrace();
                    }
                }
                final QueueOutTransactionalObject tmp = this.queueOutMap.get(entry.getID());
                if (tmp != null) {
                    this.getQueueOutService().create(tmp.contract, tmp.msgType, tmp.status, tmp.jsonMsg, tmp.issueId, tmp.matchQueueEntryId);
                }
            }
            else {
                entry.setStatus(QueueStatus.ERROR.ordinal());
            }
            this.getQueueInService().update(entry);
        }
    }
    
    private void handleDelete(final QueueIn entry, final IssueIntDTO dto, final Contract contract, final Connection connection) {
        if (dto.getIssueId() != null && dto.getRemoteIssueId() != null) {
            final SynchronizedIssuesService synchronizedIssuesService = (SynchronizedIssuesService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizedIssuesService.class);
            final List<SyncIssue> syncIssues = synchronizedIssuesService.findByIssue(dto.getIssueId());
            for (final SyncIssue syncIssue : syncIssues) {
                if (syncIssue.getRemoteIssueId().equals(dto.getRemoteIssueId())) {
                    synchronizedIssuesService.delete(syncIssue);
                }
            }
            entry.setStatus(QueueStatus.DONE.ordinal());
            entry.setIssueId(dto.getIssueId());
            final Issue issueObj = (Issue)this.getIssueManager().getIssueObject(dto.getIssueId());
            if (issueObj != null) {
                entry.setIssueKey(issueObj.getKey());
            }
            this.sentResponse(contract, dto.getIssueId(), entry);
        }
        else {
            final Response response = new Response(500, "IssueId and/or RemoteIssueId is empty");
            this.getQueueLogService().createQueueLog(response, (Entity)entry, entry.getContractId(), entry.getIssueId());
            entry.setStatus(QueueStatus.ERROR.ordinal());
            this.sentResponse(contract, dto.getIssueId(), entry, Integer.valueOf(QueueStatus.ERROR_REMOTE.ordinal()));
        }
        entry.setUpdateDate(new Date());
        this.getQueueInService().update(entry);
    }
    
    private void handleWorklog(final QueueIn entry, final IssueIntDTO dto, final Contract contract, final Connection connection) {
        if (dto.getIssueId() != null && dto.getRemoteIssueId() != null && dto.getWorklog() != null) {
            final WorklogT worklog = dto.getWorklog();
            worklog.addMetaDataToComment(connection, contract, dto.getRemoteIssueKey());
            final ObjectMapper objectMapper = new ObjectMapper();
            String json = null;
            try {
                json = objectMapper.writeValueAsString((Object)worklog);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            final String uri = "/issue/" + dto.getIssueId() + "/worklog";
            final CommunicatorServiceImpl.HttpRequestMethod method = CommunicatorServiceImpl.HttpRequestMethod.POST;
            final Response response = this.getCommunicator().callInternalRest(connection, method, uri, json);
            if (response.getStatus() >= 300) {
                this.getQueueLogService().createQueueLog(response, (Entity)entry, entry.getContractId(), entry.getIssueId());
                entry.setStatus(QueueStatus.ERROR.ordinal());
                this.sentResponse(this.getContractService().get(entry.getContractId()), dto.getIssueId(), entry, Integer.valueOf(7));
            }
            else {
                this.sentResponse(contract, dto.getIssueId(), entry);
                entry.setIssueId(dto.getIssueId());
                final Issue issueObj = (Issue)this.getIssueManager().getIssueObject(dto.getIssueId());
                if (issueObj != null) {
                    entry.setIssueKey(issueObj.getKey());
                }
                this.getQueueLogService().createQueueLog(response.getStatus(), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Response: " + response.getJson(), entry);
                entry.setStatus(QueueStatus.DONE.ordinal());
            }
        }
        else {
            final Response response2 = new Response(500, "IssueId and/or RemoteIssueId is empty");
            this.getQueueLogService().createQueueLog(response2, (Entity)entry, entry.getContractId(), entry.getIssueId());
            entry.setStatus(QueueStatus.ERROR.ordinal());
            this.sentResponse(contract, dto.getIssueId(), entry, Integer.valueOf(QueueStatus.ERROR_REMOTE.ordinal()));
        }
        entry.setUpdateDate(new Date());
        this.getQueueInService().update(entry);
    }
    
    private void handleAttachment(final QueueIn entry, final IssueIntDTO dto, final Contract contract, final Connection connection) throws FileNotFoundException, IOException {
        final String uri = "/issue" + ((dto.getIssueId() != null) ? ("/" + dto.getIssueId()) : "") + "/attachments";
        final List<AttachmentDTO> attachments = this.getMessageComposerService().buildAttachments(contract, entry);
        final boolean addPrefix = contract.getAddPrefixToAttachments() != null && contract.getAddPrefixToAttachments() == 1;
        final Response response = this.getCommunicator().callInternalRestAttachments(connection, uri, attachments, addPrefix);
        if (response.getStatus() >= 300) {
            this.getQueueLogService().createQueueLog(response, (Entity)entry, entry.getContractId(), entry.getIssueId());
            entry.setStatus(QueueStatus.ERROR.ordinal());
            final Contract c = this.getContractService().get(entry.getContractId());
            final Connection con = this.getConnectionService().get(c.getConnectionId());
            if (con.getRemoteJiraType() != null && con.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal())) {
                this.sentResponse(c, dto.getIssueId(), entry, Integer.valueOf(7));
            }
        }
        else {
            this.getQueueLogService().createQueueLog(response.getStatus(), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Response: " + response.getJson(), entry);
            final JIRAResponse respObj = this.sentAttachmentResponse(contract, dto.getIssueId(), response, entry);
            entry.setStatus(QueueStatus.DONE.ordinal());
            this.cleanupAttachmentTemporary(attachments);
        }
        entry.setUpdateDate(new Date());
        this.getQueueInService().update(entry);
    }
    
    private boolean areExternalCommentsEnabled(final Contract contract) {
        return (contract.getEnableExternalComment() == null || contract.getEnableExternalComment() != 1) ? Boolean.FALSE : Boolean.TRUE;
    }
    
    private void handleComment(final QueueIn entry, final IssueIntDTO dto, final Contract contract) {
        entry.setIssueId(dto.getRemoteIssueId());
        if (dto.getRemoteIssueId() != null) {
            entry.setIssueKey(this.getIssueManager().getIssueObject(dto.getRemoteIssueId()).getKey());
        }
        final CommentT commentT = dto.getComment();
        Comment comment = null;
        if (this.areExternalCommentsEnabled(contract)) {
            comment = this.getCommentService().createExternalComment(commentT.getAuthorDisplayName(), commentT.getComment(), dto.getRemoteIssueId(), commentT.getDateInternal(), null, commentT.getId(), Integer.valueOf(contract.getID()));
            this.getNotificationService().notifyAboutComment(comment);
        }
        else {
            final com.atlassian.jira.issue.comments.Comment buildInComment = this.getBuildInCommentService().createComment(dto.getRemoteIssueId(), contract, commentT);
            comment = this.getCommentService().createExternalComment(commentT.getAuthorDisplayName(), commentT.getComment(), dto.getRemoteIssueId(), commentT.getDateInternal(), (buildInComment != null) ? buildInComment.getId() : null, commentT.getId(), Integer.valueOf(contract.getID()));
        }
        if (comment != null) {
            this.getQueueLogService().createQueueLog(Integer.valueOf(200), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Response: Comment created " + comment.getID(), entry);
            final JIRAResponse respObj = this.sentCommentResponse(contract, comment, entry);
            entry.setStatus(QueueStatus.DONE.ordinal());
            entry.setUpdateDate(new Date());
            this.getQueueInService().update(entry);
            return;
        }
        this.getQueueLogService().createQueueLog(Integer.valueOf(500), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Response: comment not created!", entry);
        entry.setStatus(QueueStatus.ERROR.ordinal());
        this.getQueueInService().update(entry);
    }
    
    private void handleWorkflow(final QueueIn entry, final IssueIntDTO dto, final Contract contract, final Connection connection) {
        final Issue issue = (Issue)this.getIssueManager().getIssueObject(dto.getIssueId());
        String workflowValue = null;
        String resolutionValue = null;
        if (issue != null) {
            for (final ContractChangeItem cci : dto.getChanges()) {
                if (cci.getType().equals(FieldType.TYPE_STATUS)) {
                    workflowValue = cci.getValue().toString();
                    try {
                        this.getIssueIndexManager().reIndex(issue);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cci.getType().equals(FieldType.TYPE_RESOLUTION)) {
                    resolutionValue = cci.getValue().toString();
                }
            }
        }
        int resultCode = 0;
        ErrorCollection errorMessage = (ErrorCollection)new SimpleErrorCollection();
        if (contract.getWorkflowMapping() != null) {
            final ApplicationUser user = ComponentAccessor.getUserUtil().getUserByKey(connection.getUsername());
            final Integer transitionId = this.getWorkflowSyncService().getTransitionId(contract.getWorkflowMapping(), workflowValue);
            if (transitionId != null) {
                final SynchronizerConfigService synchronizerConfigService = (SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class);
                final SynchronizerConfig synchronizerConfig = synchronizerConfigService.getConfig();
                final Integer workflowRestApi = synchronizerConfig.getWorkflowRestApi();
                errorMessage = (((workflowRestApi != null) ? (workflowRestApi == 1) : this.getWorkflowSyncService().isRestApiTransition(contract.getWorkflowMapping(), workflowValue)) ? this.executeTransitionRestApi(connection, dto, transitionId, resolutionValue) : this.executeTransition(issue, user, transitionId, resolutionValue));
                if (errorMessage.hasAnyErrors()) {
                    resultCode = 2;
                }
            }
            else {
                resultCode = 3;
            }
        }
        else {
            resultCode = 1;
        }
        entry.setMsgType(MessageType.WORKFLOW.ordinal());
        this.sentResponse(contract, dto.getIssueId(), entry);
        switch (resultCode) {
            case 0: {
                this.getQueueLogService().createQueueLog(Integer.valueOf(202), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Transition: " + workflowValue + " (executed)", entry);
                entry.setStatus(QueueStatus.DONE.ordinal());
                entry.setUpdateDate(new Date());
                break;
            }
            case 1: {
                this.getQueueLogService().createQueueLog(Integer.valueOf(203), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Transition: " + workflowValue + " (not executed, Contract.workflow not set)", entry);
                entry.setStatus(QueueStatus.DONE.ordinal());
                entry.setUpdateDate(new Date());
                break;
            }
            case 2: {
                this.getQueueLogService().createQueueLog(Integer.valueOf(599), errorMessage.getErrorMessages().toString(), entry);
                entry.setStatus(QueueStatus.ERROR.ordinal());
                entry.setUpdateDate(new Date());
                break;
            }
            case 3: {
                this.getQueueLogService().createQueueLog(Integer.valueOf(204), "Perform task. Message type: " + MessageType.values()[dto.getMsgType()].toString() + ". Transition: " + workflowValue + " (not executed, transition not found in mapping)", entry);
                entry.setStatus(QueueStatus.DONE.ordinal());
                entry.setUpdateDate(new Date());
                break;
            }
        }
        this.getQueueInService().update(entry);
    }
    
    private ErrorCollection executeTransitionRestApi(final Connection connection, final IssueIntDTO dto, final int actionId, final String resolution) {
        final String uri = "/issue/" + dto.getIssueId() + "/transitions";
        String json;
        if (resolution != null) {
            json = this.getMessageComposerService().buildJSONTransition("" + actionId, resolution);
        }
        else {
            json = this.getMessageComposerService().buildJSONTransition("" + actionId);
        }
        final Response response = this.getCommunicator().callInternalRest(connection, CommunicatorServiceImpl.HttpRequestMethod.POST, uri, json);
        final SimpleErrorCollection errors = new SimpleErrorCollection();
        if (response.getStatus() >= 300) {
            errors.addErrorMessage(response.getJson());
        }
        return (ErrorCollection)errors;
    }
    
    private ErrorCollection executeTransition(final Issue issue, final ApplicationUser user, final int actionId, final String resolution) {
        try {
            final IssueInputParameters issueInputParameters = (IssueInputParameters)new IssueInputParametersImpl();
            if (resolution != null) {
                final ResolutionManager resolutionMenager = (ResolutionManager)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ResolutionManager.class);
                final List<Resolution> resolutions = (List<Resolution>)resolutionMenager.getResolutions();
                String id = null;
                for (final Resolution r : resolutions) {
                    if (r.getName().equals(resolution)) {
                        id = r.getId();
                        break;
                    }
                }
                if (id == null) {
                    final Resolution r2 = resolutionMenager.createResolution(resolution, "");
                    id = r2.getId();
                }
                issueInputParameters.setResolutionId(id);
            }
            final TransitionOptions.Builder builder = new TransitionOptions.Builder();
            final TransitionOptions transitionOptions = builder.skipConditions().build();
            final IssueService issueService = ComponentAccessor.getIssueService();
            final IssueService.TransitionValidationResult validationResult = issueService.validateTransition(user, issue.getId(), actionId, issueInputParameters, transitionOptions);
            if (validationResult.isValid()) {
                issueService.transition(user, validationResult);
            }
            return validationResult.getErrorCollection();
        }
        catch (Exception e) {
            final SimpleErrorCollection errors = new SimpleErrorCollection();
            errors.addErrorMessage(e.getMessage());
            return (ErrorCollection)errors;
        }
    }
    
    private void cleanupAttachmentTemporary(final List<AttachmentDTO> attachments) {
        for (final AttachmentDTO adto : attachments) {
            if (adto.getFile() != null && adto.getFile().exists() && !adto.getFile().delete()) {
                this.log.error(ExtendedLoggerMessageType.JOB, "Unable to remove synchronized attachment from temp: " + adto.getFilename());
            }
        }
    }
    
    private JIRAResponse sentCommentResponse(final Contract contract, final Comment comment, final QueueIn entry) {
        final JIRAResponse respObj = new JIRAResponse();
        respObj.setId(comment.getIssueId() + "");
        final String jsonMsg = this.getMessageComposerService().buildResponseInternalJSON(contract, respObj, comment);
        final QueueOut out = this.getQueueOutService().create(contract, MessageType.RESPONSE_COMMENT, QueueStatus.NEW, jsonMsg, Long.valueOf(Long.parseLong(respObj.getId())), entry.getMatchQueueId());
        this.getQueueLogService().createOutLog(out);
        return respObj;
    }
    
    private JIRAResponse sentResponse(final Contract contract, final Long issueId, final QueueIn entry) {
        return this.sentResponse(contract, issueId, entry, null);
    }
    
    private JIRAResponse sentResponse(final Contract contract, final Long issueId, final QueueIn entry, final Integer status) {
        final JIRAResponse respObj = new JIRAResponse();
        if (issueId != null) {
            respObj.setId(issueId.toString());
        }
        return this.sentResponse(contract, respObj, entry, status);
    }
    
    private JIRAResponse sentResponse(final Contract contract, final Response response, final QueueIn entry) {
        final JIRAResponse respObj = this.getMessageComposerService().parseJIRAResponse(response);
        return this.sentResponse(contract, respObj, entry);
    }
    
    private JIRAResponse sentAttachmentResponse(final Contract contract, final Long issueId, final Response response, final QueueIn entry) {
        final List<JIRAAttachmentResponse> responses = this.getMessageComposerService().parse(response);
        final JIRAResponse respObj = new JIRAResponse();
        if (issueId != null) {
            respObj.setId(issueId.toString());
        }
        respObj.setAttachments(responses);
        return this.sentResponse(contract, respObj, entry);
    }
    
    private JIRAResponse sentResponse(final Contract contract, final JIRAResponse respObj, final QueueIn entry) {
        return this.sentResponse(contract, respObj, entry, null);
    }
    
    private JIRAResponse sentResponse(final Contract contract, final JIRAResponse respObj, final QueueIn entry, final Integer status) {
        if (respObj != null && respObj.getId() != null) {
            final String jsonMsg = this.getMessageComposerService().buildResponseInternalJSON(contract, respObj, entry.getID(), status);
            final QueueOut out = this.getQueueOutService().create(contract, MessageType.RESPONSE, QueueStatus.NEW, jsonMsg, Long.valueOf(Long.parseLong(respObj.getId())), entry.getMatchQueueId());
            this.getQueueLogService().createOutLog(out);
        }
        return respObj;
    }
    
    @Override
    public void doExecute(final Map<String, Object> jobDataMap) {
        synchronized (IncomingTask.class) {
            this.log.info(ExtendedLoggerMessageType.JOB, "syn004", jobDataMap.containsKey("FORCED") ? "(FORCED)" : "(SCHEDULED)");
            final QueueInService qis = this.getQueueInService();
            if (qis == null) {
                return;
            }
            final List<QueueIn> newItems = qis.getAllByStatusAndMsgTypeNull(QueueStatus.NEW);
            final List<QueueIn> retryCreateItems = qis.getAllBy(MessageType.CREATE, QueueStatus.RETRY);
            final List<QueueIn> retryUpdateItems = qis.getAllBy(MessageType.UPDATE, QueueStatus.RETRY);
            final List<QueueIn> commentItems = qis.getAllBy(MessageType.COMMENT, QueueStatus.RETRY);
            final List<QueueIn> attachmentRetry = qis.getAllBy(MessageType.ATTACHMENT, QueueStatus.RETRY);
            final List<QueueIn> workflowRetry = qis.getAllBy(MessageType.WORKFLOW, QueueStatus.RETRY);
            final List<QueueIn> worklogRetry = qis.getAllBy(MessageType.WORKLOG, QueueStatus.RETRY);
            this.process(newItems);
            this.process(retryCreateItems);
            this.process(retryUpdateItems);
            this.process(commentItems);
            this.process(attachmentRetry);
            this.process(workflowRetry);
            this.process(worklogRetry);
        }
    }
    
    private class SyncIssueTransactionalObject
    {
        private int contractId;
        private Long issueId;
        private Long remoteIssueId;
        private String remoteIssueKey;
        
        public SyncIssueTransactionalObject(final int contractId, final Long issueId, final Long remoteIssueId, final String remoteIssueKey) {
            this.contractId = contractId;
            this.issueId = issueId;
            this.remoteIssueId = remoteIssueId;
            this.remoteIssueKey = remoteIssueKey;
        }
    }
    
    private class QueueOutTransactionalObject
    {
        Contract contract;
        MessageType msgType;
        QueueStatus status;
        String jsonMsg;
        Long issueId;
        Integer matchQueueEntryId;
        
        public QueueOutTransactionalObject(final Contract contract, final MessageType msgType, final QueueStatus status, final String jsonMsg, final Long issueId, final Integer matchQueueEntryId) {
            this.contract = contract;
            this.msgType = msgType;
            this.status = status;
            this.jsonMsg = jsonMsg;
            this.issueId = issueId;
            this.matchQueueEntryId = matchQueueEntryId;
        }
    }
}
