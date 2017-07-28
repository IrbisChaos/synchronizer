// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.atlassian.jira.component.ComponentAccessor;
import java.util.ArrayList;
import com.atlassian.upm.api.license.PluginLicenseManager;
import java.util.List;

public abstract class GenericConfigAction extends LicenseAwareAction
{
    private static final long serialVersionUID = 1167075284499724412L;
    private List<String> messages;
    private List<String> customErrors;
    
    public GenericConfigAction(final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.messages = new ArrayList<String>();
        this.customErrors = new ArrayList<String>();
    }
    
    @Override
    protected String doExecute() throws Exception {
        return super.doExecute();
    }
    
    @Override
    public String doDefault() throws Exception {
        return super.doDefault();
    }
    
    public String getBaseURL() {
        return ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
    }
    
    public List<String> getMessages() {
        return this.messages;
    }
    
    public List<String> getCustomErrors() {
        return this.customErrors;
    }
    
    public void addMessage(final String message) {
        this.messages.add(message);
    }
    
    public void addCustomErrors(final String customErrors) {
        this.customErrors.add(customErrors);
    }
    
    public boolean isEmptyMessages() {
        return this.messages.isEmpty();
    }
    
    public boolean isGeneralConfig() {
        return false;
    }
    
    public boolean isConnectionConfig() {
        return false;
    }
    
    public boolean isContractConfig() {
        return false;
    }
    
    public boolean isDialog() {
        return false;
    }
}
