// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import com.intenso.jira.plugins.synchronizer.entity.RemoteWorkflowMapping;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowIncomingTransitionsT;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowMappingT;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowTransitionT;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowEntryViewModelT;
import com.atlassian.jira.issue.status.Status;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowHelpViewModelT;
import java.util.Collection;
import com.atlassian.jira.workflow.JiraWorkflow;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import org.codehaus.jackson.map.ObjectWriter;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowMappingT;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowConfigurationT;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.service.RemoteWorkflowMappingService;
import com.atlassian.jira.workflow.WorkflowManager;
import com.intenso.jira.plugins.synchronizer.service.WorkflowSyncService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/workflowMapping")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class WorkflowMappingResource
{
    private final WorkflowSyncService workflowSyncService;
    private final WorkflowManager workflowManager;
    private final RemoteWorkflowMappingService remoteWorkflowMappingService;
    private Map<String, String> statusCategoryMap;
    
    public WorkflowMappingResource(final WorkflowSyncService workflowSyncService, final WorkflowManager workflowManager, final RemoteWorkflowMappingService remoteWorkflowMappingService) {
        this.statusCategoryMap = (Map<String, String>)ImmutableMap.builder().put((Object)"new", (Object)"aui-lozenge aui-lozenge-complete").put((Object)"indeterminate", (Object)"aui-lozenge aui-lozenge-current").put((Object)"done", (Object)"aui-lozenge aui-lozenge-success").put((Object)"undefined", (Object)"aui-lozenge jira-issue-status-lozenge-medium-gray").build();
        this.workflowSyncService = workflowSyncService;
        this.workflowManager = workflowManager;
        this.remoteWorkflowMappingService = remoteWorkflowMappingService;
    }
    
    @GET
    @Path("/remote")
    public Response getRemoteConfigurationJSON(@Context final HttpServletRequest request) {
        final String json = this.workflowSyncService.getRemoteConfigurationJSONString();
        return Response.ok((Object)((json != null) ? json : "{\"configuration\": null}")).build();
    }
    
    @GET
    @Path("/configuration")
    public Response getConfigurationJSON(@Context final HttpServletRequest request) {
        final String json = this.workflowSyncService.getConfigurationJSONString();
        return Response.ok((Object)((json != null) ? json : "{\"configuration\": null}")).build();
    }
    
    @POST
    @Path("/configuration")
    public Response saveConfigurationJSON(final WorkflowConfigurationT configuration) {
        try {
            boolean validation = true;
            final List<WorkflowMappingT> mappings = configuration.getConfiguration();
            for (final WorkflowMappingT mapping : mappings) {
                if (mapping.getWorkflowMappingId() == null) {
                    validation = false;
                    break;
                }
            }
            if (validation) {
                final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                final String json = ow.writeValueAsString((Object)configuration);
                this.workflowSyncService.saveConfigurationJSONString(json);
                return Response.ok((Object)json).build();
            }
            return Response.serverError().entity((Object)"WorkflowMappingId is required!").build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity((Object)e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/workflow/{name}")
    public Response getWorkflow(@PathParam("name") final String name) {
        final Map<String, Object> result = new HashMap<String, Object>();
        final List<JiraWorkflow> jiraWorkflows = new ArrayList<JiraWorkflow>(this.workflowManager.getWorkflows());
        JiraWorkflow jiraWorkflow = null;
        for (final JiraWorkflow jw : jiraWorkflows) {
            if (jw.getDisplayName().equals(name)) {
                jiraWorkflow = jw;
                break;
            }
        }
        if (jiraWorkflow == null) {
            return Response.status(500).build();
        }
        final WorkflowHelpViewModelT workflow = new WorkflowHelpViewModelT("0", jiraWorkflow.getDisplayName());
        List<ActionDescriptor> actionDescriptors = new ArrayList<ActionDescriptor>(jiraWorkflow.getDescriptor().getGlobalActions());
        String prevStatusId = "-1";
        String prevStatusName = "All";
        String prevStatusColor = "workflow-transition-lozenge workflow-transition-global";
        this.getTransitions(jiraWorkflow, workflow, prevStatusId, prevStatusName, prevStatusColor, actionDescriptors);
        final List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>(jiraWorkflow.getDescriptor().getSteps());
        for (final StepDescriptor stepDescriptor : stepDescriptors) {
            prevStatusId = "" + stepDescriptor.getMetaAttributes().get("jira.status.id");
            prevStatusName = stepDescriptor.getName();
            final Status prevStatus = jiraWorkflow.getLinkedStatus(stepDescriptor);
            prevStatusColor = this.statusCategoryMap.get(prevStatus.getStatusCategory().getKey());
            actionDescriptors = new ArrayList<ActionDescriptor>(stepDescriptor.getActions());
            this.getTransitions(jiraWorkflow, workflow, prevStatusId, prevStatusName, prevStatusColor, actionDescriptors);
        }
        result.put("workflow", workflow);
        return Response.ok((Object)result).build();
    }
    
    private void getTransitions(final JiraWorkflow jiraWorkflow, final WorkflowHelpViewModelT workflow, final String prevStatusId, final String prevStatusName, final String prevStatusColor, final List<ActionDescriptor> actionDescriptors) {
        for (final ActionDescriptor actionDescriptor : actionDescriptors) {
            final Status currStatus = jiraWorkflow.getLinkedStatus(jiraWorkflow.getDescriptor().getStep(actionDescriptor.getUnconditionalResult().getStep()));
            if (currStatus != null) {
                final String currentStatusId = currStatus.getId();
                final String currentStatusName = currStatus.getName();
                final String currentStatusColor = this.statusCategoryMap.get(currStatus.getStatusCategory().getKey());
                WorkflowEntryViewModelT workflowEntry = null;
                for (final WorkflowEntryViewModelT we : workflow.getEntries()) {
                    if (we.getCurrStatusId().equals(currentStatusId) && we.getPrevStatusId().equals(prevStatusId)) {
                        workflowEntry = we;
                        break;
                    }
                }
                if (workflowEntry == null) {
                    workflowEntry = new WorkflowEntryViewModelT();
                    workflowEntry.setCurrStatusId(currentStatusId);
                    workflowEntry.setCurrStatusName(currentStatusName);
                    workflowEntry.setCurrStatusColor(currentStatusColor);
                    workflowEntry.setPrevStatusId(prevStatusId);
                    workflowEntry.setPrevStatusName(prevStatusName);
                    workflowEntry.setPrevStatusColor(prevStatusColor);
                    workflow.getEntries().add(workflowEntry);
                }
                final WorkflowTransitionT transation = new WorkflowTransitionT();
                transation.setTransitionId("" + actionDescriptor.getId());
                transation.setTransitionName(actionDescriptor.getName());
                workflowEntry.getTransitions().add(transation);
            }
        }
    }
    
    @POST
    @Path("/workflow/{id}")
    public Response deleteWorkflow(@PathParam("id") final Integer id) {
        this.workflowSyncService.deleteConfigurationById(id);
        return Response.ok().build();
    }
    
    @GET
    @Path("/remote/{connection}/{id}")
    public Response getRemoteWorkflows(@PathParam("connection") final Integer connection, @PathParam("id") final Integer id) {
        final Map<String, Object> result = new HashMap<String, Object>();
        final RemoteWorkflowMappingT w = new RemoteWorkflowMappingT();
        w.setWorkflowMappingId(id);
        final List<RemoteWorkflowIncomingTransitionsT> rwits = new ArrayList<RemoteWorkflowIncomingTransitionsT>();
        for (final RemoteWorkflowMapping rwm : this.remoteWorkflowMappingService.getAll()) {
            if (rwm.getConnection().equals(connection) && rwm.getWorkflowMappingId().equals(id)) {
                w.setWorkflowMappingDisplayName(rwm.getWorkflowMappingDisplayName());
                final RemoteWorkflowIncomingTransitionsT rwit = new RemoteWorkflowIncomingTransitionsT();
                rwit.setInTransitionText(rwm.getInTransitionText());
                rwit.setResolution(rwm.getResolution() != null && rwm.getResolution() > 0);
                rwits.add(rwit);
            }
        }
        w.setIncomingTransitions(rwits);
        result.put("remoteWorkflow", w);
        return Response.ok((Object)result).build();
    }
}
