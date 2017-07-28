// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import org.apache.commons.lang.StringUtils;
import com.atlassian.scheduler.SchedulerServiceException;
import java.util.Map;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.config.JobConfig;
import java.io.Serializable;
import java.util.HashMap;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.atlassian.scheduler.JobRunner;
import java.util.Date;
import com.atlassian.scheduler.config.JobId;
import java.util.GregorianCalendar;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.atlassian.scheduler.SchedulerService;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import org.springframework.beans.factory.DisposableBean;
import com.atlassian.sal.api.lifecycle.LifecycleAware;

public class TasksSchedulerImpl implements TasksScheduler, LifecycleAware, DisposableBean
{
    private final ExtendedLogger log;
    private final long defaultInterval = 10000L;
    private final long archivizationInterval = 3600000L;
    public static final String SYNCHRONIZER_JOB_NAME = "synchronizerJobName";
    public static final String OUTGOING_JOB_NAME;
    public static final String OUTGOING_RESPONSE_JOB_NAME;
    public static final String INCOMING_JOB_NAME;
    public static final String INCOMING_RESPONSE_JOB_NAME;
    public static final String ARCHIVIZATION_JOB_NAME;
    public static final String PULL_JOB_NAME;
    public static final String PULL_RESPONSE_JOB_NAME;
    public static final String PULL_CONFIGURATION_JOB_NAME;
    public static final String ALERT_JOB_NAME;
    public static final String CLEAR_OLD_QUEUE_LOGS_JOB_NAME;
    private final SynchronizerConfigService synchronizerConfigService;
    private final SchedulerService schedulerService;
    
    public TasksSchedulerImpl(final SchedulerService schedulerService, final SynchronizerConfigService synchronizerConfigService) {
        this.log = ExtendedLoggerFactory.getLogger(TasksSchedulerImpl.class);
        this.schedulerService = schedulerService;
        this.synchronizerConfigService = synchronizerConfigService;
    }
    
    public void onStart() {
        this.rescheduleJobs(10000L);
        this.setDefaultCronConfiguration();
    }
    
    public void destroy() throws Exception {
        this.unscheduleJobs();
    }
    
    private void rescheduleJobs(final long interval) {
        this.unscheduleJobs();
        final GregorianCalendar gc = new GregorianCalendar();
        gc.add(14, (int)(Object)new Long(interval) * 3);
        this.reschedulePull(gc.getTime(), interval);
        this.reschedulePullResponse(gc.getTime(), interval);
        this.rescheduleOutgoing(gc.getTime(), interval);
        this.rescheduleOutgoingResponse(gc.getTime(), interval);
        this.rescheduleIncoming(gc.getTime(), interval);
        this.rescheduleIncomingResponse(gc.getTime(), interval);
        this.reschedulePullConfiguration(gc.getTime(), interval);
        this.rescheduleAlert(gc.getTime(), interval);
        this.rescheduleClearOldQueueLogs(gc.getTime(), interval);
        this.archivizationOfQueues(gc.getTime(), 3600000L);
    }
    
    private void unscheduleJobs() {
        try {
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.OUTGOING_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.OUTGOING_RESPONSE_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.INCOMING_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.INCOMING_RESPONSE_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.ARCHIVIZATION_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.PULL_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.PULL_RESPONSE_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.PULL_CONFIGURATION_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.ALERT_JOB_NAME));
            this.schedulerService.unscheduleJob(JobId.of(TasksSchedulerImpl.CLEAR_OLD_QUEUE_LOGS_JOB_NAME));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void archivizationOfQueues(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new ArchiveQueuesTask(), TasksSchedulerImpl.ARCHIVIZATION_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn016", String.valueOf(interval));
    }
    
    private void reschedulePull(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new PullTask(), TasksSchedulerImpl.PULL_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn017", String.valueOf(interval));
    }
    
    private void rescheduleOutgoingResponse(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new OutgoingResponseTask(), TasksSchedulerImpl.OUTGOING_RESPONSE_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn018", String.valueOf(interval));
    }
    
    private void rescheduleOutgoing(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new OutgoingTask(), TasksSchedulerImpl.OUTGOING_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn015", String.valueOf(interval));
    }
    
    private void rescheduleIncoming(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new IncomingTask(), TasksSchedulerImpl.INCOMING_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn019", String.valueOf(interval));
    }
    
