// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import javax.ws.rs.POST;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.component.ComponentAccessor;
import javax.ws.rs.core.Response;
import com.intenso.jira.plugins.synchronizer.rest.model.OperationT;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicationService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/operations")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class OperationsResource
{
    private final CommunicationService communicationService;
    private final SynchronizerConfigService synchronizerConfigService;
    private ExtendedLogger logger;
    
    public OperationsResource(final SynchronizerConfigService synchronizerConfigService, final CommunicationService communicationService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.synchronizerConfigService = synchronizerConfigService;
        this.communicationService = communicationService;
    }
    
    @POST
    @Path("/createremote")
    public Response createRemoteIssue(final OperationT input) {
        final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getUser();
        if (input.getIssueId() != null && input.getContractId() != null) {
            final IssueService.IssueResult issueResult = ComponentAccessor.getIssueService().getIssue(user, input.getIssueId());
            if (issueResult != null && issueResult.getIssue() != null) {
                final boolean created = this.communicationService.sendCreate((Issue)issueResult.getIssue(), input.getContractId(), user);
                if (created) {
                    return Response.ok((Object)input).build();
                }
                return Response.serverError().status(470).entity((Object)"Cannot create issue!").build();
            }
        }
        return Response.serverError().status(471).entity((Object)"Invalid parameters").build();
    }
}
