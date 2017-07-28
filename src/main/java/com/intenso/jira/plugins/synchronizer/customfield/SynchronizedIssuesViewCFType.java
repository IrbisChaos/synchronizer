// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.customfield;

import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import java.util.Map;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;

public class SynchronizedIssuesViewCFType extends GenericTextCFType
{
    public static final String CFG_PROP_KEY = "com.intenso.jira.plugin.synchronizer:synchronized-issues-view-cf";
    public static final String SHOW_URL_OPTION = "show_url";
    public static final String SHOW_URL_OPTION_DISPLAY = "Show remote issue link";
    public static final String OUT_OF_SYNC_INFO = "show_oos";
    public static final String OUT_OF_SYNC_INFO_DISPLAY = "Show information about synchronization status";
    private final SynchronizedIssuesService synchronizedIssuesService;
    private final QueueOutService queueOutService;
    private PluginLicenseManager pluginLicenseManager;
    
    protected SynchronizedIssuesViewCFType(final CustomFieldValuePersister customFieldValuePersister, final GenericConfigManager genericConfigManager, final SynchronizedIssuesService synchronizedIssuesService, final QueueOutService queueOutService, final PluginLicenseManager pluginLicenseManager) {
        super(customFieldValuePersister, genericConfigManager);
        this.synchronizedIssuesService = synchronizedIssuesService;
        this.queueOutService = queueOutService;
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    public Map<String, Object> getVelocityParameters(final Issue issue, final CustomField field, final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> result = (Map<String, Object>)super.getVelocityParameters(issue, field, fieldLayoutItem);
        final SimpleErrorCollection errorCollection = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (errorCollection.hasAnyErrors()) {
            result.put("licErrors", errorCollection);
        }
        result.put("showAsLink", false);
        result.put("outOfSync", false);
        return result;
    }
    
    public String getChangelogValue(final CustomField field, final String value) {
        return null;
    }
    
    public String getValueFromIssue(final CustomField field, final Issue issue) {
        String result = null;
        final List<SyncIssue> findByIssue = this.synchronizedIssuesService.findByIssue(issue.getId());
        if (findByIssue != null && !findByIssue.isEmpty()) {
            for (final SyncIssue syncIssue : findByIssue) {
                final String remoteIssueKey = syncIssue.getRemoteIssueKey();
                if (remoteIssueKey != null && !remoteIssueKey.isEmpty()) {
                    if (result == null) {
                        result = remoteIssueKey;
                    }
                    else {
                        result = result + "," + remoteIssueKey;
                    }
                }
            }
        }
        return result;
    }
    
    public Map<String, String> getValues(final Issue issue, final CustomField field) {
        final Map<String, String> result = new HashMap<String, String>();
        if (issue != null) {
            final List<SyncIssue> findByIssue = this.synchronizedIssuesService.findByIssue(issue.getId());
            for (final SyncIssue syncIssue : findByIssue) {
                final String url = "";
                String remoteIssueKey = syncIssue.getRemoteIssueKey();
                remoteIssueKey = remoteIssueKey + "_" + syncIssue.getContractId();
                result.put(remoteIssueKey, url);
            }
        }
        return result;
    }
    
    public String isOutOfSync(final Issue issue) {
        if (issue != null) {
            final List<QueueOut> tasks = this.queueOutService.findByIssue(issue);
            for (final QueueOut queueOut : tasks) {
                if (queueOut.getStatus() != QueueStatus.DONE.ordinal()) {
                    return QueueStatus.values()[queueOut.getStatus()].name();
                }
            }
            return QueueStatus.DONE.name();
        }
        return "Not found";
    }
    
    public String decode(final String key) {
        final String[] res = key.split("_");
        return res[0].equals("null") ? "" : res[0];
    }
    
    public String getCfgPropertyKey() {
        return "com.intenso.jira.plugin.synchronizer:synchronized-issues-view-cf";
    }
}
