// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import org.slf4j.LoggerFactory;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.rest.model.AlertsT;
import com.intenso.jira.plugins.synchronizer.service.AlertsService;
import org.slf4j.Logger;

public class AlertsAction extends LicenseAwareAction
{
    private static final Logger log;
    private static final long serialVersionUID = -1L;
    private AlertsService alertsService;
    private AlertsT alertsT;
    
    public AlertsAction(final PluginLicenseManager pluginLicenseManager, final AlertsService alertsService) {
        super(pluginLicenseManager);
        this.alertsService = alertsService;
    }
    
    public boolean isAlerts() {
        return true;
    }
    
    @Override
    public String doDefault() throws Exception {
        this.load();
        return super.doDefault();
    }
    
    private void load() {
        this.alertsT = this.alertsService.getConfiguration();
        if (this.alertsT.getCron() == null || this.alertsT.getCron().isEmpty()) {
            this.alertsT.setCron("0 0 1 * * ? *");
        }
    }
    
    public AlertsT getAlertsT() {
        return this.alertsT;
    }
    
    public void setAlertsT(final AlertsT alertsT) {
        this.alertsT = alertsT;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)AlertsAction.class);
    }
}
