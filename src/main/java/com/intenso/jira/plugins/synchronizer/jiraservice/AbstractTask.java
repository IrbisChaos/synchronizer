// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import org.quartz.JobExecutionException;
import org.quartz.JobExecutionContext;
import org.quartz.CronExpression;
import java.util.Date;
import org.quartz.Calendar;
import java.text.ParseException;
import org.quartz.CronTrigger;
import java.io.Serializable;
import java.util.HashMap;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.JobRunnerRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.google.gson.JsonParser;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.service.comm.Response;
import com.intenso.jira.plugins.synchronizer.service.comm.Bundle;
import java.util.Iterator;
import org.boon.Boon;
import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.ArrayList;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileParser;
import org.codehaus.jackson.smile.SmileFactory;
import net.java.ao.Entity;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import java.util.List;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.atlassian.jira.config.IssueTypeManager;
import com.intenso.jira.plugins.synchronizer.service.JobStatisticService;
import com.intenso.jira.plugins.synchronizer.service.StatisticService;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.intenso.jira.plugins.synchronizer.service.NotificationService;
import com.intenso.jira.plugins.synchronizer.service.BuildInCommentService;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueArchiveService;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedAttachmentService;
import com.atlassian.jira.issue.IssueManager;
import com.intenso.jira.plugins.synchronizer.service.CommentService;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicationService;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorService;
import com.intenso.jira.plugins.synchronizer.service.comm.BundleQueueService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.MessageComposerService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;
import org.quartz.Job;
import com.atlassian.scheduler.JobRunner;

public abstract class AbstractTask implements JobRunner, Job
{
    public static final String IS_FORCED = "FORCED";
    private QueueLogService queueLogService;
    private MessageComposerService composerService;
    private ContractService contractService;
    private QueueOutService queueOutService;
    private QueueInService queueInService;
    private BundleQueueService bundleService;
    private CommunicatorService communicator;
    private ConnectionService connectionService;
    private SynchronizedIssuesService synchronizedIssuesService;
    private CommunicationService communicationService;
    private CommentService commentService;
    private IssueManager issueManager;
    private SynchronizedAttachmentService synchronizedAttachmentService;
    private IssueIndexManager issueIndexManager;
    private QueueArchiveService queueArchiveService;
    private SynchronizerConfigService synchronizerConfigService;
    private AttachmentPathManager attachmentPathManager;
    private ContractFieldMappingEntryService contractFieldMappingEntryService;
    private BuildInCommentService buildInCommentService;
    private NotificationService notificationService;
    private WorkflowSyncService workflowSyncService;
    private StatisticService statisticService;
    private final JobStatisticService jobStatisticService;
    protected final IssueTypeManager issueTypeManager;
    protected ExtendedLogger log;
    
