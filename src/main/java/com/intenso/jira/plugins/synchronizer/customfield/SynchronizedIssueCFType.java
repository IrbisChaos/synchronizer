// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.customfield;

import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.HashMap;
import java.util.Map;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;

public class SynchronizedIssueCFType extends GenericTextCFType implements PreinstallCFType
{
    public static final String SHOW_URL_OPTION = "show_url";
    public static final String SHOW_URL_OPTION_DISPLAY = "Show remote issue address";
    public static final String CFG_PROP_KEY = "com.intenso.jira.plugin.synchronizer:synchronized-issues-cf";
    private final SynchronizedIssuesService syncIssuesService;
    private final ConnectionService connectionService;
    
    public SynchronizedIssueCFType(final CustomFieldValuePersister customFieldValuePersister, final GenericConfigManager genericConfigManager, final SynchronizedIssuesService syncIssuesService, final ConnectionService connectionService) {
        super(customFieldValuePersister, genericConfigManager);
        this.syncIssuesService = syncIssuesService;
        this.connectionService = connectionService;
    }
    
    public Map<String, Object> getVelocityParameters(final Issue issue, final CustomField field, final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> parameters = (Map<String, Object>)super.getVelocityParameters(issue, field, fieldLayoutItem);
        if (issue != null) {
            final List<SyncIssue> issues = this.syncIssuesService.findByIssue(issue.getId());
            parameters.put("issues", issues);
            final Boolean showUrl = false;
            if (issues != null) {
                final Map<Integer, Connection> connections = new HashMap<Integer, Connection>();
                if (showUrl) {
                    for (final SyncIssue si : issues) {
                        final Connection connection = this.connectionService.getConnectionForContract(si.getContractId());
                        connections.put(si.getID(), connection);
                    }
                }
            }
            parameters.put("showUrl", showUrl);
        }
        else {
            parameters.put("showUrl", false);
            parameters.put("issues", null);
        }
        return parameters;
    }
    
    public String getCfgPropertyKey() {
        return "com.intenso.jira.plugin.synchronizer:synchronized-issues-cf";
    }
}
