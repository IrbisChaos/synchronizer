// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.atlassian.jira.util.PathUtils;
import java.io.File;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.Timestamp;
import com.google.gson.JsonParser;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import org.boon.Boon;
import com.atlassian.jira.util.json.JSONException;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.rest.api.issue.IssueUpdateRequest;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.atlassian.jira.user.ApplicationUser;
import com.intenso.jira.plugins.synchronizer.rest.model.WorklogT;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.worklog.Worklog;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import org.apache.commons.lang3.StringEscapeUtils;
import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.boon.json.implementation.ObjectMapperImpl;
import org.boon.json.JsonSerializerFactory;
import java.nio.charset.StandardCharsets;
import org.boon.json.JsonParserFactory;
import org.boon.json.ObjectMapper;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public class MessageComposerServiceImpl implements MessageComposerService
{
    private static final ExtendedLogger log;
    private final CustomFieldManager cfManager;
    private final IssueManager issueManager;
    private final FieldManager fieldManager;
    private final AttachmentPathManager attachmentPathManager;
    
    public MessageComposerServiceImpl(final CustomFieldManager cfManager, final IssueManager issueManager, final FieldManager fieldManager, final AttachmentPathManager attachmentPathManager) {
        this.cfManager = cfManager;
        this.issueManager = issueManager;
        this.fieldManager = fieldManager;
        this.attachmentPathManager = attachmentPathManager;
    }
    
    private ObjectMapper createMapper() {
        final JsonParserFactory jpf = new JsonParserFactory().setCharset(StandardCharsets.ISO_8859_1);
        jpf.useAnnotations();
        final JsonSerializerFactory jsf = new JsonSerializerFactory();
        jsf.useAnnotations();
        jsf.setEncodeStrings(false);
        jsf.setAsciiOnly(false);
        return new ObjectMapperImpl(jpf, jsf);
    }
    
    @Override
    public String toJSONString(final Object dto) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ObjectMapper mapper = this.createMapper();
        mapper.writeValue(output, dto);
        return mapper.writeValueAsString(dto);
    }
    
    @Override
    public String buildInternalJSON(final Contract contract, final Comment comment, final MessageType msgType, final List<SyncIssue> remoteIssues) {
        final IssueIntDTO dto = new IssueIntDTO();
        final CommentT commentT = new CommentT(comment, null);
        final String escape = StringEscapeUtils.escapeJson(commentT.getComment());
        commentT.setComment(escape);
        dto.setComment(commentT);
        if (remoteIssues != null && remoteIssues.size() > 0) {
            dto.setRemoteIssueId(remoteIssues.get(0).getRemoteIssueId());
            dto.setRemoteIssueKey(remoteIssues.get(0).getRemoteIssueKey());
        }
        dto.setContractId(contract.getID());
        dto.setRemoteContractName(contract.getRemoteContextName());
        dto.setMsgType(msgType.ordinal());
        final Issue issue = (Issue)this.issueManager.getIssueObject(comment.getIssueId());
        dto.setIssueId(issue.getId());
        return this.toJSONString(dto);
    }
    
    @Override
    public String buildInternalJSON(final Contract contract, final IssueEvent issueEvent, final MessageType msgType, final List<ContractChangeItem> changes, final SyncIssue remoteIssue) {
        return this.buildInternalJSON(contract, issueEvent.getIssue(), msgType, changes, remoteIssue);
    }
    
    @Override
    public String buildInternalJSON(final Contract contract, final Issue issue, final MessageType msgType, final List<ContractChangeItem> changes, final SyncIssue remoteIssue) {
        final IssueIntDTO dto = new IssueIntDTO();
        dto.setChanges(changes);
        if (remoteIssue != null) {
            dto.setIssueId(remoteIssue.getRemoteIssueId());
        }
        dto.setContractId(contract.getID());
        dto.setRemoteContractName(contract.getRemoteContextName());
        dto.setMsgType(msgType.ordinal());
        if (issue.getParentObject() != null) {
            dto.setRemoteParentIssueId(issue.getParentId());
            dto.setRemoteParentIssueKey(issue.getParentObject().getKey());
        }
        dto.setRemoteIssueId(issue.getId());
        dto.setRemoteIssueKey(issue.getKey());
        return this.toJSONString(dto);
    }
    
    @Override
    public String buildInternalJSON(final Contract contract, final Worklog worklog, final Issue issue, final MessageType msgType, final SyncIssue remoteIssue) {
        final IssueIntDTO dto = new IssueIntDTO();
        final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        final WorklogT worklogT = new WorklogT(worklog, user);
        final String escape = StringEscapeUtils.escapeJson(worklogT.getComment());
        worklogT.setComment(escape);
        dto.setWorklog(worklogT);
        if (remoteIssue != null) {
            dto.setIssueId(remoteIssue.getRemoteIssueId());
        }
        dto.setContractId(contract.getID());
        dto.setRemoteContractName(contract.getRemoteContextName());
        dto.setMsgType(msgType.ordinal());
        dto.setRemoteIssueId(issue.getId());
        dto.setRemoteIssueKey(issue.getKey());
        return this.toJSONString(dto);
    }
    
    @Override
    public String buildJSONTransition(final String transitionId) {
        final StringBuilder builder = new StringBuilder();
        builder.append("{\"transition\": {\"id\": \"").append(transitionId).append("\"}}");
        return builder.toString();
    }
    
    @Override
    public String buildJSONTransition(final String transitionId, final String resolution) {
        final StringBuilder builder = new StringBuilder();
        builder.append("{\"transition\": {\"id\": \"").append(transitionId).append("\"},\"fields\": {\"resolution\": {\"name\": \"").append(resolution).append("\"}}}");
        return builder.toString();
    }
    
    @Override
    public String buildJSON(final IssueIntDTO dto, final List<ContractFieldMappingEntry> fieldMappingEntries) {
        final List<ContractChangeItem> properChanges = new ArrayList<ContractChangeItem>();
        final IssueFieldsCreator issueFields = new IssueFieldsCreator();
        final List<ContractChangeItem> changes = dto.getChanges();
        try {
            for (final ContractChangeItem cci : changes) {
                final String requestedFieldName = cci.getFieldName();
                for (final ContractFieldMappingEntry fme : fieldMappingEntries) {
                    if (cci.getType().equals(FieldType.TYPE_STATUS)) {
                        continue;
                    }
                    if (fme.getLocalFieldName() != null && fme.getLocalFieldName().equals(requestedFieldName)) {
                        cci.setFieldName(fme.getLocalFieldId());
                        properChanges.add(cci);
                        break;
                    }
                    if (!cci.getFieldName().equals(IssueFieldsCreator.PROJECT_FIELD_ID) && !cci.getFieldName().equals(IssueFieldsCreator.ISSUETYPE_FIELD_ID)) {
                        continue;
                    }
                    properChanges.add(cci);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final List<Contract> contracts = ((ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class)).findByContractName(dto.getRemoteContractName());
        issueFields.build(properChanges, contracts, dto.getParentIssueId());
        IssueUpdateRequest req = new IssueUpdateRequest();
        req = req.fields(issueFields.getIssueFields());
        final org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
        JSONObject jsonObject = null;
        try {
            final String reqs = mapper.writeValueAsString((Object)req);
            jsonObject = new JSONObject(reqs);
        }
        catch (Exception e2) {
            e2.printStackTrace();
            MessageComposerServiceImpl.log.error(ExtendedLoggerMessageType.JOB, "CreateJSON error: " + e2.getMessage());
        }
        jsonObject = this.handleDueDate(jsonObject);
        return (jsonObject == null) ? null : jsonObject.toString();
    }
    
    private JSONObject handleDueDate(JSONObject jsonObject) {
        try {
            final JSONObject fields = jsonObject.getJSONObject("fields");
            if (fields.has("duedate")) {
                final String dueDate = fields.optString("duedate");
                if (dueDate.equals("")) {
                    jsonObject = jsonObject.put("fields", (Object)fields.put("duedate", (Object)JSONObject.NULL));
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String buildResponseInternalJSON(final Contract contract, final JIRAResponse respObj, final Integer matchQueueId, final Integer status) {
        final QueueOutResponseDTO responseDTO = new QueueOutResponseDTO();
        responseDTO.setRemoteContractName(contract.getContractName());
        responseDTO.setRemoteIssueId(Long.parseLong(respObj.getId()));
        responseDTO.setRemoteIssueKey(respObj.getKey());
        responseDTO.setContractName(contract.getRemoteContextName());
        if (respObj.getAttachments() != null && respObj.getAttachments().size() > 0) {
            responseDTO.setAttachments(respObj.getAttachments().toArray(new JIRAAttachmentResponse[respObj.getAttachments().size()]));
        }
        if (status != null) {
            responseDTO.setStatus(status);
        }
        if (status != null) {
            responseDTO.setMatchQueueId(matchQueueId);
        }
        return this.toJSONString(responseDTO);
    }
    
    @Override
    public String buildResponseInternalJSON(final Contract contract, final JIRAResponse respObj, final Comment comment) {
        final QueueOutResponseDTO responseDTO = new QueueOutResponseDTO();
        responseDTO.setRemoteContractName(contract.getContractName());
        responseDTO.setRemoteIssueId(Long.parseLong(respObj.getId()));
        responseDTO.setRemoteIssueKey(respObj.getKey());
        responseDTO.setContractName(contract.getRemoteContextName());
        responseDTO.setRemoteCommentDate(comment.getDateInternal());
        responseDTO.setRemoteCommentId(comment.getID());
        responseDTO.setCommentId(comment.getRemoteCommentId());
        return this.toJSONString(responseDTO);
    }
    
    @Override
    public JIRAResponse parseJIRAResponse(final Response response) {
        if (response != null && response.getJson() != null) {
            final JIRAResponse respObj = Boon.fromJson(response.getJson(), JIRAResponse.class);
            return respObj;
        }
        return null;
    }
    
    @Override
    public IssueIntDTO toIssueIntDTO(final String text) {
        return Boon.fromJson(text, IssueIntDTO.class);
    }
    
    public FieldManager getFieldManager() {
        return this.fieldManager;
    }
    
    @Override
    public List<AttachmentDTO> buildAttachments(final Contract contract, final QueueIn entry) {
        final List<AttachmentDTO> attachments = new ArrayList<AttachmentDTO>();
        final JsonParser gson = new JsonParser();
        final JsonObject je = gson.parse(entry.getJsonMsg()).getAsJsonObject();
        final Long remoteIssueId = je.has("remoteIssueId") ? Long.parseLong(je.get("remoteIssueId").getAsString()) : null;
        final String remoteIssueKey = je.has("remoteIssueKey") ? je.get("remoteIssueKey").getAsString() : null;
        final JsonArray ja = je.get("changes").getAsJsonArray();
        for (int i = 0; i < ja.size(); ++i) {
            final JsonObject jo = ja.get(i).getAsJsonObject();
            final JsonArray attachmentsArr = jo.get("values").getAsJsonArray();
            if (attachmentsArr != null) {
                for (int j = 0; j < attachmentsArr.size(); ++j) {
                    try {
                        if (attachmentsArr.get(j).isJsonObject()) {
                            final JsonObject attachment = attachmentsArr.get(j).getAsJsonObject();
                            final Long id = Long.parseLong(attachment.get("id").toString());
                            final AttachmentDTO attDto = new AttachmentDTOBuilder().id(id).created(attachment.has("created") ? new Timestamp(Long.parseLong(attachment.get("created").toString())) : null).filename(attachment.has("filename") ? attachment.get("filename").getAsString() : null).filesize(attachment.has("filesize") ? Long.parseLong(attachment.get("filesize").toString()) : null).mimetype(attachment.has("mimetype") ? attachment.get("mimetype").getAsString() : null).remoteIssueId(remoteIssueId).remoteIssueKey(remoteIssueKey).build();
                            attDto.setFile(this.getSynchronizedFile(id));
                            attachments.add(attDto);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return attachments;
    }
    
    @Override
    public List<JIRAAttachmentResponse> parse(final Response response) {
        final List<JIRAAttachmentResponse> responses = new ArrayList<JIRAAttachmentResponse>();
        final JsonParser gson = new JsonParser();
        final JsonArray je = gson.parse(response.getJson()).getAsJsonArray();
        if (je != null) {
            for (int i = 0; i < je.size(); ++i) {
                final JsonObject att = je.get(i).getAsJsonObject();
                if (att.has("id") && att.has("filename") && att.has("created")) {
                    responses.add(new JIRAAttachmentResponse(Long.parseLong(att.get("id").getAsString()), att.get("filename").getAsString(), att.get("created").getAsString()));
                }
            }
        }
        return responses;
    }
    
    public File getSynchronizedFile(final Long id) {
        final String path = PathUtils.joinPaths(new String[] { this.attachmentPathManager.getAttachmentPath(), "synchronized", id.toString() });
        final File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            return null;
        }
        return file;
    }
    
    public CustomFieldManager getCfManager() {
        return this.cfManager;
    }
    
    public IssueManager getIssueManager() {
        return this.issueManager;
    }
    
    public AttachmentPathManager getAttachmentPathManager() {
        return this.attachmentPathManager;
    }
    
    static {
        log = ExtendedLoggerFactory.getLogger(MessageComposerServiceImpl.class);
    }
}
