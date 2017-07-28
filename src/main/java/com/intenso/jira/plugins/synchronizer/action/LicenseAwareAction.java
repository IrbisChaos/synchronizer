// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import java.util.Iterator;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class LicenseAwareAction extends JiraWebActionSupport
{
    private static final long serialVersionUID = -7957860909874835132L;
    private PluginLicenseManager pluginLicenseManager;
    
    public LicenseAwareAction(final PluginLicenseManager pluginLicenseManager) {
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    protected void validateLicense() {
        final SimpleErrorCollection errorCollection = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (errorCollection.hasAnyErrors()) {
            final StringBuilder errorMessage = new StringBuilder();
            int i = 0;
            for (final String msg : errorCollection.getErrorMessages()) {
                if (i > 0) {
                    errorMessage.append(",");
                }
                errorMessage.append(msg);
                ++i;
            }
            this.getErrors().put("licErrors", errorMessage.toString());
        }
    }
    
    protected String doExecute() throws Exception {
        this.validateLicense();
        return super.doExecute();
    }
    
    public String doDefault() throws Exception {
        this.validateLicense();
        return super.doDefault();
    }
}
