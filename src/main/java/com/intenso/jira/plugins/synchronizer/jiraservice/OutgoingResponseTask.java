// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public class OutgoingResponseTask extends AbstractTask
{
    private final ExtendedLogger log;
    
    public OutgoingResponseTask() {
        this.log = ExtendedLoggerFactory.getLogger(OutgoingResponseTask.class);
    }
    
    @Override
    public void doExecute(final Map<String, Object> jobDataMap) {
        synchronized (OutgoingResponseTask.class) {
            this.log.info(ExtendedLoggerMessageType.JOB, "syn006", jobDataMap.containsKey("FORCED") ? "(FORCED)" : "(SCHEDULED)");
            if (this.getConnectionService() == null) {
                return;
            }
            final List<Connection> activeConnections = this.getConnectionService().getActiveModeConnectionsList();
            if (activeConnections == null || activeConnections.size() == 0) {
                this.log.warn(ExtendedLoggerMessageType.JOB, "No active connections found. ");
                return;
            }
            final QueueOutService qos = this.getQueueOutService();
            if (qos == null) {
                return;
            }
            final List<QueueOut> toRetry = qos.findBy(QueueStatus.RETRY, MessageType.RESPONSE, activeConnections);
            final List<QueueOut> toRetryComments = qos.findBy(QueueStatus.RETRY, MessageType.RESPONSE_COMMENT, activeConnections);
            final List<QueueOut> toRetryIn = qos.findBy(QueueStatus.RETRY, MessageType.IN_RESPONSE, activeConnections);
            final List<QueueOut> newOnes = qos.findBy(QueueStatus.NEW, MessageType.RESPONSE, activeConnections);
            final List<QueueOut> newOnesComments = qos.findBy(QueueStatus.NEW, MessageType.RESPONSE_COMMENT, activeConnections);
            final List<QueueOut> newOnesIn = qos.findBy(QueueStatus.NEW, MessageType.IN_RESPONSE, activeConnections);
            this.process(newOnes, true, false, false);
            this.process(newOnesComments, false, true, false);
            this.process(newOnesIn, false, false, true);
            this.process(toRetry, true, false, false);
            this.process(toRetryComments, false, true, false);
            this.process(toRetryIn, false, false, true);
        }
    }
}
