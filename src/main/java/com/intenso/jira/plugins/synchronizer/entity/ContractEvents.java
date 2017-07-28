// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "contractId", "eventId" })
@Table("contr_events")
public interface ContractEvents extends Entity
{
    @Indexed
    Integer getContractId();
    
    @Indexed
    Long getEventId();
    
    Integer getEventType();
    
    void setContractId(final Integer p0);
    
    void setEventId(final Long p0);
    
    void setEventType(final Integer p0);
}
