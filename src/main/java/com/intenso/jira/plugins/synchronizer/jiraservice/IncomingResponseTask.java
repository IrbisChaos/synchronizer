// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.service.comm.JIRAAttachmentResponse;
import com.atlassian.jira.issue.IssueManager;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexingService;
import com.atlassian.jira.component.ComponentAccessor;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.sql.Timestamp;
import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import org.boon.Boon;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutResponseDTO;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import org.apache.commons.lang3.StringEscapeUtils;
import com.intenso.jira.plugins.synchronizer.service.comm.IssueIntDTO;
import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;

public class IncomingResponseTask extends AbstractTask
{
    private void updateQueueOutProcessing(final QueueOut matchQueueOut, final Long remoteIssueId) {
        final List<QueueOut> waitingList = this.getQueueOutService().findBy(QueueStatus.PROCESSING, MessageType.UPDATE, matchQueueOut.getIssueId(), matchQueueOut.getContractId());
        for (final QueueOut entry : waitingList) {
            entry.setStatus(QueueStatus.NEW.ordinal());
            final IssueIntDTO dto = this.getMessageComposerService().toIssueIntDTO(entry.getJsonMsg());
            dto.setIssueId(remoteIssueId);
            entry.setJsonMsg(this.getMessageComposerService().toJSONString(dto));
            this.getQueueOutService().update(entry);
        }
    }
    
    private void updateQueueOutAttachmentProcessing(final QueueOut matchQueueOut, final Long remoteIssueId) {
        final List<QueueOut> waitingList = this.getQueueOutService().findBy(QueueStatus.PROCESSING, MessageType.ATTACHMENT, matchQueueOut.getIssueId(), matchQueueOut.getContractId());
        for (final QueueOut entry : waitingList) {
            entry.setStatus(QueueStatus.NEW.ordinal());
            String tmp = entry.getJsonMsg().trim().substring(0, entry.getJsonMsg().length() - 1);
            tmp = tmp + ",\"issue\":" + remoteIssueId + "}";
            entry.setJsonMsg(tmp);
            this.getQueueOutService().update(entry);
        }
    }
    
    private void updateQueueOutCommentProcessing(final QueueOut matchQueueOut, final Long remoteIssueId) {
        final List<QueueOut> waitingList = this.getQueueOutService().findBy(QueueStatus.PROCESSING, MessageType.COMMENT, matchQueueOut.getIssueId(), matchQueueOut.getContractId());
        for (final QueueOut entry : waitingList) {
            entry.setStatus(QueueStatus.NEW.ordinal());
            final IssueIntDTO dto = this.getMessageComposerService().toIssueIntDTO(entry.getJsonMsg());
            dto.getComment().setComment(StringEscapeUtils.escapeJson(dto.getComment().getComment()));
            dto.setRemoteIssueId(remoteIssueId);
            entry.setJsonMsg(this.getMessageComposerService().toJSONString(dto));
            this.getQueueOutService().update(entry);
        }
    }
    
    private void updateQueueOutWorkflowProcessing(final QueueOut matchQueueOut, final Long remoteIssueId) {
        final List<QueueOut> waitingList = this.getQueueOutService().findBy(QueueStatus.PROCESSING, MessageType.WORKFLOW, matchQueueOut.getIssueId(), matchQueueOut.getContractId());
        for (final QueueOut entry : waitingList) {
            entry.setStatus(QueueStatus.NEW.ordinal());
            final IssueIntDTO dto = this.getMessageComposerService().toIssueIntDTO(entry.getJsonMsg());
            dto.setIssueId(remoteIssueId);
            entry.setJsonMsg(this.getMessageComposerService().toJSONString(dto));
            this.getQueueOutService().update(entry);
        }
    }
    
