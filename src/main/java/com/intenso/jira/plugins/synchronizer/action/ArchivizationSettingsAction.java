// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueArchiveService;

public class ArchivizationSettingsAction extends LicenseAwareAction
{
    private static final long serialVersionUID = 1L;
    private final QueueArchiveService queueArchiveService;
    private String queueIn;
    private String queueOut;
    
    public ArchivizationSettingsAction(final QueueArchiveService queueArchiveService, final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.queueArchiveService = queueArchiveService;
    }
    
    @Override
    public String doDefault() throws Exception {
        return super.doDefault();
    }
    
    protected void doValidation() {
        super.doValidation();
    }
    
    @Override
    protected String doExecute() throws Exception {
        super.doExecute();
        if (this.queueIn != null) {
            this.queueArchiveService.archiveAllFromQueueIn();
        }
        if (this.queueOut != null) {
            this.queueArchiveService.archiveAllFromQueueOut();
        }
        return this.getRedirect("/secure/admin/ArchivizationSettingsAction!default.jspa");
    }
    
    public String getQueueIn() {
        return this.queueIn;
    }
    
    public void setQueueIn(final String queueIn) {
        this.queueIn = queueIn;
    }
    
    public String getQueueOut() {
        return this.queueOut;
    }
    
    public void setQueueOut(final String queueOut) {
        this.queueOut = queueOut;
    }
}
