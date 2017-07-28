// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

public enum QueueLogLevel
{
    INFO, 
    WARN, 
    ERROR;
    
    public static QueueLogLevel getQueueLogByHttpResponseStatus(final int status) {
        if (status >= 200 && status <= 204) {
            return QueueLogLevel.INFO;
        }
        return QueueLogLevel.ERROR;
    }
}
