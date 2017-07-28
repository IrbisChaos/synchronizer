// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import com.intenso.jira.plugins.synchronizer.entity.Comment;

public interface NotificationService
{
    void notifyAboutComment(final Comment p0);
    
    void notifyAboutComment(final CommentT p0);
    
    void notifyAboutAlert(final String p0);
}
