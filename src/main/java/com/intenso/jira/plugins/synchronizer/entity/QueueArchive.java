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

@Preload({ "connectionId", "contractId", "status", "msgType" })
@Table("queue_archive")
public interface QueueArchive extends Entity
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
    
    @Indexed
    Long getIssueId();
    
    Date getCreateDate();
    
    Date getUpdateDate();
    
    Date getArchivedDate();
    
    Integer getMatchQueueId();
    
    Integer getQueueType();
    
    Integer getQueueId();
    
    void setConnectionId(final Integer p0);
    
    void setContractId(final Integer p0);
    
    void setJsonMsg(final String p0);
    
    void setStatus(final Integer p0);
    
    void setMsgType(final Integer p0);
    
    void setIssueId(final Long p0);
    
    void setMatchQueueId(final Integer p0);
    
    void setUpdateDate(final Date p0);
    
    void setCreateDate(final Date p0);
    
    void setArchivedDate(final Date p0);
    
    void setQueueType(final Integer p0);
    
    void setQueueId(final Integer p0);
}
