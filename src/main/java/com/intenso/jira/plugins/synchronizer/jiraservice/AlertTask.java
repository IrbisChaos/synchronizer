// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.NotificationService;
import com.intenso.jira.plugins.synchronizer.entity.AlertHistory;
import com.intenso.jira.plugins.synchronizer.service.AlertHistoryService;
import java.util.Date;
import org.quartz.CronExpression;
import org.quartz.Calendar;
import org.quartz.CronTrigger;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.entity.JobStatistic;
import com.intenso.jira.plugins.synchronizer.service.JobStatisticService;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.AlertsService;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunner;

public class AlertTask implements JobRunner
{
    public JobRunnerResponse runJob(final JobRunnerRequest request) {
        final Map<String, Serializable> serializableMap = (Map<String, Serializable>)request.getJobConfig().getParameters();
        final Map<String, Object> objectMap = new HashMap<String, Object>();
        for (final String key : serializableMap.keySet()) {
            objectMap.put(key, serializableMap.get(key));
        }
        try {
            this.execute(objectMap);
        }
        catch (Exception e) {
            e.printStackTrace();
            return JobRunnerResponse.failed(e.getMessage());
        }
        return JobRunnerResponse.success();
    }
    
    public void execute(final Map<String, Object> jobDataMap) throws Exception {
        final AlertsService alertsService = (AlertsService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)AlertsService.class);
        final Boolean flag = Boolean.parseBoolean(alertsService.getConfiguration().getJobs());
        if (flag != null && flag) {
            final List<JobStatistic> jobStatistics = ((JobStatisticService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)JobStatisticService.class)).getAll();
            for (final JobStatistic jobStatistic : jobStatistics) {
                final Object jobName = jobStatistic.getName();
                String cronExpr = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).getCronByJobName((jobName == null) ? null : jobName.toString());
                if (cronExpr != null && !cronExpr.toString().isEmpty()) {
                    CronTrigger cronTrigger = new CronTrigger();
                    cronTrigger.setCronExpression(cronExpr.toString());
                    cronTrigger.setName(jobName.toString());
                    cronTrigger.triggered(null);
                    CronExpression cronExpression = new CronExpression(cronTrigger.getCronExpression());
                    Date next = cronExpression.getNextValidTimeAfter(jobStatistic.getLast());
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(next);
                    cal.add(12, 5);
                    if (!cal.getTime().before(new Date())) {
                        continue;
                    }
                    final String message = "Job name: " + jobStatistic.getName() + " not work.";
                    final AlertHistoryService alertHistoryService = (AlertHistoryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)AlertHistoryService.class);
                    final List<AlertHistory> list = alertHistoryService.findByMessage(message);
                    boolean skip = false;
                    if (!list.isEmpty()) {
                        final AlertHistory alert = list.get(0);
                        cal = java.util.Calendar.getInstance();
                        cronExpr = ((AlertsService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)AlertsService.class)).getConfiguration().getCron();
                        if (cronExpr == null || cronExpr.toString().isEmpty()) {
                            cronExpr = "0 0 1 * * ? *";
                        }
                        cronTrigger = new CronTrigger();
                        cronTrigger.setCronExpression(cronExpr.toString());
                        cronTrigger.triggered(null);
                        cronExpression = new CronExpression(cronTrigger.getCronExpression());
                        next = cronExpression.getNextValidTimeAfter(alert.getLast());
                        if (next.after(cal.getTime())) {
                            skip = true;
                        }
                    }
                    if (skip) {
                        continue;
                    }
                    if (jobStatistic.getName() == TasksSchedulerImpl.ARCHIVIZATION_JOB_NAME) {
                        final int queueIn = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).getConfig().getQueueIn();
                        final int queueOut = ((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).getConfig().getQueueOut();
                        if (queueIn != 1 && queueOut != 1) {
                            continue;
                        }
                        alertHistoryService.create(message);
                        ((NotificationService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)NotificationService.class)).notifyAboutAlert(message);
                    }
                    else {
                        alertHistoryService.create(message);
                        ((NotificationService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)NotificationService.class)).notifyAboutAlert(message);
                    }
                }
            }
        }
    }
}
