// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;

public class GeneralConfigAction extends GenericConfigAction
{
    private static final long serialVersionUID = 6201442542522420822L;
    private SynchronizerConfigService synchronizerConfigService;
    private SynchronizerConfig config;
    
    public GeneralConfigAction(final SynchronizerConfigService synchronizerConfigService, final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.setSynchronizerConfigService(synchronizerConfigService);
    }
    
    @Override
    public boolean isGeneralConfig() {
        return true;
    }
    
    protected void doValidation() {
        super.doValidation();
    }
    
    @Override
    protected String doExecute() throws Exception {
        this.setConfig(this.synchronizerConfigService.getConfig());
        return super.doExecute();
    }
    
    public SynchronizerConfigService getSynchronizerConfigService() {
        return this.synchronizerConfigService;
    }
    
    public void setSynchronizerConfigService(final SynchronizerConfigService synchronizerConfigService) {
        this.synchronizerConfigService = synchronizerConfigService;
    }
    
    public SynchronizerConfig getConfig() {
        return this.config;
    }
    
    public void setConfig(final SynchronizerConfig config) {
        this.config = config;
    }
}
