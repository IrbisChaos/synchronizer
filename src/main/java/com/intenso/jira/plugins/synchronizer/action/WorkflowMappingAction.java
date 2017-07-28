// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import org.slf4j.LoggerFactory;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowConfigurationT;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.jira.util.lang.Pair;
import com.intenso.jira.plugins.synchronizer.entity.RemoteWorkflowMapping;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import com.atlassian.jira.workflow.JiraWorkflow;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowOutgoingTransitionsT;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowIncomingTransitionsT;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowMappingT;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.RemoteWorkflowMappingService;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import com.atlassian.jira.workflow.WorkflowManager;
import org.slf4j.Logger;

public class WorkflowMappingAction extends LicenseAwareAction
{
    private static final long serialVersionUID = -7649223419029636247L;
    private static final Logger log;
    private final WorkflowManager workflowManager;
    private final WorkflowSyncService workflowSyncService;
    private final RemoteWorkflowMappingService remoteWorkflowMappingService;
    private final ContractService contractService;
    private WorkflowMappingT mapping;
    private Integer workflowMappingId;
    private String workflowMappingDisplayName;
    private String workflow;
    private String in;
    private String out;
    
    public WorkflowMappingAction(final PluginLicenseManager pluginLicenseManager, final WorkflowManager workflowManager, final WorkflowSyncService workflowSyncService, final RemoteWorkflowMappingService remoteWorkflowMappingService, final ContractService contractService) {
        super(pluginLicenseManager);
        this.workflowManager = workflowManager;
        this.workflowSyncService = workflowSyncService;
        this.remoteWorkflowMappingService = remoteWorkflowMappingService;
        this.contractService = contractService;
    }
    
    public String doMapping() throws Exception {
        this.mapping = this.workflowSyncService.getWorkflowMapping(this.workflowMappingId);
        if (this.mapping == null) {
            (this.mapping = new WorkflowMappingT()).setWorkflowMappingId(this.workflowMappingId);
            this.mapping.setWorkflowMappingDisplayName(this.workflowMappingDisplayName);
            this.mapping.setWorkflowName(this.workflow);
        }
        else {
            this.mapping = this.workflowSyncService.getWorkflowMapping(this.workflowMappingId);
        }
        final ObjectMapper mapper = new ObjectMapper();
        final List<WorkflowIncomingTransitionsT> wit = this.mapping.getIncomingTransitions();
        this.in = mapper.writeValueAsString((Object)wit);
        final List<WorkflowOutgoingTransitionsT> wot = this.mapping.getOutgoingTransitions();
        this.out = mapper.writeValueAsString((Object)wot);
        return "mapping";
    }
    
    public List<String> getWorkflows() {
        final List<String> result = new ArrayList<String>();
        final List<JiraWorkflow> workflows = new ArrayList<JiraWorkflow>(this.workflowManager.getWorkflows());
        for (final JiraWorkflow w : workflows) {
            result.add(w.getDisplayName());
        }
        return result;
    }
    
    public Map<String, String> getRemoteWorkflows() {
        final Map<String, String> map = new HashMap<String, String>();
        for (final RemoteWorkflowMapping w : this.remoteWorkflowMappingService.getAll()) {
            map.put(w.getConnection() + "/" + w.getWorkflowMappingId(), w.getWorkflowMappingDisplayName());
        }
        return map;
    }
    
    public Map<String, List<Pair<Contract, Boolean>>> getContract() {
        final Map<String, List<Pair<Contract, Boolean>>> map = new HashMap<String, List<Pair<Contract, Boolean>>>();
        for (final Contract c : this.contractService.getAll()) {
            if (c.getWorkflowMapping() != null) {
                final JiraWorkflow w = this.workflowManager.getWorkflow(c.getProjectId(), c.getIssueType());
                final WorkflowMappingT wm = this.workflowSyncService.getWorkflowMapping(c.getWorkflowMapping());
                final String key = wm.getWorkflowMappingDisplayName();
                final String name = wm.getWorkflowName();
                List<Pair<Contract, Boolean>> list = map.get(key);
                if (list == null) {
                    list = new ArrayList<Pair<Contract, Boolean>>();
                }
                Boolean valid = true;
                if (!w.getDisplayName().equals(name)) {
                    valid = false;
                }
                list.add(Pair.of(c, valid));
                map.put(key, list);
            }
        }
        return map;
    }
    
    public List<WorkflowMappingT> getWorkflowMapping() {
        List<WorkflowMappingT> result = new ArrayList<WorkflowMappingT>();
        final String json = this.workflowSyncService.getConfigurationJSONString();
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final WorkflowConfigurationT w = (WorkflowConfigurationT)objectMapper.readValue(json, (Class)WorkflowConfigurationT.class);
            result = w.getConfiguration();
        }
        catch (Exception e) {
            WorkflowMappingAction.log.error("Workflow configuration error: " + e.getMessage());
        }
        return result;
    }
    
    public String getWorkflowMappingDisplayName() {
        return this.workflowMappingDisplayName;
    }
    
    public void setWorkflowMappingDisplayName(final String workflowMappingDisplayName) {
        this.workflowMappingDisplayName = workflowMappingDisplayName;
    }
    
    public String getWorkflow() {
        return this.workflow;
    }
    
    public void setWorkflow(final String workflow) {
        this.workflow = workflow;
    }
    
    public Integer getWorkflowMappingId() {
        return this.workflowMappingId;
    }
    
    public void setWorkflowMappingId(final Integer workflowMappingId) {
        this.workflowMappingId = workflowMappingId;
    }
    
    public WorkflowMappingT getMapping() {
        return this.mapping;
    }
    
    public void setMapping(final WorkflowMappingT mapping) {
        this.mapping = mapping;
    }
    
    public String getIn() {
        return this.in;
    }
    
    public void setIn(final String in) {
        this.in = in;
    }
    
    public String getOut() {
        return this.out;
    }
    
    public void setOut(final String out) {
        this.out = out;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)WorkflowMappingAction.class);
    }
}
