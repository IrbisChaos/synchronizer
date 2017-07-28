// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.upgrade;

import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import java.util.ArrayList;
import com.atlassian.sal.api.message.Message;
import java.util.Collection;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;

public class RemoteIssuePanelPropertyUpgrade implements PluginUpgradeTask
{
    private SynchronizerConfigService synchronizerConfigService;
    
    public int getBuildNumber() {
        return 3;
    }
    
    public String getShortDescription() {
        return "Setting default valu for GenRemoteIssuePanel property.";
    }
    
    public Collection<Message> doUpgrade() throws Exception {
        final Collection<Message> messages = new ArrayList<Message>();
        final SynchronizerConfig config = this.getSynchronizerConfigService().getConfig();
        final Integer propertyValue = config.getGenRemoteIssuePanel();
        if (propertyValue == null) {
            config.setGenRemoteIssuePanel(1);
        }
        this.synchronizerConfigService.saveConfig(config);
        return messages;
    }
    
    public String getPluginKey() {
        return "com.intenso.jira.plugins.synchronizer";
    }
    
    public SynchronizerConfigService getSynchronizerConfigService() {
        if (this.synchronizerConfigService == null) {
            this.synchronizerConfigService = (SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class);
        }
        return this.synchronizerConfigService;
    }
}
