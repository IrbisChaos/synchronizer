// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

public enum ExtendedLoggerMessageType
{
    CFG(0), 
    EVENT(1), 
    JOB(2), 
    REST(3), 
    COMM(4), 
    OTHER(5);
    
    private int value;
    
    private ExtendedLoggerMessageType(final int value) {
        this.value = value;
    }
    
    static ExtendedLoggerMessageType fromValue(final int value) {
        for (final ExtendedLoggerMessageType my : values()) {
            if (my.value == value) {
                return my;
            }
        }
        return ExtendedLoggerMessageType.OTHER;
    }
    
    int value() {
        return this.value;
    }
}
