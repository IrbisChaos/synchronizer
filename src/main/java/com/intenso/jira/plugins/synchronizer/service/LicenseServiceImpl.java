// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.upm.api.license.PluginLicenseManager;

public class LicenseServiceImpl implements LicenseService
{
    private PluginLicenseManager manager;
    
    public LicenseServiceImpl(final PluginLicenseManager manager) {
        this.manager = manager;
    }
    
    @Override
    public SimpleErrorCollection checkLicense() {
        return LicenseUtils.checkLicense(this.manager);
    }
}
