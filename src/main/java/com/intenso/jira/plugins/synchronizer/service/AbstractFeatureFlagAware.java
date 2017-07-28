// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.component.ComponentAccessor;

public abstract class AbstractFeatureFlagAware implements FeatureFlagAware
{
    @Override
    public Boolean isOff(final String flagKey) {
        final ApplicationProperties ap = ComponentAccessor.getApplicationProperties();
        final String setting = ap.getDefaultString(flagKey);
        if (setting == null) {
            return false;
        }
        if (!setting.equals("1")) {
            return false;
        }
        return true;
    }
}
