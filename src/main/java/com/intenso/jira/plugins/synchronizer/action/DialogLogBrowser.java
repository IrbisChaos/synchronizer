// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;

public class DialogLogBrowser extends LogsBrowserAction
{
    private static final long serialVersionUID = -173555006161057247L;
    
    public DialogLogBrowser(final QueueLogService queueLogService, final QueueOutService queueOutService, final QueueInService queueInService, final ContractService contractService, final PluginLicenseManager pluginLicenseManager, final ConnectionService connectionService) {
        super(queueLogService, queueOutService, queueInService, contractService, pluginLicenseManager, connectionService);
    }
    
    @Override
    public boolean isDialog() {
        return Boolean.TRUE;
    }
}
