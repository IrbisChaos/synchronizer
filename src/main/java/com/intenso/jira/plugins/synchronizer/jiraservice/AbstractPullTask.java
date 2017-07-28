// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import org.apache.http.client.utils.URLEncodedUtils;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import java.io.FileOutputStream;
import java.io.File;
import com.atlassian.jira.util.PathUtils;
import java.io.IOException;
import com.intenso.jira.plugins.synchronizer.service.comm.Bundle;
import java.io.InputStream;
import com.intenso.jira.plugins.synchronizer.service.comm.AttachmentUtils;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorServiceImpl;
import javax.ws.rs.core.Response;
import org.boon.Boon;
import org.apache.http.message.BasicNameValuePair;
import com.intenso.jira.plugins.synchronizer.utils.SettingUtils;
import com.intenso.jira.plugins.synchronizer.utils.LogsUtils;
import org.apache.http.NameValuePair;
import java.util.LinkedList;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.config.IssueSyncCloudUtil;
import com.intenso.jira.plugins.synchronizer.service.RemoteJiraType;
import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public abstract class AbstractPullTask extends AbstractTask
{
    private ExtendedLogger logger;
    
    public AbstractPullTask() {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
    }
    
    @Override
    public final void doExecute(final Map<String, Object> jobDataMap) {
        synchronized (this.getClass()) {
            this.logger.info(ExtendedLoggerMessageType.JOB, "syn003", jobDataMap.containsKey("FORCED") ? "(FORCED)" : "(SCHEDULED)");
            final List<Connection> connections = this.getConnectionService().getAll();
            for (final Connection connection : connections) {
                final Boolean status = this.doPull(connection);
                if (status == null) {
                    continue;
                }
                if (status) {
                    continue;
                }
                this.getLogger().error(ExtendedLoggerMessageType.JOB, "Pull job " + this.getClass().getSimpleName() + " problem for connection " + connection.getConnectionName() + " (" + connection.getID() + ")");
            }
        }
    }
    
    protected abstract Boolean doPull(final Connection p0);
    
    protected synchronized void sendResponse(final Connection connection, final List<IncomingLogT> logs, final boolean isControlMsg, final boolean isCommentControlMsg, final boolean isINControlMsg) {
        if (connection.getPassive() != null && connection.getPassive().equals(1)) {
            return;
        }
        String url;
        if (connection.getRemoteJiraType() != null && connection.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal())) {
            url = IssueSyncCloudUtil.getCloudServerUrl("/server/rest/api/1/pull/outMessages");
        }
        else {
            url = connection.getRemoteJiraURL() + "/rest/synchronizer/1.0/in/queueout";
        }
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        if (isControlMsg) {
            headers.put("X-JIRA-CONTROL-MSG", "1");
        }
        else if (isCommentControlMsg) {
            headers.put("X-JIRA-CONTROL-MSG", "2");
        }
        else if (isINControlMsg) {
            headers.put("X-JIRA-CONTROL-MSG", "3");
        }
        if (connection.getRemoteJiraType() != null && connection.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal())) {
            headers.put("X-JIRA-SYNC-REMOTE-URL", connection.getRemoteJiraURL());
        }
        final List<NameValuePair> params = new LinkedList<NameValuePair>();
        if (LogsUtils.logHasError(logs)) {
            params.add(new BasicNameValuePair("responseStatus", new Integer(SettingUtils.PARTIAL_ERROR_STATUS).toString()));
            params.add(new BasicNameValuePair("responseLogs", Boon.toJson(logs)));
        }
        else {
            params.add(new BasicNameValuePair("responseStatus", new Integer(Response.Status.OK.ordinal()).toString()));
            params.add(new BasicNameValuePair("responseLogs", Boon.toJson(logs)));
        }
        try {
            final com.intenso.jira.plugins.synchronizer.service.comm.Response response = this.getCommunicator().callExternalRest(url, connection.getProxy(), connection.getRemoteAuthKey(), CommunicatorServiceImpl.HttpRequestMethod.POST, new UrlEncodedFormEntity(params, "UTF-8"), headers);
            if (response == null) {
                this.logger.error(ExtendedLoggerMessageType.JOB, "Remote host " + connection.getRemoteJiraURL() + " is not reachable.");
                return;
            }
            if (response.getStatus() < 400) {
                this.logger.debug(ExtendedLoggerMessageType.JOB, "Pull Response was successfully sent.");
            }
            else {
                this.logger.error(ExtendedLoggerMessageType.JOB, "Unable to send Pull Response. Error code: " + response.getStatus());
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    protected synchronized List<IncomingLogT> pullData(final Connection connection, final QueueStatus status, final List<MessageType> messageType) throws IOException {
        if (connection.getPassive() != null && connection.getPassive().equals(1)) {
            return null;
        }
        final String remoteJiraUrl = this.prepareUrl(connection, status, messageType);
        final Map<String, String> headers = new HashMap<String, String>();
        if (connection.getRemoteJiraType() != null && connection.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal())) {
            headers.put("X-JIRA-SYNC-REMOTE-URL", connection.getRemoteJiraURL());
        }
        final com.intenso.jira.plugins.synchronizer.service.comm.Response response = this.getCommunicator().callExternalRest(remoteJiraUrl, connection.getProxy(), connection.getRemoteAuthKey(), CommunicatorServiceImpl.HttpRequestMethod.GET, headers).getResponse();
        if (response == null || response.getBytes() == null) {
            this.log.debug(ExtendedLoggerMessageType.JOB, "syn008", (response == null) ? "Unknown" : response.getStatus().toString(), (response == null) ? "Connection problem" : response.getJson());
            return new ArrayList<IncomingLogT>();
        }
        if (response.getStatus() >= 400) {
            this.log.debug(ExtendedLoggerMessageType.JOB, "syn002", response.getStatus().toString(), response.getJson());
            return null;
        }
        if (response.getStatus() == 210) {
            return null;
        }
        final InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        final Bundle bundle = AttachmentUtils.readBundle(inputStream, response.getContentType().getValue());
        final List<Long> attachments = this.getBundleQueueService().bundleAttachments(bundle);
        if (attachments != null && !attachments.isEmpty()) {
            for (final Long att : attachments) {
                this.pullFile(connection, att);
            }
        }
        final MessageType mt = messageType.contains(MessageType.RESPONSE) ? MessageType.RESPONSE : (messageType.contains(MessageType.RESPONSE_COMMENT) ? MessageType.RESPONSE_COMMENT : null);
        final List<IncomingLogT> logs = this.getBundleQueueService().saveIncomingBundle(connection.getID(), bundle, mt);
        return logs;
    }
    
    private void pullFile(final Connection connection, final Long attachment) {
        final Map<String, String> headers = new HashMap<String, String>();
        String url;
        if (connection.getRemoteJiraType() != null && connection.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal())) {
            url = IssueSyncCloudUtil.getCloudServerUrl("/server/rest/api/1/pull/attachment/" + attachment);
            headers.put("X-JIRA-SYNC-REMOTE-URL", connection.getRemoteJiraURL());
        }
        else {
            url = connection.getRemoteJiraURL() + "/rest/synchronizer/1.0/in/queueout/attachment/" + attachment;
        }
        final String path = PathUtils.joinPaths(new String[] { this.getAttachmentPathManager().getAttachmentPath(), "synchronized" });
        final File tempAttachmentFolder = new File(path);
        if (!tempAttachmentFolder.exists()) {
            tempAttachmentFolder.mkdirs();
        }
        final File tempAttachmentFile = new File(PathUtils.joinPaths(new String[] { path, attachment.toString() }));
        try (final FileOutputStream fos = new FileOutputStream(tempAttachmentFile)) {
            final com.intenso.jira.plugins.synchronizer.service.comm.Response response = this.getCommunicator().callExternalRest(url, connection.getProxy(), connection.getRemoteAuthKey(), CommunicatorServiceImpl.HttpRequestMethod.GET, headers).getResponse();
            if (response.getJson() != null) {
                IOUtils.copy(IOUtils.toInputStream(response.getJson()), fos);
            }
            else {
                IOUtils.copy(new ByteArrayInputStream(response.getBytes()), fos);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String prepareUrl(final Connection connection, final QueueStatus queueStatus, final List<MessageType> msgTypes) {
        String url;
        if (connection.getRemoteJiraType() != null && connection.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal())) {
            url = IssueSyncCloudUtil.getCloudServerUrl("/server/rest/api/1/pull/outMessages?");
        }
        else {
            url = connection.getRemoteJiraURL() + "/rest/synchronizer/1.0/in/queueout?";
        }
        final List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("status", new Integer(QueueStatus.NEW.ordinal()).toString()));
        for (final MessageType mt : msgTypes) {
            params.add(new BasicNameValuePair("msgTypes", new Integer(mt.ordinal()).toString()));
        }
        final String paramString = URLEncodedUtils.format(params, "utf-8");
        url += paramString;
        return url;
    }
    
    public ExtendedLogger getLogger() {
        return this.logger;
    }
}