    private void handleCommentResponse(final QueueIn queueIn) {
        final QueueOutResponseDTO response = Boon.fromJson(queueIn.getJsonMsg(), QueueOutResponseDTO.class);
        final Comment comment = (response.getCommentId() == null) ? null : this.getCommentService().get(response.getCommentId());
        if (comment == null) {
            this.log.error(ExtendedLoggerMessageType.JOB, "syn024", response.getCommentId().toString());
            queueIn.setStatus(QueueStatus.ERROR.ordinal());
            this.getQueueInService().update(queueIn);
            return;
        }
        comment.setDateExternal(response.getRemoteCommentDate());
        comment.setRemoteCommentId(response.getRemoteCommentId());
        comment.save();
        if (response.getResponseState() != null && !response.getResponseState().isSuccessful()) {
            queueIn.setStatus(QueueStatus.ERROR.ordinal());
        }
        else {
            queueIn.setStatus(QueueStatus.DONE.ordinal());
            if (comment.getBuildInCommentId() != null) {
                this.updateBuildInComment(comment.getBuildInCommentId(), comment.getDateExternal());
            }
        }
        queueIn.setIssueId(comment.getIssueId());
        if (comment.getIssueId() != null) {
            final Issue issue = (Issue)this.getIssueManager().getIssueObject(comment.getIssueId());
            if (issue != null) {
                queueIn.setIssueKey(issue.getKey());
            }
            else {
                this.log.error(ExtendedLoggerMessageType.JOB, "syn025", comment.getIssueId().toString());
            }
        }
        queueIn.setContractId(comment.getContractId());
        this.getQueueInService().update(queueIn);
        final QueueOut matchQueueOut = this.getQueueOutService().find(queueIn.getMatchQueueId());
        if (matchQueueOut != null) {
            if (queueIn.getStatus().equals(QueueStatus.DONE.ordinal())) {
                matchQueueOut.setStatus(QueueStatus.DONE.ordinal());
            }
            else {
                matchQueueOut.setStatus(QueueStatus.ERROR.ordinal());
            }
            this.getQueueOutService().update(matchQueueOut);
        }
        else {
            this.log.error(ExtendedLoggerMessageType.JOB, "syn026", String.valueOf(queueIn.getID()), queueIn.getMatchQueueId().toString());
        }
        this.getQueueLogService().createQueueLog(Integer.valueOf(200), "Response processing successful: local Issue: " + comment.getIssueId() + ", local issue key:  " + queueIn.getIssueKey() + ", Contract id: " + comment.getContractId(), queueIn);
    }
    
