// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.atlassian.jira.config.util.AttachmentPathManager;
import org.codehaus.jackson.smile.SmileParser;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.smile.SmileFactory;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.atlassian.jira.util.PathUtils;
import org.apache.commons.fileupload.MultipartStream;
import java.io.InputStream;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.QueueLogLevel;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.AttachmentManager;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.atlassian.jira.issue.attachment.FileAttachments;
import com.atlassian.jira.component.ComponentAccessor;
import java.io.File;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public class AttachmentUtils
{
    public static final String BOUNDARY = "--AaB03x";
    private static ExtendedLogger logger;
    
    public static File prepareAttachmentForRequest(final Long attachmentId) {
        final AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
        final Attachment att = attachmentManager.getAttachment(attachmentId);
        final File root = new File(getAttachmentPathManager().getAttachmentPath());
        final File issueAttachmentFolder = FileAttachments.getAttachmentDirectoryForIssue(root, att.getIssueObject().getProjectObject().getOriginalKey(), att.getIssueObject().getKey());
        final File attachmentFile = new File(issueAttachmentFolder, att.getId().toString());
        if (attachmentFile.exists() && attachmentFile.canRead()) {
            return attachmentFile;
        }
        AttachmentUtils.logger.error(ExtendedLoggerMessageType.COMM, "syn054", String.valueOf(attachmentId));
        return null;
    }
    
    public static Map<String, File> prepareAttachmentsForRequest(final Map<Integer, List<Attachment>> attachments) {
        final Map<String, File> attachmentsBodies = new HashMap<String, File>();
        for (final Integer queueOutId : attachments.keySet()) {
            for (final Attachment attachmentObject : attachments.get(queueOutId)) {
                AttachmentUtils.logger.error(ExtendedLoggerMessageType.COMM, "attachments: " + attachmentObject.getIssueObject().getProjectObject().getProjectTypeKey().getKey());
                final File root = new File(getAttachmentPathManager().getAttachmentPath());
                final File issueAttachmentFolder = FileAttachments.getAttachmentDirectoryForIssue(root, attachmentObject.getIssueObject().getProjectObject().getOriginalKey(), attachmentObject.getIssueObject().getKey());
                final File attachmentFile = new File(issueAttachmentFolder, attachmentObject.getId().toString());
                if (attachmentFile.exists() && attachmentFile.canRead()) {
                    attachmentsBodies.put(attachmentObject.getId().toString(), attachmentFile);
                }
                else {
                    final QueueOut qo = getQueueOutService().get(queueOutId);
                    getQueueLogService().createQueueLog(QueueType.OUT, QueueLogLevel.ERROR, "Project: " + attachmentObject.getIssueObject().getProjectObject().getId() + "(" + attachmentObject.getIssueObject().getProjectObject().getKey() + ")" + "Attachment " + attachmentObject.getFilename() + "in " + attachmentFile.getPath() + " " + (attachmentFile.exists() ? " not found" : " cannot be read") + "!", null, queueOutId, qo.getContractId(), qo.getIssueId(), 500);
                }
            }
        }
        return attachmentsBodies;
    }
    
    public static Bundle readMultipart(final InputStream requestInputStream, final String contentType) throws IOException {
        Bundle bundle = null;
        final String boundary = extractBoundary(contentType);
        if (boundary == null) {
            AttachmentUtils.logger.error(ExtendedLoggerMessageType.COMM, "syn053");
            return null;
        }
        final ObjectMapper mapper = prepareSmileObjectMapper(Bundle.class);
        final MultipartStream multipartStream = new MultipartStream(requestInputStream, boundary.getBytes());
        for (boolean nextPart = multipartStream.skipPreamble(); nextPart; nextPart = multipartStream.readBoundary()) {
            final String header = multipartStream.readHeaders();
            final String[] pairs = header.split(";");
            String name = null;
            if (pairs != null) {
                for (int i = 0; i < pairs.length; ++i) {
                    if (pairs[i].replaceAll("\\s+", "").startsWith("name=")) {
                        name = pairs[i].replaceAll("name=", "");
                        name = name.replaceAll("\"", "");
                        name = name.replaceAll("\\s+", "");
                        break;
                    }
                }
            }
            if (name != null && !name.isEmpty() && !name.equals("bundle_data")) {
                FileOutputStream fos = null;
                try {
                    final String path = PathUtils.joinPaths(new String[] { getAttachmentPathManager().getAttachmentPath(), "synchronized" });
                    final File tempAttachmentFolder = new File(path);
                    if (!tempAttachmentFolder.exists()) {
                        tempAttachmentFolder.mkdirs();
                    }
                    final File tempAttachmentFile = new File(PathUtils.joinPaths(new String[] { path, name }));
                    fos = new FileOutputStream(tempAttachmentFile);
                    multipartStream.readBodyData(fos);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
            else {
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                multipartStream.readBodyData(outputStream);
                bundle = (Bundle)mapper.readValue(outputStream.toByteArray(), (Class)Bundle.class);
            }
        }
        return bundle;
    }
    
    public static String extractBoundary(final String line) {
        final int index = line.indexOf("boundary=");
        if (index == -1) {
            return null;
        }
        final String boundary = line.substring(index + 9);
        return boundary;
    }
    
    public static Bundle readSimple(final InputStream requestInputStream) {
        final ObjectMapper mapper = prepareSmileObjectMapper(Bundle.class);
        if (requestInputStream == null) {
            AttachmentUtils.logger.warn(ExtendedLoggerMessageType.COMM, "syn049");
        }
        Bundle bundle = null;
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(requestInputStream);
            if (bytes == null || bytes.length == 0) {
                AttachmentUtils.logger.warn(ExtendedLoggerMessageType.COMM, "syn050");
            }
            else {
                bundle = (Bundle)mapper.readValue(bytes, (Class)Bundle.class);
                if (bundle != null && bundle.getMessages() != null && !bundle.getMessages().isEmpty()) {
                    AttachmentUtils.logger.debug(ExtendedLoggerMessageType.COMM, "syn051", bundle.toString());
                }
            }
        }
        catch (Exception e) {
            logByteArray(bytes);
            e.printStackTrace();
            if (requestInputStream != null) {
                try {
                    requestInputStream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        finally {
            if (requestInputStream != null) {
                try {
                    requestInputStream.close();
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return bundle;
    }
    
    private static void logByteArray(final byte[] bytes) {
        if (bytes != null) {
            try {
                AttachmentUtils.logger.debug(ExtendedLoggerMessageType.COMM, "syn051", new String(bytes, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                AttachmentUtils.logger.warn(ExtendedLoggerMessageType.COMM, "syn052", e.getMessage());
            }
        }
    }
    
    public static Bundle readBundle(final InputStream requestInputStream, final String contentType) throws IOException {
        if (contentType.startsWith("multipart/form-data")) {
            return readMultipart(requestInputStream, contentType);
        }
        return readSimple(requestInputStream);
    }
    
    private static ObjectMapper prepareSmileObjectMapper(final Class clazz) {
        final SmileFactory f = new SmileFactory();
        final ObjectMapper mapper = new ObjectMapper((JsonFactory)f);
        f.configure(SmileParser.Feature.REQUIRE_HEADER, false);
        mapper.registerSubtypes(new Class[] { clazz });
        return mapper;
    }
    
    public static byte[] compress(final Bundle bundle) {
        final SmileFactory f = new SmileFactory();
        final ObjectMapper mapper = new ObjectMapper((JsonFactory)f);
        mapper.registerSubtypes(new Class[] { Bundle.class });
        byte[] result = null;
        try {
            result = mapper.writeValueAsBytes((Object)bundle);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static QueueLogService getQueueLogService() {
        return (QueueLogService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueLogService.class);
    }
    
    public static AttachmentPathManager getAttachmentPathManager() {
        return (AttachmentPathManager)ComponentAccessor.getComponent((Class)AttachmentPathManager.class);
    }
    
    public static QueueOutService getQueueOutService() {
        return (QueueOutService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueOutService.class);
    }
    
    static {
        AttachmentUtils.logger = ExtendedLoggerFactory.getLogger(AttachmentUtils.class);
    }
}
