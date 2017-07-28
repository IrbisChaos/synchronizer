// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.commons.lang3.StringUtils;
import java.net.ConnectException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import java.nio.charset.StandardCharsets;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import com.intenso.jira.plugins.synchronizer.utils.MimeTypeHelper;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.nio.charset.Charset;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.auth.UsernamePasswordCredentials;
import com.intenso.jira.plugins.synchronizer.utils.CryptoUtils;
import java.io.FileNotFoundException;
import org.apache.http.entity.ContentType;
import java.util.Iterator;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import java.io.File;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.IOException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpEntity;
import java.io.InputStream;
import org.apache.http.entity.InputStreamEntity;
import java.io.ByteArrayInputStream;
import org.apache.http.client.methods.HttpPost;
import com.intenso.jira.plugins.synchronizer.config.IssueSyncCloudUtil;
import com.intenso.jira.plugins.synchronizer.service.RemoteJiraType;
import com.atlassian.jira.issue.attachment.Attachment;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import org.springframework.beans.factory.DisposableBean;

public class CommunicatorServiceImpl implements CommunicatorService, DisposableBean
{
    public static final String REMOTE_REST_URL = "/rest/synchronizer/1.0/in/queuein";
    public static final String BUILD_IN_REST_URL = "/rest/api/2";
    public static final String HEADER_CONTROL_MSG = "X-JIRA-CONTROL-MSG";
    public static final String HEADER_TIMESTAMP = "X-ISSUE-SYNC-TIMESTAMP";
    public static final String HEADER_CONTROL_MSG_TRUE = "1";
    public static final String HEADER_CONTROL_MSG_COMMENT = "2";
    public static final String HEADER_CONTROL_MSG_IN = "3";
    private ExtendedLogger logger;
    private ConnectionService connectionService;
    private ApplicationProperties appProperties;
    private QueueLogService queueLogService;
    private QueueOutService queueOutService;
    private final SystemHttpClient systemHttpClient;
    
    public CommunicatorServiceImpl(final ConnectionService connectionService, final ApplicationProperties appProperties, final QueueLogService queueLogService, final QueueOutService queueOutService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.connectionService = connectionService;
        this.appProperties = appProperties;
        this.queueLogService = queueLogService;
        this.queueOutService = queueOutService;
        this.systemHttpClient = new SystemHttpClient();
    }
    
    @Override
    public Response send(final Connection connection, final Bundle bundle, final boolean isControlMsg, final boolean isCommentControlMsg, final boolean isINControlMsg) {
        return this.executeHttpPostRequest(connection, AttachmentUtils.compress(bundle), bundle.getAttachments(), isControlMsg, isCommentControlMsg, isINControlMsg);
    }
    
    @Override
    public Integer recognizeConnection(final HttpServletRequest request) {
        final String appKey = request.getHeader("X-JIRA-SYNC-APPKEY");
        final Connection connection = this.connectionService.findByAppKey(appKey);
        return connection.getID();
    }
    
