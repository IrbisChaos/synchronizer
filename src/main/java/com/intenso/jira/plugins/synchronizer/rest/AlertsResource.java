// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import com.intenso.jira.plugins.synchronizer.service.AlertHistoryService;
import com.intenso.jira.plugins.synchronizer.service.AlertsService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/alerts")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class AlertsResource
{
    private final AlertsService alertsService;
    private final AlertHistoryService alertHistoryService;
    
    public AlertsResource(final AlertsService alertsService, final AlertHistoryService alertHistoryService) {
        this.alertsService = alertsService;
        this.alertHistoryService = alertHistoryService;
    }
    
    @POST
    @Path("/configuration")
    public Response saveConfigurationJSON(final String json) {
        try {
            this.alertsService.saveConfigurationJSONString(json);
            return Response.ok((Object)json).build();
        }
        catch (Exception e) {
            return Response.serverError().entity((Object)e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/clean")
    public Response cleanAlertHistory() {
        try {
            this.alertHistoryService.clean();
            return Response.ok().build();
        }
        catch (Exception e) {
            return Response.serverError().entity((Object)e.getMessage()).build();
        }
    }
}
