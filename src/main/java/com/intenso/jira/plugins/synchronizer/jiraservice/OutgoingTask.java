// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Arrays;
import java.util.List;
import com.google.common.collect.Lists;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public class OutgoingTask extends AbstractTask
{
    private final ExtendedLogger log;
    
    public OutgoingTask() {
        this.log = ExtendedLoggerFactory.getLogger(OutgoingTask.class);
    }
    
    @Override
    public void doExecute(final Map<String, Object> jobDataMap) {
        synchronized (OutgoingTask.class) {
            this.log.info(ExtendedLoggerMessageType.JOB, "syn005", jobDataMap.containsKey("FORCED") ? "(FORCED)" : "(SCHEDULED)");
            if (this.getConnectionService() == null) {
                return;
            }
            final List<Connection> connections = this.getConnectionService().getActiveModeConnectionsList();
            if (connections == null || connections.size() == 0) {
                this.log.warn(ExtendedLoggerMessageType.JOB, "No active connections found. ");
                return;
            }
            final QueueOutService qos = this.getQueueOutService();
            if (qos == null) {
                return;
            }
            final List<QueueOut> toRetry = qos.findByOrderByEventDate(QueueStatus.RETRY, Lists.newArrayList((Object[])new MessageType[] { MessageType.CREATE, MessageType.UPDATE, MessageType.DELETE, MessageType.COMMENT, MessageType.WORKFLOW, MessageType.WORKLOG }), connections);
            final List<QueueOut> newOnes = qos.findByOrderByEventDate(QueueStatus.NEW, Lists.newArrayList((Object[])new MessageType[] { MessageType.CREATE, MessageType.UPDATE, MessageType.DELETE, MessageType.COMMENT, MessageType.WORKFLOW, MessageType.WORKLOG }), connections);
            this.process(newOnes, false, false, false);
            this.process(toRetry, false, false, false);
            final List<QueueOut> retryAttachments = qos.findBy(QueueStatus.RETRY, Arrays.asList(MessageType.ATTACHMENT), connections);
            final List<QueueOut> newAttachments = qos.findBy(QueueStatus.NEW, Arrays.asList(MessageType.ATTACHMENT), connections);
            this.process(retryAttachments, false, false, false);
            this.process(newAttachments, false, false, false);
        }
    }
}
