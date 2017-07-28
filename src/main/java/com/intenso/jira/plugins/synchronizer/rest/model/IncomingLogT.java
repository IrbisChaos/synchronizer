// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.boon.json.annotations.JsonProperty;

public class IncomingLogT
{
    @JsonProperty("message")
    private String message;
    @JsonProperty("queueIn")
    private Integer queueIn;
    @JsonProperty("sourceQueueOutId")
    private Integer sourceQueueOutId;
    @JsonProperty("error")
    private String error;
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public Integer getQueueIn() {
        return this.queueIn;
    }
    
    public void setQueueIn(final Integer queueIn) {
        this.queueIn = queueIn;
    }
    
    public String getError() {
        return this.error;
    }
    
    public void setError(final String error) {
        this.error = error;
    }
    
    public Integer getSourceQueueOutId() {
        return this.sourceQueueOutId;
    }
    
    public void setSourceQueueOutId(final Integer sourceQueueOutId) {
        this.sourceQueueOutId = sourceQueueOutId;
    }
}
