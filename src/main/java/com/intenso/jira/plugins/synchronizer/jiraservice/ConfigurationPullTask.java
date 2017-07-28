// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.RemoteFieldMappingService;

public class ConfigurationPullTask extends AbstractPullTask
{
    private final RemoteFieldMappingService remoteConfigurationService;
    
    public ConfigurationPullTask() {
        this.remoteConfigurationService = (RemoteFieldMappingService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)RemoteFieldMappingService.class);
    }
    
    @Override
    protected Boolean doPull(final Connection connection) {
        if ((connection.getPassive() != null && connection.getPassive().equals(1)) || this.remoteConfigurationService == null) {
            return null;
        }
        final Boolean result = this.remoteConfigurationService.sendConfiguration(connection);
        if (result != null && result.equals(Boolean.TRUE)) {
            this.getConnectionService().makeConnectionInSync(connection.getID());
        }
        return result;
    }
    
    @Override
    protected void sendResponse(final Connection connection, final List<IncomingLogT> logs, final boolean isControlMsg, final boolean isCommentControlMsg, final boolean isINControlMsg) {
    }
}
