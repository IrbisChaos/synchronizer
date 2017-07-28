// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.providers;

import com.atlassian.jira.event.type.EventTypeManager;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import java.util.Collection;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.atlassian.jira.issue.Issue;
import java.util.HashMap;
import java.util.Map;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;

public class SyncStatusProvider extends AbstractJiraContextProvider
{
    private final ConnectionService connectionService;
    private final ContractService contractService;
    private final QueueOutService queueOutService;
    private final QueueInService queueInService;
    private PluginLicenseManager pluginLicenseManager;
    
    public SyncStatusProvider(final ConnectionService connectionService, final ContractService contractService, final QueueOutService queueOutService, final QueueInService queueInService, final PluginLicenseManager pluginLicenseManager) {
        this.connectionService = connectionService;
        this.contractService = contractService;
        this.queueOutService = queueOutService;
        this.queueInService = queueInService;
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    public Map<String, Object> getContextMap(final ApplicationUser user, final JiraHelper jiraHelper) {
        final Map<String, Object> contextMap = new HashMap<String, Object>();
        final Issue issue = (Issue)jiraHelper.getContextParams().get("issue");
        int processingCREATE = 0;
        int processingUPDATE = 0;
        int processingWORKFLOW = 0;
        int processingCOMMENT = 0;
        int processingATTACHMENT = 0;
        int typeCREATE = 0;
        int typeUPDATE = 0;
        int typeWORKFLOW = 0;
        int typeRESPONSE = 0;
        int typeCOMMENT = 0;
        int typeCOMMENTRESP = 0;
        int typeATTACHMENT = 0;
        int createDirection = -1;
        final List<QueueOut> outgoing = this.queueOutService.findByIssue(issue);
        int outNEW = 0;
        int outSENT = 0;
        int outERROR = 0;
        for (final QueueOut q : outgoing) {
            switch (q.getStatus()) {
                case 0: {
                    ++outNEW;
                    break;
                }
                case 2: {
                    ++outSENT;
                    break;
                }
                case 6: {
                    ++outERROR;
                    break;
                }
            }
            switch (q.getMsgType()) {
                case 0: {
                    ++typeRESPONSE;
                    continue;
                }
                case 1: {
                    ++typeCREATE;
                    if (q.getStatus() != 5) {
                        processingCREATE = 1;
                    }
                    createDirection = 1;
                    continue;
                }
                case 2: {
                    ++typeUPDATE;
                    if (q.getStatus() != 5) {
                        processingUPDATE = 1;
                        continue;
                    }
                    continue;
                }
                case 3: {
                    ++typeCOMMENT;
                    if (q.getStatus() != 5) {
                        processingCOMMENT = 1;
                        continue;
                    }
                    continue;
                }
                case 4: {
                    ++typeCOMMENTRESP;
                    continue;
                }
                case 5: {
                    ++typeATTACHMENT;
                    if (q.getStatus() != 5) {
                        processingATTACHMENT = 1;
                        continue;
                    }
                    continue;
                }
                case 6: {
                    ++typeWORKFLOW;
                    if (q.getStatus() != 5) {
                        processingWORKFLOW = 1;
                        continue;
                    }
                    continue;
                }
            }
        }
        final List<QueueIn> incoming = this.queueInService.findByIssue(issue);
        int inNEW = 0;
        int inPROCESSING = 0;
        int inERROR = 0;
        for (final QueueIn q2 : incoming) {
            switch (q2.getStatus()) {
                case 0: {
                    ++inNEW;
                    break;
                }
                case 1: {
                    ++inPROCESSING;
                    break;
                }
                case 6: {
                    ++inERROR;
                    break;
                }
            }
            switch (q2.getMsgType()) {
                case 0: {
                    ++typeRESPONSE;
                    continue;
                }
                case 1: {
                    ++typeCREATE;
                    createDirection = 0;
                    continue;
                }
                case 2: {
                    ++typeUPDATE;
                    continue;
                }
                case 3: {
                    ++typeCOMMENT;
                    continue;
                }
                case 4: {
                    ++typeCOMMENTRESP;
                    continue;
                }
                case 5: {
                    ++typeATTACHMENT;
                    continue;
                }
                case 6: {
                    ++typeWORKFLOW;
                    continue;
                }
            }
        }
        contextMap.put("outNEW", outNEW);
        contextMap.put("outSENT", outSENT);
        contextMap.put("outERROR", outERROR);
        contextMap.put("outWaitingCount", outNEW + outSENT);
        contextMap.put("outAllCount", outgoing.size());
        contextMap.put("inNEW", inNEW);
        contextMap.put("inPROCESSING", inPROCESSING);
        contextMap.put("inERROR", inERROR);
        contextMap.put("inWaitingCount", inNEW + inPROCESSING);
        contextMap.put("inAllCount", incoming.size());
        contextMap.put("typeCREATE", typeCREATE);
        contextMap.put("processingCREATE", processingCREATE);
        contextMap.put("typeCREATEdirection", createDirection);
        contextMap.put("typeUPDATE", typeUPDATE);
        contextMap.put("processingUPDATE", processingUPDATE);
        contextMap.put("typeWORKFLOW", typeWORKFLOW);
        contextMap.put("processingWORKFLOW", processingWORKFLOW);
        contextMap.put("typeRESPONSE", typeRESPONSE);
        contextMap.put("typeCOMMENT", typeCOMMENT);
        contextMap.put("processingCOMMENT", processingCOMMENT);
        contextMap.put("typeCOMMENTRESP", typeCOMMENTRESP);
        contextMap.put("typeATTACHMENT", typeATTACHMENT);
        contextMap.put("processingATTACHMENT", processingATTACHMENT);
        final List<Contract> contracts = this.contractService.findByContext(issue.getProjectId(), issue.getIssueTypeId());
        contextMap.put("technicalUserValidation", this.technicalUserValidation(contracts, issue, user));
        contextMap.put("passiveMode", this.checkPassiveMode(contracts, issue));
        final SimpleErrorCollection licenseError = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (licenseError.hasAnyErrors()) {
            contextMap.put("licenseErrors", licenseError.getErrorMessages());
        }
        contextMap.put("connAndContr", this.prepareConnAndContrModel(contracts));
        contextMap.put("baseURL", ComponentAccessor.getApplicationProperties().getString("jira.baseurl"));
        contextMap.put("issueKey", issue.getKey());
        final SynchronizerConfigService configService = (SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class);
        final SynchronizerConfig config = configService.getConfig();
        final Integer queueIn = config.getQueueIn();
        final Integer queueOut = config.getQueueOut();
        contextMap.put("archivizationOUTenabled", queueOut != null && queueOut == 1);
        contextMap.put("archivizationINenabled", queueIn != null && queueIn == 1);
        return contextMap;
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
    
    private List<String> checkPassiveMode(final List<Contract> contracts, final Issue issue) {
        final List<String> result = new ArrayList<String>();
        for (final Contract c : contracts) {
            final Connection connection = this.connectionService.get(c.getConnectionId());
            if (connection.getPassive() != null && connection.getPassive().equals(1)) {
                result.add("Connection : " + connection.getConnectionName() + " is passive. Messages need to be PULL by 'the other' JIRA.");
            }
        }
        return result;
    }
    
    private Collection<ConnectionDiagnosticModel> prepareConnAndContrModel(final List<Contract> contracts) {
        final Map<Integer, ConnectionDiagnosticModel> result = new HashMap<Integer, ConnectionDiagnosticModel>();
        final EventTypeManager eventMng = ComponentAccessor.getEventTypeManager();
        for (final Contract c : contracts) {
            ConnectionDiagnosticModel connEntry;
            if (result.containsKey(c.getConnectionId())) {
                connEntry = result.get(c.getConnectionId());
            }
            else {
                final Connection connection = this.connectionService.get(c.getConnectionId());
                connEntry = new ConnectionDiagnosticModel();
                connEntry.name = connection.getConnectionName();
                connEntry.id = connection.getID();
                connEntry.passiveMode = (connection.getPassive() != null && connection.getPassive().equals(1));
                result.put(connection.getID(), connEntry);
            }
            final ContractDiagnosticModel contrEntry = new ContractDiagnosticModel();
            connEntry.contracts.add(contrEntry);
            contrEntry.name = c.getContractName();
            contrEntry.id = c.getID();
            contrEntry.isEnabled = (c.getStatus() == 0);
            contrEntry.isAttachementEnabled = (c.getAttachments() == 1);
            contrEntry.isSynchronizeAllAttachmentsEnabled = (c.getSynchronizeAllAttachments() == 1);
            contrEntry.isCommentEnabled = (c.getComments() == 1);
            contrEntry.isCommentExternalEnabled = (c.getEnableExternalComment() == 1);
            contrEntry.isAllCommentsSynchronizationEnabled = (c.getSynchronizeAllComments() == 1);
            contrEntry.isWorkflowEnabled = (c.getWorkflowMapping() != null);
            if (c.getJqlConstraints() != null && !c.getJqlConstraints().isEmpty()) {
                contrEntry.jqlConstraints = c.getJqlConstraints();
            }
            List<ContractEvents> events = this.contractService.getEventsForContract(c.getID(), EventType.CREATE);
            for (final ContractEvents event : events) {
                contrEntry.createEvents.add(eventMng.getEventType(event.getEventId()).getName());
            }
            events = this.contractService.getEventsForContract(c.getID(), EventType.UPDATE);
            for (final ContractEvents event : events) {
                contrEntry.updateEvents.add(eventMng.getEventType(event.getEventId()).getName());
            }
            events = this.contractService.getEventsForContract(c.getID(), EventType.DELETE);
            for (final ContractEvents event : events) {
                contrEntry.deleteEvents.add(eventMng.getEventType(event.getEventId()).getName());
            }
        }
        return result.values();
    }
}