    private void rescheduleIncomingResponse(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new IncomingResponseTask(), TasksSchedulerImpl.INCOMING_RESPONSE_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn020", String.valueOf(interval));
    }
    
    private void reschedulePullResponse(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new PullResponseTask(), TasksSchedulerImpl.PULL_RESPONSE_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn021", String.valueOf(interval));
    }
    
    private void reschedulePullConfiguration(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new ConfigurationPullTask(), TasksSchedulerImpl.PULL_CONFIGURATION_JOB_NAME, startTime, interval);
        this.log.debug(ExtendedLoggerMessageType.CFG, "syn022", String.valueOf(interval));
    }
    
    private void rescheduleAlert(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new AlertTask(), TasksSchedulerImpl.ALERT_JOB_NAME, startTime, interval);
    }
    
    private void rescheduleClearOldQueueLogs(final Date startTime, final long interval) {
        this.scheduleJob((JobRunner)new ClearOldQueueLogsTask(), TasksSchedulerImpl.CLEAR_OLD_QUEUE_LOGS_JOB_NAME, startTime, interval);
    }
    
    private void scheduleJob(final JobRunner taskInstance, final String jobName, final Date startTime, final long interval) {
        if (this.schedulerService == null) {
            this.log.warn(ExtendedLoggerMessageType.CFG, "syn023");
            return;
        }
        this.schedulerService.registerJobRunner(JobRunnerKey.of(taskInstance.getClass().getName()), taskInstance);
        final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put("synchronizerJobName", jobName);
        final JobConfig jobConfig = JobConfig.forJobRunnerKey(JobRunnerKey.of(taskInstance.getClass().getName())).withSchedule(Schedule.forInterval(interval, startTime)).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withParameters((Map)parameters);
        try {
            this.schedulerService.scheduleJob(JobId.of(jobName), jobConfig);
        }
        catch (SchedulerServiceException e) {
            e.printStackTrace();
            this.log.error(ExtendedLoggerMessageType.CFG, "exception scheduling job: " + jobName + " message: " + e.getMessage());
        }
    }
    
    private void setDefaultCronConfiguration() {
        final SynchronizerConfig config = this.synchronizerConfigService.getConfig();
        if (StringUtils.isBlank(config.getCronOutgoingJob())) {
            config.setCronOutgoingJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronOutgoingResponseJob())) {
            config.setCronOutgoingResponseJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronIncomingJob())) {
            config.setCronIncomingJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronIncomingResponseJob())) {
            config.setCronIncomingResponseJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronPullJob())) {
            config.setCronPullJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronPullResponsesJob())) {
            config.setCronPullResponsesJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronPullConfigurationJob())) {
            config.setCronPullConfigurationJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronArchivizationJob())) {
            config.setCronArchivizationJob("1 * * * * ? *");
        }
        if (StringUtils.isBlank(config.getCronClearOldQueueLogsJob())) {
            config.setCronClearOldQueueLogsJob("0 0 0 * * ? *");
        }
        if (config.getWorkflowRestApi() == null) {
            config.setWorkflowRestApi(1);
        }
        this.synchronizerConfigService.saveConfig(config);
    }
    
    static {
        OUTGOING_JOB_NAME = TasksSchedulerImpl.class.getName() + ":outgoing:job";
        OUTGOING_RESPONSE_JOB_NAME = TasksSchedulerImpl.class.getName() + ":outgoing-response:job";
        INCOMING_JOB_NAME = TasksSchedulerImpl.class.getName() + ":incoming:job";
        INCOMING_RESPONSE_JOB_NAME = TasksSchedulerImpl.class.getName() + ":incoming-response:job";
        ARCHIVIZATION_JOB_NAME = TasksSchedulerImpl.class.getName() + ":archivization:job";
        PULL_JOB_NAME = TasksSchedulerImpl.class.getName() + ":pull:job";
        PULL_RESPONSE_JOB_NAME = TasksSchedulerImpl.class.getName() + ":pull-response:job";
        PULL_CONFIGURATION_JOB_NAME = TasksSchedulerImpl.class.getName() + ":pull-configuration:job";
        ALERT_JOB_NAME = TasksSchedulerImpl.class.getName() + ":alert:job";
        CLEAR_OLD_QUEUE_LOGS_JOB_NAME = TasksSchedulerImpl.class.getName() + ":clear-old-queue-logs:job";
    }
}
