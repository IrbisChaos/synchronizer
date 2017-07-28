// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

public interface FeatureFlagAware
{
    String getFeatureFlagKey();
    
    Boolean isOff(final String p0);
}
