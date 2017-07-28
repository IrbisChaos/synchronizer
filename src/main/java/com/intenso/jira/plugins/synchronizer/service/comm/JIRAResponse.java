// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.List;
import org.boon.json.annotations.JsonProperty;

public class JIRAResponse
{
    @JsonProperty("self")
    private String self;
    @JsonProperty("key")
    private String key;
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("errorMessages")
    private String[] errorMessages;
    @JsonProperty("attachments")
    private List<JIRAAttachmentResponse> attachments;
    
    public Long getIssueIdAsLong() {
        if (this.id != null && !this.id.isEmpty()) {
            try {
                return Long.parseLong(this.id);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public String getSelf() {
        return this.self;
    }
    
    public void setSelf(final String self) {
        this.self = self;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String[] getErrorMessages() {
        return this.errorMessages;
    }
    
    public void setErrorMessages(final String[] errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public List<JIRAAttachmentResponse> getAttachments() {
        return this.attachments;
    }
    
    public void setAttachments(final List<JIRAAttachmentResponse> attachments) {
        this.attachments = attachments;
    }
}