    private void updateBuildInComment(final Long buildInCommentId, final Timestamp dateExternal) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(dateExternal.getTime());
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy hh:mm aaa", Locale.US);
        final String acknowledgeDate = sdf.format(calendar.getTime());
        this.getBuildInCommentService().updateBuildComment(buildInCommentId, acknowledgeDate);
    }
    
    @Override
    public void doExecute(final Map<String, Object> jobDataMap) {
        synchronized (IncomingResponseTask.class) {
            this.log.info(ExtendedLoggerMessageType.JOB, "syn007", jobDataMap.containsKey("FORCED") ? "(FORCED)" : "(SCHEDULED)");
            if (this.getQueueInService() == null || this.getQueueOutService() == null) {
                return;
            }
            final List<QueueIn> responses = this.getQueueInService().getAllResponses();
            this.log.debug(ExtendedLoggerMessageType.JOB, "syn027", (responses == null) ? "" : String.valueOf(responses.size()));
            for (final QueueIn queueIn : responses) {
                if (queueIn.getMsgType() != null && queueIn.getMsgType().equals(MessageType.RESPONSE_COMMENT.ordinal())) {
                    this.handleCommentResponse(queueIn);
                }
                else if (queueIn.getMsgType() != null && queueIn.getMsgType().equals(MessageType.IN_RESPONSE.ordinal())) {
                    this.handleInResponse(queueIn);
                }
                else {
                    this.handleIssueAndAttachmentResponse(queueIn);
                }
            }
        }
    }
    
    private void handleInResponse(final QueueIn queueIn) {
        if (queueIn.getJsonMsg() != null) {
            final QueueOutResponseDTO response = Boon.fromJson(queueIn.getJsonMsg(), QueueOutResponseDTO.class);
            final QueueIn matchQueueIn = this.getQueueInService().find(queueIn.getMatchQueueId());
            if (matchQueueIn != null) {
                response.setIssueId(matchQueueIn.getIssueId());
                SyncIssue si;
                if (matchQueueIn.getMsgType() == MessageType.CREATE.ordinal()) {
                    si = this.getSynchronizedIssuesService().update(response);
                    final IssueManager issueManager = ComponentAccessor.getIssueManager();
                    final Issue issue = (Issue)issueManager.getIssueObject(si.getIssueId());
                    final IssueIndexingService issueIndexManager = (IssueIndexingService)ComponentAccessor.getComponent((Class)IssueIndexingService.class);
                    try {
                        issueIndexManager.reIndex(issue);
                    }
                    catch (IndexException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    si = this.getSynchronizedIssuesService().findByContractSingleResult(matchQueueIn.getContractId(), matchQueueIn.getIssueId());
                }
                if (si == null) {
                    queueIn.setStatus(QueueStatus.ERROR.ordinal());
                    this.getQueueInService().update(queueIn);
                    final String errorMessage = "Error processing response: remote Issue: " + response.getRemoteIssueKey() + ", Contract id: " + response.getContractName();
                    this.log.error(ExtendedLoggerMessageType.JOB, "syn028", errorMessage);
                    this.getQueueLogService().createQueueLog(Integer.valueOf(500), errorMessage, queueIn);
                }
                else {
                    if (response.getResponseState() != null && !response.getResponseState().isSuccessful()) {
                        queueIn.setStatus(QueueStatus.ERROR.ordinal());
                    }
                    else {
                        queueIn.setStatus(QueueStatus.DONE.ordinal());
                    }
                    queueIn.setIssueId(si.getIssueId());
                    if (si.getIssueId() != null) {
                        final Issue iss = (Issue)this.getIssueManager().getIssueObject(si.getIssueId());
                        if (iss != null) {
                            queueIn.setIssueKey(iss.getKey());
                        }
                    }
                    queueIn.setContractId(si.getContractId());
                    this.getQueueInService().update(queueIn);
                    final String infoMessage = "Response processing successful: local Issue: " + si.getIssueId() + ", remote Issue: " + si.getRemoteIssueKey() + ", Contract id: " + si.getContractId();
                    this.log.debug(ExtendedLoggerMessageType.JOB, "syn029", infoMessage);
                    this.getQueueLogService().createQueueLog(Integer.valueOf(200), infoMessage, queueIn);
                    if (response.getStatus() != null) {
                        if (response.getStatus() < QueueStatus.values().length) {
                            matchQueueIn.setStatus(response.getStatus());
                        }
                        else {
                            matchQueueIn.setStatus(QueueStatus.DONE.ordinal());
                        }
                    }
                    else {
                        matchQueueIn.setStatus(QueueStatus.DONE.ordinal());
                    }
                    if (response.getMatchQueueId() != null) {
                        matchQueueIn.setMatchQueueId(response.getMatchQueueId());
                    }
                    this.getQueueInService().update(matchQueueIn);
                }
            }
            else {
                queueIn.setStatus(QueueStatus.ERROR.ordinal());
                this.getQueueInService().update(queueIn);
                final String errorMessage2 = "Error processing response: remote Issue: " + response.getRemoteIssueKey() + ", Contract id: " + response.getContractName();
                this.log.error(ExtendedLoggerMessageType.JOB, "syn030", errorMessage2);
                this.getQueueLogService().createQueueLog(Integer.valueOf(409), errorMessage2, queueIn);
            }
        }
    }
    
    private void handleIssueAndAttachmentResponse(final QueueIn queueIn) {
        if (queueIn.getJsonMsg() != null) {
            final QueueOutResponseDTO response = Boon.fromJson(queueIn.getJsonMsg(), QueueOutResponseDTO.class);
            final QueueOut matchQueueOut = this.getQueueOutService().find(queueIn.getMatchQueueId());
            if (matchQueueOut != null) {
                response.setIssueId(matchQueueOut.getIssueId());
                SyncIssue si;
                if (matchQueueOut.getMsgType() == MessageType.CREATE.ordinal()) {
                    if (response.getResponseState() != null && !response.getResponseState().isSuccessful()) {
                        this.getSynchronizedIssuesService().delete(matchQueueOut.getContractId(), matchQueueOut.getIssueId());
                        si = null;
                    }
                    else {
                        si = this.getSynchronizedIssuesService().update(response);
                        if (si != null) {
                            final IssueManager issueManager = ComponentAccessor.getIssueManager();
                            final Issue issue = (Issue)issueManager.getIssueObject(si.getIssueId());
                            final IssueIndexingService issueIndexManager = (IssueIndexingService)ComponentAccessor.getComponent((Class)IssueIndexingService.class);
                            try {
                                issueIndexManager.reIndex(issue);
                            }
                            catch (IndexException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    si = this.getSynchronizedIssuesService().findByContractSingleResult(matchQueueOut.getContractId(), matchQueueOut.getIssueId());
                }
                if (si == null) {
                    queueIn.setIssueId(matchQueueOut.getIssueId());
                    queueIn.setContractId(matchQueueOut.getContractId());
                    queueIn.setStatus(QueueStatus.ERROR.ordinal());
                    this.getQueueInService().update(queueIn);
                    matchQueueOut.setStatus(QueueStatus.ERROR.ordinal());
                    this.getQueueOutService().update(matchQueueOut);
                    final String errorMessage = "Error processing response: remote Issue: " + response.getRemoteIssueKey() + ", Contract id: " + response.getContractName();
                    this.log.error(ExtendedLoggerMessageType.JOB, "syn028", errorMessage);
                    this.getQueueLogService().createQueueLog(Integer.valueOf(500), errorMessage, queueIn);
                }
                else {
                    if (response.getResponseState() != null && !response.getResponseState().isSuccessful()) {
                        queueIn.setStatus(QueueStatus.ERROR.ordinal());
                    }
                    else {
                        queueIn.setStatus(QueueStatus.DONE.ordinal());
                    }
                    queueIn.setIssueId(si.getIssueId());
                    if (si.getIssueId() != null) {
                        final Issue iss = (Issue)this.getIssueManager().getIssueObject(si.getIssueId());
                        if (iss != null) {
                            queueIn.setIssueKey(iss.getKey());
                        }
                    }
                    queueIn.setContractId(si.getContractId());
                    this.getQueueInService().update(queueIn);
                    final String infoMessage = "Response processing successful: local Issue: " + si.getIssueId() + ", remote Issue: " + si.getRemoteIssueKey() + ", Contract id: " + si.getContractId();
                    this.log.debug(ExtendedLoggerMessageType.JOB, "syn029", infoMessage);
                    this.getQueueLogService().createQueueLog(Integer.valueOf(200), infoMessage, queueIn);
                    final JIRAAttachmentResponse[] synchronizedAttachments = response.getAttachments();
                    if (synchronizedAttachments != null) {
                        for (int i = 0; i < synchronizedAttachments.length; ++i) {
                            final JIRAAttachmentResponse att = synchronizedAttachments[i];
                            if (att != null) {
                                String localAttachmentId = att.getFilename();
                                final Issue issue2 = (Issue)this.getIssueManager().getIssueObject(si.getIssueId());
                                if (issue2 != null) {
                                    final String key = issue2.getKey();
                                    if (key != null) {
                                        final String tmp = att.getFilename().trim().replaceAll(key, "").trim();
                                        if (tmp.length() > 1 && tmp.contains(")")) {
                                            localAttachmentId = tmp.substring(1, tmp.indexOf(")"));
                                        }
                                    }
                                }
                                this.getSynchronizedAttachmentService().save(si.getID(), localAttachmentId, att.getId(), Boon.toJson(synchronizedAttachments));
                            }
                        }
                    }
                    if (response.getStatus() != null) {
                        if (response.getStatus() < QueueStatus.values().length) {
                            matchQueueOut.setStatus(response.getStatus());
                        }
                        else {
                            matchQueueOut.setStatus(QueueStatus.DONE.ordinal());
                        }
                    }
                    else {
                        matchQueueOut.setStatus(QueueStatus.DONE.ordinal());
                    }
                    if (response.getMatchQueueId() != null) {
                        matchQueueOut.setMatchQueueId(response.getMatchQueueId());
                    }
                    this.getQueueOutService().update(matchQueueOut);
                    this.updateQueueOutProcessing(matchQueueOut, si.getRemoteIssueId());
                    this.updateQueueOutCommentProcessing(matchQueueOut, si.getRemoteIssueId());
                    this.updateQueueOutAttachmentProcessing(matchQueueOut, si.getRemoteIssueId());
                    this.updateQueueOutWorkflowProcessing(matchQueueOut, si.getRemoteIssueId());
                }
            }
            else {
                queueIn.setStatus(QueueStatus.ERROR.ordinal());
                this.getQueueInService().update(queueIn);
                final String errorMessage2 = "Error processing response: remote Issue: " + response.getRemoteIssueKey() + ", Contract id: " + response.getContractName();
                this.log.error(ExtendedLoggerMessageType.JOB, "syn030", errorMessage2);
                this.getQueueLogService().createQueueLog(Integer.valueOf(409), errorMessage2, queueIn);
            }
        }
    }
}
