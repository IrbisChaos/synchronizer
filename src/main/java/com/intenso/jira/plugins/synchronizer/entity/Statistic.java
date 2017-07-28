// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.sql.Timestamp;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "date", "connectionId", "contractId", "messageType", "inOut", "processingTime", "msgSize", "attachmentSize" })
@Table("statistic")
public interface Statistic extends Entity
{
    Timestamp getDate();
    
    Integer getConnectionId();
    
    Integer getContractId();
    
    Integer getMessageType();
    
    Integer getInOut();
    
    Long getProcessingTime();
    
    Integer getMsgSize();
    
    Long getAttachmentSize();
    
    void setDate(final Timestamp p0);
    
    void setConnectionId(final Integer p0);
    
    void setContractId(final Integer p0);
    
    void setMessageType(final Integer p0);
    
    void setInOut(final Integer p0);
    
    void setProcessingTime(final Long p0);
    
    void setMsgSize(final Integer p0);
    
    void setAttachmentSize(final Long p0);
}
