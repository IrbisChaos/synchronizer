// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.atlassian.jira.issue.attachment.Attachment;
import org.boon.json.annotations.JsonIgnore;
import java.io.File;
import org.boon.json.annotations.JsonProperty;
import java.sql.Timestamp;

public class AttachmentDTO
{
    @JsonProperty("created")
    private Timestamp created;
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("filesize")
    private Long filesize;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("mimetype")
    private String mimetype;
    @JsonIgnore
    private File file;
    @JsonIgnore
    private String remoteIssueKey;
    @JsonIgnore
    private Long remoteIssueId;
    @JsonProperty("remoteAttachmentId")
    private Long remoteAttachmentId;
    @JsonProperty("remoteCreatedDate")
    private String remoteCreatedDate;
    
    public AttachmentDTO(final AttachmentDTOBuilder builder) {
        this.created = builder.getCreated();
        this.filename = builder.getFilename();
        this.filesize = builder.getFilesize();
        this.id = builder.getId();
        this.mimetype = builder.getMimetype();
        this.remoteIssueId = builder.getRemoteIssueId();
        this.remoteIssueKey = builder.getRemoteIssueKey();
    }
    
    public AttachmentDTO(final Attachment attachmentObject) {
        this.setCreated(attachmentObject.getCreated());
        this.setFilename(attachmentObject.getFilename());
        this.setFilesize(attachmentObject.getFilesize());
        this.setId(attachmentObject.getId());
        this.setMimetype(attachmentObject.getMimetype());
    }
    
    public Timestamp getCreated() {
        return this.created;
    }
    
    public void setCreated(final Timestamp created) {
        this.created = created;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public Long getFilesize() {
        return this.filesize;
    }
    
    public void setFilesize(final Long filesize) {
        this.filesize = filesize;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public String getMimetype() {
        return this.mimetype;
    }
    
    public void setMimetype(final String mimetype) {
        this.mimetype = mimetype;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public String getRemoteIssueKey() {
        return this.remoteIssueKey;
    }
    
    public void setRemoteIssueKey(final String remoteIssueKey) {
        this.remoteIssueKey = remoteIssueKey;
    }
    
    public Long getRemoteIssueId() {
        return this.remoteIssueId;
    }
    
    public void setRemoteIssueId(final Long remoteIssueId) {
        this.remoteIssueId = remoteIssueId;
    }
    
    public Long getRemoteAttachmentId() {
        return this.remoteAttachmentId;
    }
    
    public void setRemoteAttachmentId(final Long remoteAttachmentId) {
        this.remoteAttachmentId = remoteAttachmentId;
    }
    
    public String getRemoteCreatedDate() {
        return this.remoteCreatedDate;
    }
    
    public void setRemoteCreatedDate(final String remoteCreatedDate) {
        this.remoteCreatedDate = remoteCreatedDate;
    }
}
