// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

public enum QueueDefaultLogText
{
    NEW_ADDED("Local issue changed, passed to synchronization"), 
    SEND_RESPONSE("Sending response to remote JIRA");
    
    private String text;
    
    private QueueDefaultLogText(final String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
}
