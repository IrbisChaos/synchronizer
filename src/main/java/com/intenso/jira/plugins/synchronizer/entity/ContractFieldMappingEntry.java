// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "localFieldId", "localFieldName", "remoteFieldName" })
@Table("contract_fm")
public interface ContractFieldMappingEntry extends Entity
{
    Integer getContractId();
    
    String getLocalFieldId();
    
    String getLocalFieldName();
    
    String getRemoteFieldName();
    
    void setContractId(final Integer p0);
    
    void setLocalFieldId(final String p0);
    
    void setLocalFieldName(final String p0);
    
    void setRemoteFieldName(final String p0);
}