    protected Response executeHttpPostRequest(final Connection connection, final byte[] result, final Map<Integer, List<Attachment>> attachments, final boolean isControlMsg, final boolean isCommentControlMsg, final boolean isINControlMsg) {
        String responseEntity = null;
        Integer status = null;
        CloseableHttpResponse httpResponse = null;
        final boolean isCloudJira = connection.getRemoteJiraType() != null && connection.getRemoteJiraType().equals(RemoteJiraType.CLOUD.ordinal());
        String url;
        if (isCloudJira) {
            url = IssueSyncCloudUtil.getCloudServerUrl("/receiver/server/queuein");
        }
        else {
            url = connection.getRemoteJiraURL() + "/rest/synchronizer/1.0/in/queuein";
        }
        final HttpPost request = (HttpPost)this.prepareExternalHttpHeaderRequest(url, HttpRequestMethod.POST, connection.getRemoteAuthKey(), isControlMsg, isCommentControlMsg, isINControlMsg);
        if (isCloudJira) {
            request.setHeader("X-JIRA-SYNC-REMOTE-URL", connection.getRemoteJiraURL());
        }
        request.setHeader("X-ISSUE-SYNC-TIMESTAMP", String.valueOf(System.currentTimeMillis()));
        ByteArrayInputStream stream = null;
        try {
            if (attachments != null && !attachments.isEmpty()) {
                final Map<String, File> bodies = AttachmentUtils.prepareAttachmentsForRequest(attachments);
                if (isCloudJira) {
                    this.prepareAttachmentRequestForCloud(result, request, bodies);
                }
                else {
                    this.prepareAttachmentRequestForServer(result, request, bodies);
                }
            }
            else {
                stream = new ByteArrayInputStream(result);
                final InputStreamEntity data = new InputStreamEntity(stream, -1L);
                request.addHeader("content-type", "application/octet-stream");
                request.setEntity(data);
            }
            httpResponse = this.systemHttpClient.execute(request, connection.getProxy());
            if (httpResponse.getEntity() != null) {
                responseEntity = this.streamToString(httpResponse.getEntity().getContent());
            }
            status = httpResponse.getStatusLine().getStatusCode();
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Response(500, e.getMessage());
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        final Response response = new Response(status, responseEntity);
        return response;
    }
    
    private void prepareAttachmentRequestForServer(final byte[] result, final HttpPost request, final Map<String, File> bodies) throws IOException {
        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        request.setHeader("Content-type", "multipart/form-data; boundary=--AaB03x");
        for (final String name : bodies.keySet()) {
            final File file = bodies.get(name);
            if (file.exists()) {
                final FileInputStream fis = new FileInputStream(file);
                entityBuilder.addBinaryBody(name, IOUtils.toByteArray(fis));
                if (fis == null) {
                    continue;
                }
                fis.close();
            }
        }
        entityBuilder.addBinaryBody("", result);
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.setBoundary("--AaB03x");
        final HttpEntity entity = entityBuilder.build();
        request.setEntity(entity);
    }
    
    private void prepareAttachmentRequestForCloud(final byte[] result, final HttpPost request, final Map<String, File> bodies) throws FileNotFoundException {
        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        for (final String name : bodies.keySet()) {
            final FileInputStream fileInputStream = new FileInputStream(bodies.get(name));
            entityBuilder.addBinaryBody(name, fileInputStream, ContentType.APPLICATION_OCTET_STREAM, name);
        }
        entityBuilder.addBinaryBody("bundle_data", new ByteArrayInputStream(result), ContentType.APPLICATION_OCTET_STREAM, "bundle_data");
        request.setEntity(entityBuilder.build());
    }
    
    private HttpRequestBase prepareExternalHttpHeaderRequest(final String url, final HttpRequestMethod httpRequestMethod, final String remoteAuthKey, final boolean isControlMsg, final boolean isCommentControlMsg, final boolean isINControlMsg) {
        final HttpRequestBase request = httpRequestMethod.getHttpRequest(url);
        request.setHeader("X-JIRA-SYNC-APPKEY", remoteAuthKey);
        request.setHeader("X-JIRA-SYNC-REQ", "1");
        if (url.startsWith(IssueSyncCloudUtil.getCloudServerUrl())) {
            request.setHeader("IssueSyncToken", IssueSyncCloudUtil.getSecretToken());
        }
        if (isControlMsg) {
            request.setHeader("X-JIRA-CONTROL-MSG", "1");
        }
        else if (isCommentControlMsg) {
            request.setHeader("X-JIRA-CONTROL-MSG", "2");
        }
        else if (isINControlMsg) {
            request.setHeader("X-JIRA-CONTROL-MSG", "3");
        }
        return request;
    }
    
    private HttpRequestBase prepareInternalHttpHeaderRequest(final String url, final HttpRequestMethod httpRequestMethod, final Connection connection) {
        return this.prepareInternalHttpHeaderRequest(url, httpRequestMethod, connection.getUsername(), connection.getPassword());
    }
    
    private HttpRequestBase prepareInternalHttpHeaderRequest(final String url, final HttpRequestMethod httpRequestMethod, final String username, final String password) {
        final HttpRequestBase request = httpRequestMethod.getHttpRequest(url);
        request.setHeader("X-JIRA-SYNC-REQ", "1");
        if (password != null && password != null && !password.isEmpty()) {
            final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, CryptoUtils.decrypt("com.intenso.jira.synchronizer", password));
            try {
                final HttpClientContext localContext = HttpClientContext.create();
                request.addHeader(new BasicScheme().authenticate(creds, request, localContext));
            }
            catch (AuthenticationException e1) {
                e1.printStackTrace();
            }
        }
        else if (username != null) {
            request.addHeader("X-JIRA-SYNC-APP-CONNECTION", username);
        }
        return request;
    }
    
