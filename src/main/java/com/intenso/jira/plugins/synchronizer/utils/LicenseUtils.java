// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import com.atlassian.jira.license.LicenseDetails;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import java.util.Arrays;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.license.JiraLicenseManager;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.upm.api.license.PluginLicenseManager;

public class LicenseUtils
{
    private static final String[] validServerIds;
    private static final String ADD_ON_NAME = "IssueSYNC";
    private static final String ADD_ON_VERSION = "0.4";
    private static final String APPLICATION_KEY = "com.intenso.jira.plugins.synchronizer";
    
    public static SimpleErrorCollection checkLicense(final PluginLicenseManager pluginLicenseManager) {
        final JiraLicenseManager license = (JiraLicenseManager)ComponentAccessor.getComponent((Class)JiraLicenseManager.class);
        final String serverId = license.getServerId();
        if (LicenseUtils.validServerIds != null && LicenseUtils.validServerIds.length > 0 && Arrays.asList(LicenseUtils.validServerIds).contains(serverId)) {
            return new SimpleErrorCollection();
        }
        final SimpleErrorCollection errorCollection = checkMarketplaceLicense(pluginLicenseManager);
        return errorCollection;
    }
    
    private static void checkLicense(final Option<PluginLicense> license, final SimpleErrorCollection errorCollectionToAddTo) {
        final JiraLicenseManager jiraLicenseManager = (JiraLicenseManager)ComponentAccessor.getComponent((Class)JiraLicenseManager.class);
        final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        if (license.isDefined()) {
            if (!((PluginLicense)license.get()).isValid()) {
                switch ((LicenseError)((PluginLicense)license.get()).getError().get()) {
                    case EXPIRED: {
                        errorCollectionToAddTo.addErrorMessage("Invalid license: Your evaluation license of IssueSYNC expired. Please use the 'Buy' button to purchase a new license.");
                        break;
                    }
                    case TYPE_MISMATCH: {
                        if (!jiraLicenseManager.getLicense("com.intenso.jira.plugins.synchronizer").isDeveloper()) {
                            final LicenseDetails licenseDetails = jiraLicenseManager.getLicense("com.intenso.jira.plugins.synchronizer");
                            errorCollectionToAddTo.addErrorMessage("Invalid license: Your IssueSYNC license does not match the " + licenseDetails.getDescription() + " license on this JIRA installation. Please get a " + licenseDetails.getDescription() + " license for " + "IssueSYNC" + " and try again.");
                            break;
                        }
                        break;
                    }
                    case VERSION_MISMATCH: {
                        errorCollectionToAddTo.addErrorMessage("Invalid license: Your license for maintenance of IssueSYNC is not valid for version 0.4 Please use the 'Renew' button to renew your IssueSYNC license.");
                        break;
                    }
                    case USER_MISMATCH: {
                        final int pluginCount = (int)((PluginLicense)license.get()).getMaximumNumberOfUsers().get();
                        final int appCount = jiraLicenseManager.getLicense("com.intenso.jira.plugins.synchronizer").getJiraLicense().getMaximumNumberOfUsers();
                        final boolean unlimitedPlugin = ((PluginLicense)license.get()).isUnlimitedNumberOfUsers();
                        final boolean unlimitedJira = ((PluginLicense)license.get()).isUnlimitedNumberOfUsers();
                        if (unlimitedJira) {
                            if (!unlimitedPlugin) {
                                errorCollectionToAddTo.addErrorMessage("Invalid license: Your IssueSYNC is only licensed for " + pluginCount + " users. Your JIRA installation requires a license for unlimited users. Please get a " + "IssueSYNC" + " license for unlimited users and try again.");
                                break;
                            }
                            break;
                        }
                        else {
                            if (pluginCount >= appCount || unlimitedPlugin) {
                                break;
                            }
                            final int connections = connectionService.countAll();
                            if (pluginCount == 50 && connections > 1) {
                                errorCollectionToAddTo.addErrorMessage("Invalid license: Your IssueSYNC is only licensed for " + pluginCount + " users. Your JIRA installation requires a license for " + appCount + " users. Please get a " + "IssueSYNC" + " license for " + appCount + " users and try again.");
                                break;
                            }
                            break;
                        }
                        break;
                    }
                }
            }
        }
        else {
            errorCollectionToAddTo.addErrorMessage("IssueSYNC is unlicensed. Please use the 'Buy' button to purchase a new license.");
        }
    }
    
    private static SimpleErrorCollection checkMarketplaceLicense(final PluginLicenseManager pluginLicenseManager) {
        final SimpleErrorCollection errorCollectionToAddTo = new SimpleErrorCollection();
        checkLicense((Option<PluginLicense>)pluginLicenseManager.getLicense(), errorCollectionToAddTo);
        return errorCollectionToAddTo;
    }
    
    static {
        validServerIds = new String[0];
    }
}
