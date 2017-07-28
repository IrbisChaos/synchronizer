// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.Map;

public class ClearOldQueueLogsTask extends AbstractTask
{
    private static final Integer DAYS_VALIDITY;
    
    @Override
    public void doExecute(final Map<String, Object> jobDataMap) {
        this.log.info(ExtendedLoggerMessageType.JOB, "syn055", jobDataMap.containsKey("FORCED") ? "(FORCED)" : "(SCHEDULED)");
        final QueueLogService queueLogService = this.getQueueLogService();
        if (queueLogService == null) {
            return;
        }
        final Integer queueLogsBefore = queueLogService.countAllByFilter(new HashMap<String, Object>());
        queueLogService.clearOutdated(ClearOldQueueLogsTask.DAYS_VALIDITY);
        final Integer queueLogsAfter = queueLogService.countAllByFilter(new HashMap<String, Object>());
        this.log.debug(ExtendedLoggerMessageType.CFG, "Removed " + (queueLogsBefore - queueLogsAfter) + " log entries.");
    }
    
    static {
        DAYS_VALIDITY = 30;
    }
}
