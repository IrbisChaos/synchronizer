// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.util.Date;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "connectionId", "contractId", "status", "msgType", "issueKey", "issueId" })
@Table("queue_in")
public interface QueueIn extends Entity
{
    @Indexed
    Integer getConnectionId();
    
    @Indexed
    Integer getContractId();
    
    @StringLength(-1)
    String getJsonMsg();
    
    @Indexed
    Integer getStatus();
    
    @Indexed
    Integer getMsgType();
    
    Date getCreateDate();
    
    Date getUpdateDate();
    
    Integer getMatchQueueId();
    
    @Indexed
    Long getIssueId();
    
    @Indexed
    String getIssueKey();
    
    void setIssueId(final Long p0);
    
    void setIssueKey(final String p0);
    
    void setConnectionId(final Integer p0);
    
    void setContractId(final Integer p0);
    
    void setJsonMsg(final String p0);
    
    void setStatus(final Integer p0);
    
    void setMsgType(final Integer p0);
    
    void setMatchQueueId(final Integer p0);
    
    void setUpdateDate(final Date p0);
    
    void setCreateDate(final Date p0);
}
