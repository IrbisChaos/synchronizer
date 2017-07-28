// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.QueueArchive;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.jiraservice.PullResponseTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.PullTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.IncomingResponseTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.IncomingTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.OutgoingResponseTask;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.jiraservice.TasksSchedulerImpl;
import com.google.common.collect.ImmutableMap;
import com.intenso.jira.plugins.synchronizer.jiraservice.OutgoingTask;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.JobStatisticService;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.StatisticService;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueArchiveService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import org.slf4j.Logger;

public class OtherPerformanceAction extends LicenseAwareAction
{
    private static final Logger log;
    private static final long serialVersionUID = -7649223419029636247L;
    private Integer incomingErrorCount;
    private Integer outgoingErrorCount;
    private QueueOutService queueOutService;
    private QueueInService queueInService;
    private QueueArchiveService queueArchiveService;
    private ContractService contractService;
    private ConnectionService connectionService;
    private SynchronizerConfigService synchronizerConfigService;
    private StatisticService statisticService;
    private List<String> archiveValue;
    private JobStatisticService jobStatisticService;
    private int[] jobIds;
    
    public OtherPerformanceAction(final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.jobStatisticService = (JobStatisticService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)JobStatisticService.class);
    }
    
    public boolean isOtherPerformanceOther() {
        return true;
    }
    
    public String doRetryIncoming() throws Exception {
        this.getQueueInService().updateAll(this.getQueueInService().findByStatus(QueueStatus.ERROR), QueueStatus.RETRY);
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return "success";
    }
    
    public String doRetryOutgoing() throws Exception {
        this.getQueueOutService().updateAll(this.getQueueOutService().findByStatus(QueueStatus.ERROR), QueueStatus.RETRY);
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return "success";
    }
    
    public String doRemoveIncoming() throws Exception {
        this.getQueueInService().deleteAll(this.getQueueInService().findByStatus(QueueStatus.ERROR));
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return "success";
    }
    
    public String doRemoveOutgoing() throws Exception {
        this.getQueueOutService().deleteAll(this.getQueueOutService().findByStatus(QueueStatus.ERROR));
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return "success";
    }
    
    @Override
    public String doDefault() throws Exception {
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return super.doDefault();
    }
    
    public String doMove() throws Exception {
        this.getQueueArchiveService().archiveAllFromQueueIn();
        this.getQueueArchiveService().archiveAllFromQueueOut();
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return "success";
    }
    
    public String doPurge() throws Exception {
        this.getQueueArchiveService().purge();
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return "success";
    }
    
    public String doRun() {
        this.run();
        this.outgoingErrorCount = this.getQueueOutService().findByStatus(QueueStatus.ERROR).size();
        this.incomingErrorCount = this.getQueueInService().findByStatus(QueueStatus.ERROR).size();
        return "success";
    }
    
    private void run() {
        for (final int jobId : this.jobIds) {
            if (jobId == 0) {
                new OutgoingTask().doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.OUTGOING_JOB_NAME).build());
            }
            else if (jobId == 1) {
                new OutgoingResponseTask().doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.OUTGOING_RESPONSE_JOB_NAME).build());
            }
            else if (jobId == 2) {
                new IncomingTask().doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.INCOMING_JOB_NAME).build());
            }
            else if (jobId == 3) {
                new IncomingResponseTask().doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.INCOMING_RESPONSE_JOB_NAME).build());
            }
            else if (jobId == 4) {
                new PullTask().doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.PULL_JOB_NAME).build());
            }
            else if (jobId == 5) {
                new PullResponseTask().doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.PULL_RESPONSE_JOB_NAME).build());
            }
        }
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
    
    protected QueueArchiveService getQueueArchiveService() {
        if (this.queueArchiveService == null) {
            this.queueArchiveService = (QueueArchiveService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueArchiveService.class);
        }
        return this.queueArchiveService;
    }
    
    protected ContractService getContractService() {
        if (this.contractService == null) {
            this.contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
        }
        return this.contractService;
    }
    
    protected ConnectionService getConnectionService() {
        if (this.connectionService == null) {
            this.connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        }
        return this.connectionService;
    }
    
    protected SynchronizerConfigService getSynchronizerConfigService() {
        if (this.synchronizerConfigService == null) {
            this.synchronizerConfigService = (SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class);
        }
        return this.synchronizerConfigService;
    }
    
    protected StatisticService getStatisticService() {
        if (this.statisticService == null) {
            this.statisticService = (StatisticService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)StatisticService.class);
        }
        return this.statisticService;
    }
    
    public List<String> getJobs() {
        final List<String> result = new ArrayList<String>();
        final Date now = new Date();
        result.add("Outgoing (" + this.getSynchronizerConfigService().getConfig().getCronOutgoingJob() + ")" + this.getJobLastRunDate(TasksSchedulerImpl.OUTGOING_JOB_NAME, now));
        result.add("Outgoing Response (" + this.getSynchronizerConfigService().getConfig().getCronOutgoingResponseJob() + ")" + this.getJobLastRunDate(TasksSchedulerImpl.OUTGOING_RESPONSE_JOB_NAME, now));
        result.add("Incoming (" + this.getSynchronizerConfigService().getConfig().getCronIncomingJob() + ")" + this.getJobLastRunDate(TasksSchedulerImpl.INCOMING_JOB_NAME, now));
        result.add("Incoming Response (" + this.getSynchronizerConfigService().getConfig().getCronIncomingResponseJob() + ")" + this.getJobLastRunDate(TasksSchedulerImpl.INCOMING_RESPONSE_JOB_NAME, now));
        result.add("Pull (" + this.getSynchronizerConfigService().getConfig().getCronPullJob() + ")" + this.getJobLastRunDate(TasksSchedulerImpl.PULL_JOB_NAME, now));
        result.add("Pull Response (" + this.getSynchronizerConfigService().getConfig().getCronPullResponsesJob() + ")" + this.getJobLastRunDate(TasksSchedulerImpl.PULL_RESPONSE_JOB_NAME, now));
        return result;
    }
    
    private String getJobLastRunDate(final String jobName, final Date now) {
        final Timestamp lastRunDate = this.jobStatisticService.getLastRunDate(jobName);
        if (lastRunDate != null) {
            return " last scheduled run " + this.secondsBetween(now, lastRunDate) + "s ago";
        }
        return " job not executed";
    }
    
    private Long secondsBetween(final Date first, final Date second) {
        return (first.getTime() - second.getTime()) / 1000L;
    }
    
    public String getQueues() {
        String result = "";
        final List<QueueIn> allIn = this.getQueueInService().getAll();
        final List<QueueOut> allOut = this.getQueueOutService().getAll();
        final JSONObject in = new JSONObject();
        final JSONObject out = new JSONObject();
        final JSONArray array = new JSONArray();
        try {
            in.append("queue-type", (Object)"Incoming");
            in.append("new", (Object)this.getQueueInCountByStatus(allIn, QueueStatus.NEW));
            in.append("processing", (Object)this.getQueueInCountByStatus(allIn, QueueStatus.PROCESSING));
            in.append("cancelled", (Object)this.getQueueInCountByStatus(allIn, QueueStatus.CANCELLED));
            in.append("retry", (Object)this.getQueueInCountByStatus(allIn, QueueStatus.RETRY));
            in.append("done", (Object)this.getQueueInCountByStatus(allIn, QueueStatus.DONE));
            in.append("error", (Object)this.getQueueInCountByStatus(allIn, QueueStatus.ERROR));
            out.append("queue-type", (Object)"Outgoing");
            out.append("new", (Object)this.getQueueOutCountByStatus(allOut, QueueStatus.NEW));
            out.append("processing", (Object)this.getQueueInCountByStatus(allIn, QueueStatus.PROCESSING));
            out.append("sent", (Object)this.getQueueOutCountByStatus(allOut, QueueStatus.SENT));
            out.append("cancelled", (Object)this.getQueueOutCountByStatus(allOut, QueueStatus.CANCELLED));
            out.append("retry", (Object)this.getQueueOutCountByStatus(allOut, QueueStatus.RETRY));
            out.append("done", (Object)this.getQueueOutCountByStatus(allOut, QueueStatus.DONE));
            out.append("error", (Object)this.getQueueOutCountByStatus(allOut, QueueStatus.ERROR));
            out.append("error_remote", (Object)this.getQueueOutCountByStatus(allOut, QueueStatus.ERROR_REMOTE));
            array.put((Object)in);
            array.put((Object)out);
            result = array.toString();
        }
        catch (JSONException e) {
            OtherPerformanceAction.log.error(e.getMessage());
        }
        return result;
    }
    
    public List<String> getArchiveValue() {
        return this.archiveValue;
    }
    
    public String getArchive() {
        String result = "";
        final List<QueueIn> allIn = this.getQueueInService().getAll();
        final List<QueueOut> allOut = this.getQueueOutService().getAll();
        final List<QueueArchive> allArchive = this.getQueueArchiveService().getAll();
        final JSONObject object = new JSONObject();
        final JSONArray array = new JSONArray();
        try {
            final int archive = allArchive.size();
            final int queues = this.getQueueInCountByStatus(allIn, QueueStatus.DONE) + this.getQueueOutCountByStatus(allOut, QueueStatus.DONE);
            final int archivemax = archive + queues;
            (this.archiveValue = new ArrayList<String>()).add(Integer.toString(archive));
            this.archiveValue.add(Integer.toString(queues));
            int x = 50;
            int y = 50;
            if (archivemax != 0) {
                x = archive * 98 / archivemax;
                ++x;
                y = 100 - x;
            }
            object.put("name", (Object)"Archived : Queues Done");
            object.put("archive", x);
            object.put("queues", y);
            array.put((Object)object);
            result = array.toString();
        }
        catch (JSONException e) {
            OtherPerformanceAction.log.error(e.getMessage());
        }
        return result;
    }
    
    public String getColorForNotFinishedStatuses() {
        return "#4a6785";
    }
    
    public String getColorForErrorStatus() {
        return "#d04437";
    }
    
    public String getColorForDoneStatus() {
        return "#14892c";
    }
    
    public String getColorForSentStatus() {
        return "#f6c342";
    }
    
    public String getColorForCancelledStatus() {
        return "#ccc";
    }
    
    public String getColorForArchived() {
        return "#815b3a";
    }
    
    private Integer getQueueInCountByStatus(final List<QueueIn> allIn, final QueueStatus status) {
        Integer result = 0;
        for (final QueueIn queueIn : allIn) {
            if (queueIn.getStatus().equals(status.ordinal())) {
                ++result;
            }
        }
        return result;
    }
    
    private Integer getQueueOutCountByStatus(final List<QueueOut> allOut, final QueueStatus status) {
        Integer result = 0;
        for (final QueueOut queueOut : allOut) {
            if (queueOut.getStatus().equals(status.ordinal())) {
                ++result;
            }
        }
        return result;
    }
    
    public Integer getIncomingErrorCount() {
        return this.incomingErrorCount;
    }
    
    public void setIncomingErrorCount(final Integer incomingErrorCount) {
        this.incomingErrorCount = incomingErrorCount;
    }
    
    public Integer getOutgoingErrorCount() {
        return this.outgoingErrorCount;
    }
    
    public void setOutgoingErrorCount(final Integer outgoingErrorCount) {
        this.outgoingErrorCount = outgoingErrorCount;
    }
    
    public void setJobsArray(final int[] jobsArray) {
        this.jobIds = jobsArray;
    }
    
    public int[] getJobsArray() {
        return this.jobIds;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)OtherPerformanceAction.class);
    }
}
