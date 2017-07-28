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

@Preload({ "queueType", "msgId", "logLevel", "contractId", "issueId", "issueKey", "responseStatus" })
@Table("queue_log")
public interface QueueLog extends Entity
{
    @Indexed
    Integer getQueueType();
    
    @Indexed
    Integer getMsgId();
    
    @Indexed
    Integer getLogLevel();
    
    @StringLength(-1)
    String getLogMessage();
    
    @StringLength(-1)
    String getLogData();
    
    Date getCreateDate();
    
    @Indexed
    Integer getContractId();
    
    @Indexed
    Long getIssueId();
    
    @Indexed
    String getIssueKey();
    
    @Indexed
    Integer getResponseStatus();
    
    void setResponseStatus(final Integer p0);
    
    void setIssueId(final Long p0);
    
    void setIssueKey(final String p0);
    
    void setContractId(final Integer p0);
    
    void setQueueType(final Integer p0);
    
    void setMsgId(final Integer p0);
    
    void setLogLevel(final Integer p0);
    
    void setCreateDate(final Date p0);
    
    void setLogData(final String p0);
    
    void setLogMessage(final String p0);
}
