// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.incoming;

import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import org.boon.json.implementation.ObjectMapperImpl;
import org.apache.commons.io.IOUtils;
import com.intenso.jira.plugins.synchronizer.rest.model.cloud.ConnectionServerSyncData;
import org.codehaus.jackson.map.ObjectMapper;
import javax.ws.rs.POST;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonGenerationException;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteConfigurationWrapperT;
import java.io.InputStream;
import java.io.File;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.service.comm.AttachmentUtils;
import com.intenso.jira.plugins.synchronizer.service.comm.Bundle;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import javax.ws.rs.core.Context;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.intenso.jira.plugins.synchronizer.utils.SettingUtils;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import javax.servlet.http.HttpServletRequest;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;
import com.intenso.jira.plugins.synchronizer.service.comm.BundleQueueService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/in/queueout")
@Consumes({ "application/json", "application/octet-stream", "multipart/form-data", "application/x-www-form-urlencoded" })
@Produces({ "application/octet-stream", "multipart/form-data", "application/json" })
public class QueueOutResource
{
    private QueueOutService queueOutService;
    private ConnectionService connectionService;
    private BundleQueueService bundleQueueService;
    private QueueLogService queueLogService;
    private PluginLicenseManager pluginLicenseManager;
    private ExtendedLogger logger;
    public static final String STATUS_PARAMETER = "status";
    public static final String MESSAGE_TYPE_PARAMETER = "msgTypes";
    public static final String RESPONSE_STATUS_PARAMETER = "responseStatus";
    public static final String RESPONSE_LOGS_PARAMETER = "responseLogs";
    
    public QueueOutResource(final PluginLicenseManager pluginLicenseManager) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    private QueueStatus getQueueStatus(final HttpServletRequest request) {
        final String status = request.getParameter("status");
        if (status == null) {
            return null;
        }
        final Integer statusInt = Integer.parseInt(status);
        if (statusInt == null || statusInt > QueueStatus.values().length - 1) {
            return null;
        }
        final QueueStatus statusObject = QueueStatus.values()[statusInt];
        return statusObject;
    }
    
    private List<MessageType> getMessageTypes(final HttpServletRequest request) {
        final List<MessageType> messageTypesResult = new ArrayList<MessageType>();
        final String[] msgTypes = request.getParameterValues("msgTypes");
        if (msgTypes == null) {
            return null;
        }
        for (final String s : Arrays.asList(msgTypes)) {
            if (s != null) {
                final Integer typeInt = Integer.parseInt(s);
                if (typeInt == null || typeInt >= MessageType.values().length) {
                    continue;
                }
                messageTypesResult.add(MessageType.values()[typeInt]);
            }
        }
        return messageTypesResult;
    }
    
    private Response checkLicense() {
        final SimpleErrorCollection licenseError = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (licenseError.hasAnyErrors()) {
            return Response.status((int)SettingUtils.INVALID_LICENSE_STATUS).entity((Object)"Probably JIRA has invalid plugin license").build();
        }
        return null;
    }
    
    private Response checkPassiveMode(final HttpServletRequest request) {
        final String appKey = request.getHeader("X-JIRA-SYNC-APPKEY");
        final Connection connection = this.getConnectionService().findByAppKey(appKey);
        if (appKey == null || connection == null) {
            return Response.serverError().status(500).entity((Object)("Not enough parameters (appKey is null: " + (appKey == null) + ", connection: " + connection)).build();
        }
        if (connection.getPassive() == null || connection.getPassive() != 1) {
            return Response.serverError().status(210).entity((Object)"Not passive connection!").build();
        }
        return null;
    }
    
