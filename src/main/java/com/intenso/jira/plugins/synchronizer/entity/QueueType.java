// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

public enum QueueType
{
    IN, 
    OUT;
    
    public static QueueType getByOrdinal(final Integer ordinal) {
        if (ordinal < values().length) {
            return values()[ordinal];
        }
        return null;
    }
}