    public AbstractTask() {
        this.log = ExtendedLoggerFactory.getLogger(this.getClass());
        this.jobStatisticService = (JobStatisticService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)JobStatisticService.class);
        this.issueTypeManager = (IssueTypeManager)ComponentAccessor.getComponent((Class)IssueTypeManager.class);
    }
    
    protected void process(final List<QueueOut> queue, final boolean isControlMsg, final boolean isCommentControlMsg, final boolean isINControlMsg) {
        try {
            final Map<Integer, List<QueueOut>> map = this.getBundleQueueService().splitQueueByConnection(queue);
            for (final Map.Entry<Integer, List<QueueOut>> entry : map.entrySet()) {
                try {
                    final Bundle bundle = this.getBundleQueueService().createBundle(entry.getKey(), entry.getValue(), isControlMsg || isCommentControlMsg || isINControlMsg);
                    final Connection connection = this.getConnectionService().find(entry.getKey());
                    final long startTime = System.nanoTime();
                    final Response response = this.getCommunicator().send(connection, bundle, isControlMsg, isCommentControlMsg, isINControlMsg);
                    final long processingTime = (System.nanoTime() - startTime) / 1000000L;
                    this.createStatistic(entry, processingTime);
                    this.log.debug(ExtendedLoggerMessageType.JOB, "syn031", response.getStatus().toString(), response.getJson());
                    if (response.getStatus() != 250) {
                        if (response.getStatus() < 300) {
                            this.getQueueOutService().updateAll(entry.getValue(), (isControlMsg || isCommentControlMsg || isINControlMsg) ? QueueStatus.DONE : QueueStatus.SENT);
                        }
                        else {
                            this.getQueueOutService().updateAll(entry.getValue(), QueueStatus.ERROR);
                            for (final QueueOut q : queue) {
                                this.getQueueLogService().createQueueLog(response, (Entity)q, q.getContractId(), q.getIssueId());
                            }
                        }
                    }
                    else {
                        final String description = response.getJson();
                        final SmileFactory f = new SmileFactory();
                        f.configure(SmileParser.Feature.REQUIRE_HEADER, false);
                        final ObjectMapper mapper = new ObjectMapper((JsonFactory)f);
                        try {
                            final List<IncomingLogT> log = (List<IncomingLogT>)mapper.readValue(description.getBytes("UTF-8"), (Class)ArrayList.class);
                            if (log == null) {
                                continue;
                            }
                            for (final QueueOut q2 : queue) {
                                boolean found = false;
                                for (final IncomingLogT logEntry : log) {
                                    final String json = logEntry.getMessage();
                                    QueueOut qo = null;
                                    try {
                                        qo = Boon.fromJson(json, QueueOut.class);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (qo != null && qo.getID() == q2.getID()) {
                                        Boolean error = false;
                                        if (logEntry.getError() != null || logEntry.getQueueIn() == null) {
                                            error = true;
                                        }
                                        this.getQueueLogService().createQueueLog(response.getStatus(), logEntry.getError(), q2);
                                        if (error) {
                                            this.getQueueOutService().update(qo.getID(), QueueStatus.ERROR);
                                            this.getQueueLogService().createQueueLog(response.getStatus(), logEntry.getError(), qo);
                                        }
                                        else {
                                            this.getQueueOutService().update(qo.getID(), QueueStatus.SENT);
                                        }
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    this.getQueueLogService().createQueueLog(response.getStatus(), "No information found in response about id: " + q2.getID(), q2);
                                }
                            }
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                catch (Exception et) {
                    et.printStackTrace();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void createStatistic(final Map.Entry<Integer, List<QueueOut>> entry, final long processingTime) {
        try {
            for (final QueueOut out : entry.getValue()) {
                Long attSize = 0L;
                if (out.getMsgType() != null && out.getMsgType().equals(MessageType.ATTACHMENT)) {
                    final JsonParser gson = new JsonParser();
                    final JsonObject je = gson.parse(out.getJsonMsg()).getAsJsonObject();
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
                this.getStatisticService().create(out.getConnectionId(), out.getConnectionId(), MessageType.getByOrdinal(out.getMsgType()), QueueType.OUT, processingTime, out.getJsonMsg().length() * 2, attSize);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public JobRunnerResponse runJob(final JobRunnerRequest request) {
        final Map<String, Serializable> serializableMap = (Map<String, Serializable>)request.getJobConfig().getParameters();
        final Map<String, Object> objectMap = new HashMap<String, Object>();
        for (final String key : serializableMap.keySet()) {
            objectMap.put(key, serializableMap.get(key));
        }
        this.execute(objectMap);
        return JobRunnerResponse.success();
    }
    
    public void execute(final Map<String, Object> jobDataMap) {
        try {
            final SynchronizerConfigService syncConfigService = this.getSynchronizerConfigService();
            if (syncConfigService == null) {
                this.log.warn(ExtendedLoggerMessageType.JOB, "syn032");
                return;
            }
            final Object jobName = jobDataMap.get("synchronizerJobName");
            final String cronExpr = syncConfigService.getCronByJobName((jobName == null) ? null : jobName.toString());
            if (cronExpr != null && !cronExpr.toString().isEmpty()) {
                final CronTrigger cronTrigger = new CronTrigger();
                try {
                    cronTrigger.setCronExpression(cronExpr.toString());
                }
                catch (ParseException e3) {
                    this.log.error(ExtendedLoggerMessageType.JOB, "syn033", cronExpr, jobName.toString());
                    return;
                }
                cronTrigger.setName(jobName.toString());
                final Long lastRun = this.getSynchronizerConfigService().getLastRun(jobName.toString());
                cronTrigger.triggered(null);
                if (lastRun != null) {
                    cronTrigger.setPreviousFireTime(new Date(lastRun));
                }
                try {
                    final CronExpression cronExpression = new CronExpression(cronTrigger.getCronExpression());
                    final Date nextTime = (lastRun == null) ? new Date() : cronExpression.getNextValidTimeAfter(new Date(lastRun));
                    if (nextTime.after(new Date())) {
                        return;
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.getSynchronizerConfigService().setLastRun(jobName.toString(), new Date().getTime());
            if (!jobDataMap.containsKey("FORCED")) {
                this.jobStatisticService.create(jobName.toString());
            }
            this.doExecute(jobDataMap);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public abstract void doExecute(final Map<String, Object> p0);
    
    protected StatisticService getStatisticService() {
        if (this.statisticService == null) {
            this.statisticService = (StatisticService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)StatisticService.class);
        }
        return this.statisticService;
    }
    
    protected CommunicationService getCommunicationService() {
        if (this.communicationService == null) {
            this.communicationService = (CommunicationService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)CommunicationService.class);
        }
        return this.communicationService;
    }
    
    protected MessageComposerService getMessageComposerService() {
        if (this.composerService == null) {
            this.composerService = (MessageComposerService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)MessageComposerService.class);
        }
        return this.composerService;
    }
    
    protected ContractService getContractService() {
        if (this.contractService == null) {
            this.contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
        }
        return this.contractService;
    }
    
    protected SynchronizedIssuesService getSynchronizedIssuesService() {
        if (this.synchronizedIssuesService == null) {
            this.synchronizedIssuesService = (SynchronizedIssuesService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizedIssuesService.class);
        }
        return this.synchronizedIssuesService;
    }
    
    protected QueueOutService getQueueOutService() {
        if (this.queueOutService == null) {
            this.queueOutService = (QueueOutService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueOutService.class);
        }
        return this.queueOutService;
    }
    
    protected QueueInService getQueueInService() {
        if (this.queueInService == null) {
            this.queueInService = (QueueInService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueInService.class);
        }
        return this.queueInService;
    }
    
    protected BundleQueueService getBundleQueueService() {
        if (this.bundleService == null) {
            this.bundleService = (BundleQueueService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)BundleQueueService.class);
        }
        return this.bundleService;
    }
    
    protected CommunicatorService getCommunicator() {
        if (this.communicator == null) {
            this.communicator = (CommunicatorService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)CommunicatorService.class);
        }
        return this.communicator;
    }
    
    protected ConnectionService getConnectionService() {
        if (this.connectionService == null) {
            this.connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        }
        return this.connectionService;
    }
    
    public QueueLogService getQueueLogService() {
        if (this.queueLogService == null) {
            this.queueLogService = (QueueLogService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueLogService.class);
        }
        return this.queueLogService;
    }
    
    public void setQueueLogService(final QueueLogService queueLogService) {
        this.queueLogService = queueLogService;
    }
    
    public CommentService getCommentService() {
        if (this.commentService == null) {
            this.commentService = (CommentService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)CommentService.class);
        }
        return this.commentService;
    }
    
    public void setCommentService(final CommentService commentService) {
        this.commentService = commentService;
    }
    
    public IssueManager getIssueManager() {
        if (this.issueManager == null) {
            this.issueManager = ComponentAccessor.getIssueManager();
        }
        return this.issueManager;
    }
    
    public SynchronizedAttachmentService getSynchronizedAttachmentService() {
        if (this.synchronizedAttachmentService == null) {
            this.synchronizedAttachmentService = (SynchronizedAttachmentService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizedAttachmentService.class);
        }
        return this.synchronizedAttachmentService;
    }
    
    public IssueIndexManager getIssueIndexManager() {
        if (this.issueIndexManager == null) {
            this.issueIndexManager = (IssueIndexManager)ComponentAccessor.getComponent((Class)IssueIndexManager.class);
        }
        return this.issueIndexManager;
    }
    
    public QueueArchiveService getQueueArchiveService() {
        if (this.queueArchiveService == null) {
            this.queueArchiveService = (QueueArchiveService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueArchiveService.class);
        }
        return this.queueArchiveService;
    }
    
    public ContractFieldMappingEntryService getContractFieldMappingEntryService() {
        if (this.contractFieldMappingEntryService == null) {
            this.contractFieldMappingEntryService = (ContractFieldMappingEntryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractFieldMappingEntryService.class);
        }
        return this.contractFieldMappingEntryService;
    }
    
    public void setQueueArchiveService(final QueueArchiveService queueArchiveService) {
        this.queueArchiveService = queueArchiveService;
    }
    
    public SynchronizerConfigService getSynchronizerConfigService() {
        if (this.synchronizerConfigService == null) {
            this.synchronizerConfigService = (SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class);
        }
        return this.synchronizerConfigService;
    }
    
    public AttachmentPathManager getAttachmentPathManager() {
        if (this.attachmentPathManager == null) {
            this.attachmentPathManager = ComponentAccessor.getAttachmentPathManager();
        }
        return this.attachmentPathManager;
    }
    
    public BuildInCommentService getBuildInCommentService() {
        if (this.buildInCommentService == null) {
            this.buildInCommentService = (BuildInCommentService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)BuildInCommentService.class);
        }
        return this.buildInCommentService;
    }
    
    public NotificationService getNotificationService() {
        if (this.notificationService == null) {
            this.notificationService = (NotificationService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)NotificationService.class);
        }
        return this.notificationService;
    }
    
    public WorkflowSyncService getWorkflowSyncService() {
        if (this.workflowSyncService == null) {
            this.workflowSyncService = (WorkflowSyncService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)WorkflowSyncService.class);
        }
        return this.workflowSyncService;
    }
    
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        this.doExecute(context.getMergedJobDataMap());
    }
}
