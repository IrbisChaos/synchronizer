// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.sql.Timestamp;

public class AttachmentDTOBuilder
{
    private Timestamp created;
    private String filename;
    private Long filesize;
    private Long id;
    private String mimetype;
    private Long remoteIssueId;
    private String remoteIssueKey;
    
    public AttachmentDTO build() {
        return new AttachmentDTO(this);
    }
    
    public Timestamp getCreated() {
        return this.created;
    }
    
    public AttachmentDTOBuilder created(final Timestamp created) {
        this.created = created;
        return this;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public AttachmentDTOBuilder filename(final String filename) {
        this.filename = filename;
        return this;
    }
    
    public Long getFilesize() {
        return this.filesize;
    }
    
    public AttachmentDTOBuilder filesize(final Long filesize) {
        this.filesize = filesize;
        return this;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public AttachmentDTOBuilder id(final Long id) {
        this.id = id;
        return this;
    }
    
    public String getMimetype() {
        return this.mimetype;
    }
    
    public AttachmentDTOBuilder mimetype(final String mimetype) {
        this.mimetype = mimetype;
        return this;
    }
    
    public Long getRemoteIssueId() {
        return this.remoteIssueId;
    }
    
    public AttachmentDTOBuilder remoteIssueId(final Long remoteIssueId) {
        this.remoteIssueId = remoteIssueId;
        return this;
    }
    
    public String getRemoteIssueKey() {
        return this.remoteIssueKey;
    }
    
    public AttachmentDTOBuilder remoteIssueKey(final String remoteIssueKey) {
        this.remoteIssueKey = remoteIssueKey;
        return this;
    }
}
