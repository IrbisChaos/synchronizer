// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.StringLength;
import java.sql.Timestamp;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "author", "commentType", "issueId", "dateInternal", "dateExternal", "syncIssueId", "comment", "buildInCommentId", "remoteCommentId" })
@Table("comments")
public interface Comment extends Entity
{
    Long getIssueId();
    
    Integer getContractId();
    
    String getAuthor();
    
    Integer getCommentType();
    
    Integer getSyncIssueId();
    
    Timestamp getDateInternal();
    
    Timestamp getDateExternal();
    
    Integer getRemoteCommentId();
    
    @StringLength(-1)
    String getComment();
    
    Long getBuildInCommentId();
    
    void setBuildInCommentId(final Long p0);
    
    void setAuthor(final String p0);
    
    void setCommentType(final Integer p0);
    
    void setSyncIssueId(final Integer p0);
    
    void setDateExternal(final Timestamp p0);
    
    void setDateInternal(final Timestamp p0);
    
    void setIssueId(final Long p0);
    
    void setRemoteCommentId(final Integer p0);
    
    void setComment(final String p0);
    
    void setContractId(final Integer p0);
}
