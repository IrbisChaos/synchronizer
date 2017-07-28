// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.rest.model.AlertsT;

public interface AlertsService
{
    void saveConfigurationJSONString(final String p0);
    
    AlertsT getConfiguration();
}
