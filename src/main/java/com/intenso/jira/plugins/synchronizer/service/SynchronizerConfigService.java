// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;

public interface SynchronizerConfigService
{
    PluginSettings saveConfig(final SynchronizerConfig p0);
    
    SynchronizerConfig getConfig();
    
    void clearCache();
    
    Long getLastRun(final String p0);
    
    void setLastRun(final String p0, final Long p1);
    
    String getCronByJobName(final String p0);
    
    Boolean canCreateRemoteIssueOnDemand();
    
    Boolean canExternalComments();
}
