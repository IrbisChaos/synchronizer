// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.List;
import com.atlassian.jira.util.SimpleErrorCollection;
import java.util.Arrays;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.service.LicenseService;

public class PullTask extends AbstractPullTask
{
    private LicenseService licenseService;
    
    @Override
    protected Boolean doPull(final Connection connection) {
        final SimpleErrorCollection errorCollection = this.getLicenseService().checkLicense();
        if (errorCollection.hasAnyErrors()) {
            this.log.error(ExtendedLoggerMessageType.JOB, "License key is invalid. Data will not be pull from remote instance.");
            return null;
        }
        try {
            List<IncomingLogT> pulledData = null;
            pulledData = this.pullData(connection, QueueStatus.NEW, Arrays.asList(MessageType.CREATE, MessageType.UPDATE, MessageType.DELETE, MessageType.COMMENT, MessageType.WORKFLOW, MessageType.WORKLOG));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.getLogger().debug(ExtendedLoggerMessageType.JOB, "Sending Pull Response for status NEW and message type: COMMENT, UPDATE, CREATE, DELETE");
                this.sendResponse(connection, pulledData, false, false, false);
            }
            pulledData = this.pullData(connection, QueueStatus.RETRY, Arrays.asList(MessageType.CREATE, MessageType.UPDATE, MessageType.DELETE, MessageType.COMMENT, MessageType.WORKFLOW, MessageType.WORKLOG));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.getLogger().debug(ExtendedLoggerMessageType.JOB, "Sending Pull Response for status RETRY and message type: COMMENT, UPDATE, CREATE, DELETE");
                this.sendResponse(connection, pulledData, false, false, false);
            }
            pulledData = this.pullData(connection, QueueStatus.NEW, Arrays.asList(MessageType.ATTACHMENT));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.getLogger().debug(ExtendedLoggerMessageType.JOB, "Sending Pull Response for status NEW and message type: ATTACHMENT");
                this.sendResponse(connection, pulledData, false, false, false);
            }
            pulledData = this.pullData(connection, QueueStatus.RETRY, Arrays.asList(MessageType.ATTACHMENT));
            if (pulledData != null && !pulledData.isEmpty()) {
                this.getLogger().debug(ExtendedLoggerMessageType.JOB, "Sending Pull Response for status RETRY and message type: ATTACHMENT");
                this.sendResponse(connection, pulledData, false, false, false);
            }
            return Boolean.TRUE;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.log.error(ExtendedLoggerMessageType.JOB, "Pull Job broken " + e.getMessage());
            return Boolean.FALSE;
        }
    }
    
    private LicenseService getLicenseService() {
        if (this.licenseService == null) {
            this.licenseService = (LicenseService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)LicenseService.class);
        }
        return this.licenseService;
    }
}
