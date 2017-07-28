// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import org.slf4j.LoggerFactory;
import java.io.File;
import com.atlassian.jira.util.PathUtils;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.atlassian.jira.issue.attachment.Attachment;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.issue.AttachmentManager;
import org.slf4j.Logger;

public class BundleQueueServiceImpl implements BundleQueueService
{
    private static final Logger log;
    private final QueueInService queueInService;
    private final QueueLogService queueLogService;
    private final AttachmentManager attachmentManager;
    private final AttachmentPathManager attachmentPathManager;
    public static final String TEMP_SYNC_ATTACHMENT_FOLDER = "synchronized";
    private ExtendedLogger logger;
    
    public BundleQueueServiceImpl(final QueueInService queueInService, final QueueLogService queueLogService, final AttachmentManager attachmentManager, final AttachmentPathManager attachmentPathManager) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.queueLogService = queueLogService;
        this.queueInService = queueInService;
        this.attachmentManager = attachmentManager;
        this.attachmentPathManager = attachmentPathManager;
    }
    
    @Override
    public List<Long> bundleAttachments(final Bundle bundle) {
        final List<Long> attachments = new ArrayList<Long>();
        if (bundle == null || bundle.getMessages() == null) {
            return attachments;
        }
        for (final String[] msList : bundle.getMessages()) {
            JsonObject je = null;
            String message = null;
            try {
                if (msList == null || msList.length < 2) {
                    continue;
                }
                message = msList[1];
                final JsonParser gson = new JsonParser();
                je = gson.parse(message).getAsJsonObject();
                if (!je.has("changes")) {
                    continue;
                }
            }
            catch (Exception e) {
                BundleQueueServiceImpl.log.error(e.getMessage(), (Throwable)e);
                BundleQueueServiceImpl.log.error("invalid message: " + message);
                this.logger.error(ExtendedLoggerMessageType.JOB, "Exception occurred:" + e.getMessage());
            }
            if (je != null) {
                final JsonArray ja = je.get("changes").getAsJsonArray();
                for (int i = 0; i < ja.size(); ++i) {
                    try {
                        final JsonObject jo = ja.get(i).getAsJsonObject();
                        if (jo.has("values")) {
                            final JsonArray attachmentsArr = jo.get("values").getAsJsonArray();
                            if (attachmentsArr != null) {
                                for (int j = 0; j < attachmentsArr.size(); ++j) {
                                    final JsonObject attachment = attachmentsArr.get(j).getAsJsonObject();
                                    final Long id = Long.parseLong(attachment.get("id").toString());
                                    attachments.add(id);
                                }
                            }
                        }
                    }
                    catch (Exception e2) {
                        BundleQueueServiceImpl.log.error(e2.getMessage(), (Throwable)e2);
                        this.logger.error(ExtendedLoggerMessageType.OTHER, "Exception occurred:" + e2.getMessage());
                    }
                }
            }
        }
        return attachments;
    }
    
    private List<Attachment> bundleAttachments(final QueueOut entry) {
        final List<Attachment> attachments = new ArrayList<Attachment>();
        final JsonParser gson = new JsonParser();
        final JsonObject je = gson.parse(entry.getJsonMsg()).getAsJsonObject();
        final JsonArray ja = je.get("changes").getAsJsonArray();
        for (int i = 0; i < ja.size(); ++i) {
            try {
                final JsonObject jo = ja.get(i).getAsJsonObject();
                final JsonArray attachmentsArr = jo.get("values").getAsJsonArray();
                if (attachmentsArr != null) {
                    for (int j = 0; j < attachmentsArr.size(); ++j) {
                        final JsonObject attachment = attachmentsArr.get(j).getAsJsonObject();
                        final Long id = Long.parseLong(attachment.get("id").toString());
                        final Attachment attachmentObject = this.attachmentManager.getAttachment(id);
                        if (attachmentObject != null) {
                            attachments.add(attachmentObject);
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return attachments;
    }
    
    @Override
    public Bundle createBundle(final Integer connectionId, final List<QueueOut> queue, final boolean isControlMsg) {
        final Bundle bundle = new Bundle();
        for (final QueueOut entry : queue) {
            if (entry.getMsgType().equals(MessageType.ATTACHMENT.ordinal())) {
                if (!bundle.getAttachments().containsKey(new Integer(entry.getID()))) {
                    bundle.getAttachments().put(entry.getID(), new ArrayList<Attachment>());
                }
                bundle.getAttachments().get(entry.getID()).addAll(this.bundleAttachments(entry));
            }
            String extraId;
            if (isControlMsg) {
                extraId = "" + entry.getMatchQueueId();
            }
            else {
                extraId = "" + entry.getID();
            }
            bundle.getMessages().add(new String[] { extraId, entry.getJsonMsg(), new Integer(entry.getID()).toString() });
        }
        return bundle;
    }
    
    @Override
    public Map<Integer, List<QueueOut>> splitQueueByConnection(final List<QueueOut> queue) {
        final Map<Integer, List<QueueOut>> result = new HashMap<Integer, List<QueueOut>>();
        for (final QueueOut entry : queue) {
            List<QueueOut> list = result.get(entry.getConnectionId());
            if (list == null) {
                list = new ArrayList<QueueOut>();
                result.put(entry.getConnectionId(), list);
            }
            list.add(entry);
        }
        return result;
    }
    
    @Override
    public List<IncomingLogT> saveIncomingBundle(final Integer connectionId, final Bundle bundle, final MessageType msgType) {
        final List<IncomingLogT> log = new ArrayList<IncomingLogT>();
        if (bundle != null) {
            for (final String[] msg : bundle.getMessages()) {
                final IncomingLogT logEntry = new IncomingLogT();
                QueueIn qin = null;
                try {
                    if (msg != null && msg.length > 2) {
                        logEntry.setSourceQueueOutId(Integer.parseInt(msg[2]));
                    }
                    logEntry.setMessage(msg[1]);
                    final Integer matchQueueEntryId = Integer.parseInt(msg[0]);
                    qin = this.queueInService.create(null, connectionId, msgType, msg[1], matchQueueEntryId);
                    this.queueLogService.createQueueLog(Integer.valueOf(200), "New incoming task", qin.getJsonMsg(), qin);
                    logEntry.setQueueIn(qin.getID());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    logEntry.setError(e.getMessage());
                }
                log.add(logEntry);
            }
        }
        return log;
    }
    
    public void initTmpStorage() {
        final String folderPath = PathUtils.joinPaths(new String[] { this.attachmentPathManager.getAttachmentPath(), "synchronized" });
        final File folderFile = new File(folderPath);
        if (!folderFile.exists() || !folderFile.isDirectory()) {
            folderFile.mkdirs();
        }
    }
    
    static {
        log = LoggerFactory.getLogger((Class)BundleQueueServiceImpl.class);
    }
}
