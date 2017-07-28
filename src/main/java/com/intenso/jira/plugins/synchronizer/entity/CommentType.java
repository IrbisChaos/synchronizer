// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

public enum CommentType
{
    INTERNAL, 
    EXTERNAL;
    
    public static CommentType getCommentTypeByOrdinal(final Integer ordinal) {
        if (values().length > ordinal) {
            return values()[ordinal];
        }
        return null;
    }
}