    private String streamToString(final InputStream in) throws IOException {
        try {
            final InputStreamReader is = new InputStreamReader(in);
            final StringBuilder sb = new StringBuilder();
            final BufferedReader br = new BufferedReader(is);
            for (String read = br.readLine(); read != null; read = br.readLine()) {
                sb.append(read);
            }
            return sb.toString();
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public MessageType getMessageType(final HttpServletRequest request) {
        final String value = request.getHeader("X-JIRA-CONTROL-MSG");
        if (value != null && "1".equals(value)) {
            return MessageType.RESPONSE;
        }
        if (value != null && "2".equals(value)) {
            return MessageType.RESPONSE_COMMENT;
        }
        if (value != null && "3".equals(value)) {
            return MessageType.IN_RESPONSE;
        }
        return null;
    }
    
    @Override
    public Response callInternalRest(final Connection connection, final HttpRequestMethod method, final String address) {
        return this.callInternalRest(connection, method, address, null);
    }
    
    @Override
    public Response callInternalRestAttachments(final Connection connection, final String address, final List<AttachmentDTO> files, final boolean addPrefix) throws FileNotFoundException, IOException {
        final HttpPost request = (HttpPost)this.prepareInternalHttpHeaderRequest(((connection.getAlterBaseUrl() == null || connection.getAlterBaseUrl().isEmpty()) ? this.appProperties.getString("jira.baseurl") : connection.getAlterBaseUrl()) + "/rest/api/2" + address, HttpRequestMethod.POST, connection);
        request.addHeader("X-Atlassian-Token", "nocheck");
        final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setCharset(Charset.forName("UTF-8"));
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (final AttachmentDTO att : files) {
            final File file = att.getFile();
            if (file == null || !file.canRead()) {
                this.logger.error(ExtendedLoggerMessageType.COMM, "Unable to read file :" + att.getFilename() + "(" + att.getId() + ") no such file!");
            }
            else {
                final String remoteIdentifier = (att.getRemoteIssueKey() == null || att.getRemoteIssueId() == null) ? "" : (att.getRemoteIssueKey() + "(" + att.getRemoteIssueId() + ") ");
                final FileBody fileBody = new FileBody(file, MimeTypeHelper.getContentTypeByMime(att.getMimetype()), (addPrefix ? remoteIdentifier : "") + att.getFilename());
                entityBuilder.addPart("file", fileBody);
            }
        }
        final HttpEntity entity = entityBuilder.build();
        request.setEntity(entity);
        InputStream is = null;
        CloseableHttpResponse response = null;
        try {
            response = this.systemHttpClient.execute(request);
            final Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                this.logger.error(ExtendedLoggerMessageType.COMM, "There was an error while executing rest " + address + ", method: POST");
            }
            String result = null;
            if (response.getEntity() != null) {
                is = response.getEntity().getContent();
                result = this.streamToString(is);
            }
            return new Response(statusCode, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (response != null) {
                response.close();
            }
        }
        return null;
    }
    
    @Override
    public Response callInternalRest(final Connection connection, final HttpRequestMethod method, final String address, final String json) {
        CloseableHttpResponse response = null;
        final HttpRequestBase request = this.prepareInternalHttpHeaderRequest(((connection.getAlterBaseUrl() == null || connection.getAlterBaseUrl().isEmpty()) ? this.appProperties.getString("jira.baseurl") : connection.getAlterBaseUrl()) + "/rest/api/2" + address, method, connection);
        if (json != null) {
            request.addHeader("content-type", "application/json;charset=UTF-8");
            if (method.equals(HttpRequestMethod.POST)) {
                final HttpPost post = (HttpPost)request;
                try {
                    final StringEntity params = new StringEntity(json, "UTF-8");
                    post.setEntity(params);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (method.equals(HttpRequestMethod.PUT)) {
                final HttpPut put = (HttpPut)request;
                try {
                    final StringEntity params = new StringEntity(json, "UTF-8");
                    put.setEntity(params);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        InputStream is = null;
        try {
            response = this.systemHttpClient.execute(request);
            final Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                this.logger.error(ExtendedLoggerMessageType.COMM, "There was an error while executing rest " + address + ", method:" + method.name() + ", json: " + json);
            }
            String result = null;
            if (response.getEntity() != null) {
                is = response.getEntity().getContent();
                result = this.streamToString(is);
            }
            return new Response(statusCode, result);
        }
        catch (IllegalStateException e2) {
            e2.printStackTrace();
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                }
                catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public ConnectionService getConnectionService() {
        return this.connectionService;
    }
    
    public void setConnectionService(final ConnectionService connectionService) {
        this.connectionService = connectionService;
    }
    
    public QueueLogService getQueueLogService() {
        return this.queueLogService;
    }
    
    public void setQueueLogService(final QueueLogService queueLogService) {
        this.queueLogService = queueLogService;
    }
    
    public QueueOutService getQueueOutService() {
        return this.queueOutService;
    }
    
    public void setQueueOutService(final QueueOutService queueOutService) {
        this.queueOutService = queueOutService;
    }
    
    public void destroy() throws Exception {
        this.systemHttpClient.close();
    }
    
    @Override
    public Response callInternalRest(final String username, final String password, final HttpRequestMethod method, final String url) {
        final HttpRequestBase request = this.prepareInternalHttpHeaderRequest(url, method, username, password);
        try (final CloseableHttpResponse resp = this.systemHttpClient.execute(request)) {
            return new Response(resp);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Response callExternalRest(final String remoteJiraUrl, final String proxy, final HttpRequestMethod get, final String remoteAuthKey) {
        final HttpRequestBase request = this.prepareExternalHttpHeaderRequest(remoteJiraUrl, get, remoteAuthKey, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        try (final CloseableHttpResponse resp = this.systemHttpClient.execute(request, proxy)) {
            return new Response(resp);
        }
        catch (Exception e) {
            this.onHttpException(remoteJiraUrl, e);
            return null;
        }
    }
    
    @Override
    public Response callExternalRest(final String url, final String proxy, final String remoteAuthKey, final HttpRequestMethod method, final AbstractHttpEntity entity) {
        final HttpRequestBase request = this.prepareExternalHttpHeaderRequest(url, method, remoteAuthKey, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        entity.setContentEncoding(StandardCharsets.UTF_8.name());
        entity.setContentType(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
        if (method.equals(HttpRequestMethod.POST)) {
            ((HttpPost)request).setEntity(entity);
        }
        else {
            if (!method.equals(HttpRequestMethod.PUT)) {
                throw new UnsupportedOperationException();
            }
            ((HttpPut)request).setEntity(entity);
        }
        try (final CloseableHttpResponse response = this.systemHttpClient.execute(request, proxy)) {
            return new Response(response);
        }
        catch (Exception e) {
            this.onHttpException(url, e);
            return null;
        }
    }
    
    @Override
    public Response callExternalRest(final String url, final String proxy, final String remoteAuthKey, final HttpRequestMethod method, final UrlEncodedFormEntity entity, final Map<String, String> headers) {
        final HttpRequestBase request = this.prepareExternalHttpHeaderRequest(url, method, remoteAuthKey, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        if (method.equals(HttpRequestMethod.POST)) {
            ((HttpPost)request).setEntity(entity);
        }
        else {
            if (!method.equals(HttpRequestMethod.PUT)) {
                throw new UnsupportedOperationException();
            }
            ((HttpPut)request).setEntity(entity);
        }
        if (headers != null && headers.size() > 0) {
            for (final String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
        try (final CloseableHttpResponse response = this.systemHttpClient.execute(request, proxy)) {
            return new Response(response);
        }
        catch (Exception e) {
            this.onHttpException(url, e);
            return null;
        }
    }
    
    @Override
    public ResponseErrorsAware callExternalRest(final String url, final String proxy, final String remoteAuthKey, final HttpRequestMethod method, final Map<String, String> headers) {
        final HttpRequestBase request = this.prepareExternalHttpHeaderRequest(url, method, remoteAuthKey, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        if (headers != null && headers.size() > 0) {
            for (final String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
        try (final CloseableHttpResponse response = this.systemHttpClient.execute(request, proxy)) {
            return ResponseErrorsAware.with(new Response(response));
        }
        catch (Exception e) {
            this.onHttpException(url, e);
            return ResponseErrorsAware.with(e, url, proxy, headers);
        }
    }
    
    @Override
    public Response callExternalRest(final String url, final String proxy, final String remoteAuthKey, final HttpRequestMethod method, final Map<String, String> headers, final String json) {
        final HttpRequestBase request = this.prepareExternalHttpHeaderRequest(url, method, remoteAuthKey, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        if (!method.equals(HttpRequestMethod.POST)) {
            throw new UnsupportedOperationException();
        }
        try {
            final StringEntity entity = new StringEntity(json, "UTF-8");
            ((HttpPost)request).setEntity(entity);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (headers != null && headers.size() > 0) {
            for (final String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
        try (final CloseableHttpResponse response = this.systemHttpClient.execute(request, proxy)) {
            return new Response(response);
        }
        catch (Exception e) {
            this.onHttpException(url, e);
            return null;
        }
    }
    
    private void onHttpException(final String url, final Exception e) {
        final Throwable rootCause = ExceptionUtils.getRootCause((Throwable)e);
        final String msg = "Request failed to " + url + " with message: " + e.getMessage();
        if (rootCause != null && rootCause instanceof ConnectException && e.getCause().getMessage().equals("Connection refused: connect")) {
            this.logger.error(ExtendedLoggerMessageType.COMM, msg);
        }
        else {
            final String[] stackFrames = ExceptionUtils.getStackFrames((Throwable)e);
            this.logger.error(ExtendedLoggerMessageType.COMM, msg + " Stack trace: {{" + StringUtils.join((Object[])stackFrames, " \\n ") + "}}");
        }
    }
    
    public enum HttpRequestMethod
    {
        GET {
            @Override
            public HttpRequestBase getHttpRequest(final String url) {
                return new HttpGet(url);
            }
        }, 
        PUT {
            @Override
            public HttpRequestBase getHttpRequest(final String url) {
                return new HttpPut(url);
            }
        }, 
        POST {
            @Override
            public HttpRequestBase getHttpRequest(final String url) {
                return new HttpPost(url);
            }
        }, 
        DELETE {
            @Override
            public HttpRequestBase getHttpRequest(final String url) {
                return new HttpDelete(url);
            }
        };
        
        public abstract HttpRequestBase getHttpRequest(final String p0);
    }
}
