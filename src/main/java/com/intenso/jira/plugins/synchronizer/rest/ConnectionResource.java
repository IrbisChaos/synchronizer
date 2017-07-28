// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Date;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ConfigurationJIRAClient;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/connection")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class ConnectionResource
{
    private ExtendedLogger logger;
    private ConfigurationJIRAClient remoteJiraClient;
    private ConnectionService connectionService;
    
    public ConnectionResource(final ConfigurationJIRAClient client, final ConnectionService connectionService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.remoteJiraClient = client;
        this.connectionService = connectionService;
    }
    
    @GET
    @Path("test/{authKey}")
    @AnonymousAllowed
    public Response getConnection(@PathParam("authKey") final String authKey) {
        final Connection connection = this.connectionService.findByAppKey(authKey);
        if (connection == null) {
            this.logger.warn(ExtendedLoggerMessageType.CFG, "ConnectionResource.getConnection connection is null");
            return Response.serverError().status(404).build();
        }
        if (!connection.getLocalAuthKey().equals(authKey)) {
            this.logger.warn(ExtendedLoggerMessageType.CFG, "ConnectionResource.getConnection authKey is invalid");
            return Response.serverError().status(500).build();
        }
        if (this.remoteJiraClient.testLocalConnection((connection.getAlterBaseUrl() == null || connection.getAlterBaseUrl().isEmpty()) ? ComponentAccessor.getApplicationProperties().getString("jira.baseurl") : connection.getAlterBaseUrl(), connection.getUsername(), connection.getPassword())) {
            this.logger.debug(ExtendedLoggerMessageType.CFG, "Local connection is ok");
            connection.setLastTest(new Date());
            final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
            connectionService.update(connection);
            return Response.ok().build();
        }
        return Response.serverError().status(404).build();
    }
}
