// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.List;
import java.util.Arrays;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.sal.api.scheduling.PluginJob;

public class PullResponseTask extends AbstractPullTask implements PluginJob
{
    @Override
    protected Boolean doPull(final Connection connection) {
        try {
            List<IncomingLogT> pulledData = null;
            pulledData = this.pullData(connection, QueueStatus.NEW, Arrays.asList(MessageType.RESPONSE));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.sendResponse(connection, pulledData, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
            }
            pulledData = this.pullData(connection, QueueStatus.NEW, Arrays.asList(MessageType.RESPONSE_COMMENT));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.sendResponse(connection, pulledData, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
            }
            pulledData = this.pullData(connection, QueueStatus.NEW, Arrays.asList(MessageType.IN_RESPONSE));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.sendResponse(connection, pulledData, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            }
            pulledData = this.pullData(connection, QueueStatus.RETRY, Arrays.asList(MessageType.RESPONSE));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.sendResponse(connection, pulledData, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
            }
            pulledData = this.pullData(connection, QueueStatus.RETRY, Arrays.asList(MessageType.RESPONSE_COMMENT));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.sendResponse(connection, pulledData, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
            }
            pulledData = this.pullData(connection, QueueStatus.RETRY, Arrays.asList(MessageType.IN_RESPONSE));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.sendResponse(connection, pulledData, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
            }
            return Boolean.TRUE;
        }
        catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
}
