// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import org.boon.json.annotations.JsonProperty;

public class JIRAAttachmentResponse
{
    @JsonProperty("id")
    private Long id;
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("created")
    private String created;
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public String getCreated() {
        return this.created;
    }
    
    public void setCreated(final String created) {
        this.created = created;
    }
    
    public JIRAAttachmentResponse(final Long id, final String filename, final String created) {
        this.id = id;
        this.filename = filename;
        this.created = created;
    }
}
