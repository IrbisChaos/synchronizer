// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

public enum MessageType
{
    RESPONSE, 
    CREATE, 
    UPDATE, 
    COMMENT, 
    RESPONSE_COMMENT, 
    ATTACHMENT, 
    WORKFLOW, 
    IN_RESPONSE, 
    DELETE, 
    WORKLOG;
    
    public static MessageType getByOrdinal(final Integer ordinal) {
        if (ordinal < values().length) {
            return values()[ordinal];
        }
        return null;
    }
}
