// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.service.comm.QueueArchiveService;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.Map;

public class ArchiveQueuesTask extends AbstractTask
{
    @Override
    public void doExecute(final Map<String, Object> jobDataMap) {
        this.log.info(ExtendedLoggerMessageType.JOB, "syn001", jobDataMap.containsKey("FORCED") ? "(FORCED)" : "(SCHEDULED)");
        final QueueArchiveService queueArchiveService = this.getQueueArchiveService();
        if (queueArchiveService == null) {
            return;
        }
        if (this.getSynchronizerConfigService().getConfig().getQueueIn() != null && this.getSynchronizerConfigService().getConfig().getQueueIn().equals(1)) {
            queueArchiveService.archiveAllFromQueueIn();
        }
        if (this.getSynchronizerConfigService().getConfig().getQueueOut() != null && this.getSynchronizerConfigService().getConfig().getQueueOut().equals(1)) {
            queueArchiveService.archiveAllFromQueueOut();
        }
    }
}