    @GET
    public Response process(@Context final HttpServletRequest request) {
        final Response licenseResp = this.checkLicense();
        if (licenseResp != null) {
            return licenseResp;
        }
        final Response passiveResp = this.checkPassiveMode(request);
        if (passiveResp != null) {
            return passiveResp;
        }
        final String appKey = request.getHeader("X-JIRA-SYNC-APPKEY");
        final Connection connection = this.getConnectionService().findByAppKey(appKey);
        final QueueStatus queueStatus = this.getQueueStatus(request);
        final List<MessageType> msgTypes = this.getMessageTypes(request);
        if (queueStatus == null || msgTypes == null || msgTypes.size() == 0) {
            return Response.serverError().status(500).entity((Object)("Not enough parameters (queueStatus: " + queueStatus + ", msgTypes: " + msgTypes + ", appKey is null: " + (appKey == null) + ", connection: " + connection)).build();
        }
        List<QueueOut> out = null;
        boolean isControlMsg = Boolean.FALSE;
        if (msgTypes.contains(MessageType.RESPONSE)) {
            out = this.getQueueOutService().findBy(queueStatus, MessageType.RESPONSE, Arrays.asList(connection));
            isControlMsg = Boolean.TRUE;
            if (msgTypes.size() > 1) {
                this.logger.error(ExtendedLoggerMessageType.REST, "Pulling responses with other information. Only response should be here.");
            }
        }
        else if (msgTypes.contains(MessageType.RESPONSE_COMMENT)) {
            isControlMsg = Boolean.TRUE;
            out = this.getQueueOutService().findBy(queueStatus, MessageType.RESPONSE_COMMENT, Arrays.asList(connection));
            if (msgTypes.size() > 1) {
                this.logger.error(ExtendedLoggerMessageType.REST, "Pulling responses with other information. Only response should be here.");
            }
        }
        else {
            out = this.getQueueOutService().findBy(queueStatus, msgTypes, Arrays.asList(connection));
        }
        if (out == null || out.size() == 0) {
            return Response.ok().status(204).entity((Object)AttachmentUtils.compress(new Bundle())).build();
        }
        final Bundle bundle = this.getBundleQueueService().createBundle(connection.getID(), out, isControlMsg);
        return Response.ok((Object)AttachmentUtils.compress(bundle)).header("content-type", (Object)"application/octet-stream").type("application/octet-stream").build();
    }
    
    @GET
    @Path("/attachment/{attachmentId}")
    public Response getAttachment(@PathParam("attachmentId") final String attachmentId, @Context final HttpServletRequest request) {
        final Response licenseResp = this.checkLicense();
        if (licenseResp != null) {
            return licenseResp;
        }
        final Response passiveResp = this.checkPassiveMode(request);
        if (passiveResp != null) {
            return passiveResp;
        }
        final File file = AttachmentUtils.prepareAttachmentForRequest(Long.parseLong(attachmentId));
        return Response.ok((Object)file).build();
    }
    
    @POST
    @Path("/configuration")
    @Produces({ "application/octet-stream" })
    public Response updateConfiguration(@Context final HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException {
        final String applicationKey = request.getHeader("X-JIRA-SYNC-APPKEY");
        final Connection connection = this.getConnectionService().findByAppKey(applicationKey);
        if (connection == null) {
            return Response.serverError().status(500).entity((Object)"Unauthorized").build();
        }
        final RemoteConfigurationWrapperT remoteConfig = SettingUtils.parseRemoteConfigurationWrapper((InputStream)request.getInputStream());
        remoteConfig.setConnection(connection.getID());
        SettingUtils.updateConfiguration(remoteConfig);
        this.getConnectionService().makeConnectionInSync(connection.getID());
        final RemoteConfigurationWrapperT ownConfig = SettingUtils.getConfigurationForConnection(connection);
        final byte[] result = SettingUtils.writeRemoteConfigurationWrapper(ownConfig);
        return Response.ok((Object)result).type("application/octet-stream").build();
    }
    
    @POST
    @Path("/configuration/cloud")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response updateConfigurationCloud(@Context final HttpServletRequest request) {
        final String applicationKey = request.getHeader("X-JIRA-SYNC-APPKEY");
        final Connection connection = this.getConnectionService().findByAppKey(applicationKey);
        if (connection == null) {
            return Response.serverError().status(500).entity((Object)"Unauthorized").build();
        }
        ConnectionServerSyncData remoteConfiguration = null;
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            remoteConfiguration = (ConnectionServerSyncData)objectMapper.readValue((InputStream)request.getInputStream(), (Class)ConnectionServerSyncData.class);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().status(500).entity((Object)e.getMessage()).build();
        }
        SettingUtils.updateConfiguration(remoteConfiguration, connection.getID());
        final ConnectionServerSyncData localConfiguration = SettingUtils.getConfigurationForConnectionCloud(connection);
        String localJson = "";
        try {
            localJson = objectMapper.writeValueAsString((Object)localConfiguration);
            return Response.ok().entity((Object)localJson).build();
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return Response.serverError().status(500).entity((Object)e2.getMessage()).build();
        }
    }
    
