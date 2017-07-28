// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.incoming;

import javax.ws.rs.POST;
import java.io.IOException;
import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.service.comm.Bundle;
import org.boon.Boon;
import com.intenso.jira.plugins.synchronizer.utils.LogsUtils;
import java.io.InputStream;
import com.intenso.jira.plugins.synchronizer.service.comm.AttachmentUtils;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.utils.SettingUtils;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import javax.ws.rs.core.Response;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorService;
import com.intenso.jira.plugins.synchronizer.service.comm.BundleQueueService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/in/queuein")
@Consumes({ "application/octet-stream", "multipart/form-data" })
@Produces({ "application/json" })
public class QueueInResource
{
    private final BundleQueueService bundleService;
    private final CommunicatorService communicator;
    private final QueueInService queueInService;
    private final PluginLicenseManager pluginLicenseManager;
    
    public QueueInResource(final BundleQueueService bundleService, final CommunicatorService communicator, final QueueInService queueInService, final PluginLicenseManager pluginLicenseManager) {
        this.bundleService = bundleService;
        this.communicator = communicator;
        this.queueInService = queueInService;
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    private Response checkLicense() {
        final SimpleErrorCollection licenseError = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (licenseError.hasAnyErrors()) {
            return Response.status((int)SettingUtils.INVALID_LICENSE_STATUS).entity((Object)"Probably JIRA has invalid plugin license").build();
        }
        return null;
    }
    
    @POST
    public Response process(@Context final HttpServletRequest request) throws IOException {
        final Response licenseResp = this.checkLicense();
        if (licenseResp != null) {
            return licenseResp;
        }
        try {
            final Bundle bundle = AttachmentUtils.readBundle((InputStream)request.getInputStream(), request.getContentType());
            final Integer connectionId = this.communicator.recognizeConnection(request);
            final MessageType msgType = this.communicator.getMessageType(request);
            if (bundle == null) {
                return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR).entity((Object)"Unable to read bundle. Errors detected.").build();
            }
            final List<IncomingLogT> queueIn = this.bundleService.saveIncomingBundle(connectionId, bundle, msgType);
            if (LogsUtils.logHasError(queueIn)) {
                return Response.ok((Object)Boon.toJson(queueIn), "application/json").status((int)SettingUtils.PARTIAL_ERROR_STATUS).build();
            }
            return Response.ok((Object)Boon.toJson(queueIn), "application/json").status(Response.Status.OK).build();
        }
        catch (Exception e) {
            return Response.serverError().status(500).entity((Object)e.getStackTrace()).build();
        }
    }
    
    public QueueInService getQueueInService() {
        return this.queueInService;
    }
}
