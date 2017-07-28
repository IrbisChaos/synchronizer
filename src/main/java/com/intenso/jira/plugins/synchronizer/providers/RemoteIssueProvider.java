// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.providers;

import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Iterator;
import java.util.List;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import com.atlassian.jira.issue.Issue;
import java.util.HashMap;
import java.util.Map;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;

public class RemoteIssueProvider extends AbstractJiraContextProvider
{
    private final ConnectionService connectionService;
    private final ContractService contractService;
    private final SynchronizedIssuesService synchronizedIssuesService;
    private final QueueOutService queueOutService;
    private PluginLicenseManager pluginLicenseManager;
    
    public RemoteIssueProvider(final ConnectionService connectionService, final ContractService contractService, final QueueOutService queueOutService, final PluginLicenseManager pluginLicenseManager, final SynchronizedIssuesService synchronizedIssuesService) {
        this.connectionService = connectionService;
        this.contractService = contractService;
        this.queueOutService = queueOutService;
        this.synchronizedIssuesService = synchronizedIssuesService;
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    public Map<String, Object> getContextMap(final ApplicationUser user, final JiraHelper jiraHelper) {
        final Map<String, Object> contextMap = new HashMap<String, Object>();
        final Issue issue = jiraHelper.getContextParams().get("issue");
        final SimpleErrorCollection errorCollection = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (errorCollection.hasAnyErrors()) {
            contextMap.put("licErrors", errorCollection);
        }
        contextMap.put("showAsLink", true);
        contextMap.put("outOfSync", true);
        final Map<Integer, Boolean> outOfSyncWarn = new HashMap<Integer, Boolean>();
        final List<QueueOut> tasks = this.queueOutService.findByIssue(issue);
        for (final QueueOut queueOut : tasks) {
            if (queueOut.getStatus() != QueueStatus.DONE.ordinal() && queueOut.getStatus() != QueueStatus.CANCELLED.ordinal()) {
                outOfSyncWarn.put(queueOut.getContractId(), true);
            }
        }
        contextMap.put("outOfSyncWarn", outOfSyncWarn);
        final Map<Integer, Integer> errorRemote = new HashMap<Integer, Integer>();
        for (final QueueOut queueOut2 : tasks) {
            if (queueOut2.getStatus() == QueueStatus.ERROR_REMOTE.ordinal()) {
                errorRemote.put(queueOut2.getContractId(), queueOut2.getID());
            }
        }
        contextMap.put("errorRemote", errorRemote);
        final List<Contract> contracts = this.contractService.findByContext(issue.getProjectObject().getId(), issue.getIssueTypeId());
        final Map<Integer, String> contractsWithIsues = new HashMap<Integer, String>();
        final Map<String, String> remoteIssues = new HashMap<String, String>();
        final List<SyncIssue> findByIssue = this.synchronizedIssuesService.findByIssue(issue.getId());
        for (final SyncIssue syncIssue : findByIssue) {
            String url = "";
            final Integer contractId = syncIssue.getContractId();
            contractsWithIsues.put(contractId, this.findContractName(contracts, contractId));
            final Connection connectionForContract = this.connectionService.getConnectionForContract(contractId);
            if (connectionForContract != null) {
                url = connectionForContract.getRemoteJiraURL();
                remoteIssues.put(this.encode(syncIssue.getRemoteIssueKey(), contractId), (url != null) ? (url + "/browse/" + syncIssue.getRemoteIssueKey()) : null);
            }
        }
        contextMap.put("keys", remoteIssues);
        contextMap.put("contractsWithIsues", contractsWithIsues);
        final Map<Integer, String> createRemoteAction = new HashMap<Integer, String>();
        if (((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).canCreateRemoteIssueOnDemand()) {
            for (final Contract c : contracts) {
                if (!contractsWithIsues.keySet().contains(c.getID()) && new Integer(0).equals(c.getStatus())) {
                    createRemoteAction.put(c.getID(), c.getContractName());
                }
            }
        }
        contextMap.put("createRemoteAction", createRemoteAction);
        contextMap.put("technicalUserValidation", this.technicalUserValidation(contracts, issue, user));
        contextMap.put("baseURL", ComponentAccessor.getApplicationProperties().getString("jira.baseurl"));
        contextMap.put("provider", this);
        return contextMap;
    }
    
    private String findContractName(final List<Contract> contracts, final Integer id) {
        if (contracts != null && id != null) {
            for (final Contract c : contracts) {
                if (id.equals(c.getID())) {
                    return c.getContractName();
                }
            }
        }
        return "";
    }
    
    private String encode(final String key, final Integer contractId) {
        return key + "_" + contractId;
    }
    
    public String decode(final String key) {
        final String[] res = key.split("_");
        return res[0].equals("null") ? "" : res[0];
    }
    
    public Integer decodeContract(final String key) {
        final String[] res = key.split("_");
        if (res.length > 1) {
            return res[1].equals("null") ? null : Integer.parseInt(res[1]);
        }
        return null;
    }
    
    private List<String> technicalUserValidation(final List<Contract> contracts, final Issue issue, final ApplicationUser user) {
        final List<String> result = new ArrayList<String>();
        for (final Contract c : contracts) {
            final Connection connection = this.connectionService.get(c.getConnectionId());
            if (connection.getUsername() == null) {
                result.add("No technical user set for connection: " + connection.getConnectionName());
            }
            else {
                if (!connection.getUsername().equals(user.getName())) {
                    continue;
                }
                result.add("You are logged as a technical user for connection: " + connection.getConnectionName() + "! Synchronization will not be performed!");
            }
        }
        return result;
    }
}