    @POST
    public Response saveResponse(@Context final HttpServletRequest request) {
        try {
            final Response licenseResp = this.checkLicense();
            if (licenseResp != null) {
                return licenseResp;
            }
            final Response passiveResp = this.checkPassiveMode(request);
            if (passiveResp != null) {
                return passiveResp;
            }
            final String statusParam = request.getParameter("responseStatus");
            IOUtils.toString((InputStream)request.getInputStream());
            if (statusParam == null) {
                return Response.serverError().status(500).entity((Object)"Status cannot be empty!").build();
            }
            final String controlMsg = request.getHeader("X-JIRA-CONTROL-MSG");
            boolean isControlMsg = Boolean.FALSE;
            if (controlMsg != null && (controlMsg.equals("1") || controlMsg.equals("2"))) {
                isControlMsg = Boolean.TRUE;
            }
            final Integer responseStatus = Integer.parseInt(statusParam);
            final Response.Status statusCode = (responseStatus != null && responseStatus < Response.Status.values().length) ? Response.Status.values()[responseStatus] : null;
            final String logs = request.getParameter("responseLogs");
            final org.boon.json.ObjectMapper mapper = new ObjectMapperImpl();
            final List<IncomingLogT> log = mapper.readValue(logs, (Class<List<IncomingLogT>>)List.class, IncomingLogT.class);
            if (log != null) {
                for (final IncomingLogT logEntry : log) {
                    try {
                        Boolean error = false;
                        if (logEntry.getError() != null || logEntry.getQueueIn() == null) {
                            error = true;
                        }
                        this.getQueueLogService().createQueueLog(Integer.valueOf((statusCode != null) ? statusCode.getStatusCode() : responseStatus), (logEntry.getError() != null) ? logEntry.getError() : (("Acknowledge " + statusCode != null) ? statusCode.getReasonPhrase() : ""), (logEntry.getSourceQueueOutId() != null) ? this.getQueueOutService().find(logEntry.getSourceQueueOutId()) : null);
                        if (error) {
                            this.getQueueOutService().update(logEntry.getSourceQueueOutId(), QueueStatus.ERROR);
                        }
                        else {
                            this.getQueueOutService().update(logEntry.getSourceQueueOutId(), isControlMsg ? QueueStatus.DONE : QueueStatus.SENT);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return Response.ok().status(Response.Status.OK).build();
        }
        catch (Exception e2) {
            return Response.serverError().status(500).entity((Object)e2.getMessage()).build();
        }
    }
    
    public QueueOutService getQueueOutService() {
        if (this.queueOutService == null) {
            this.queueOutService = (QueueOutService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueOutService.class);
        }
        return this.queueOutService;
    }
    
    public ConnectionService getConnectionService() {
        if (this.connectionService == null) {
            this.connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        }
        return this.connectionService;
    }
    
    public BundleQueueService getBundleQueueService() {
        if (this.bundleQueueService == null) {
            this.bundleQueueService = (BundleQueueService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)BundleQueueService.class);
        }
        return this.bundleQueueService;
    }
    
    public QueueLogService getQueueLogService() {
        if (this.queueLogService == null) {
            this.queueLogService = (QueueLogService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueLogService.class);
        }
        return this.queueLogService;
    }
}
