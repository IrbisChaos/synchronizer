// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.sql.Timestamp;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "connection", "contractName", "fieldName", "fieldType" })
@Table("remote_field_mapping")
public interface RemoteFieldMapping extends Entity
{
    @Indexed
    Integer getConnection();
    
    @Indexed
    String getContractName();
    
    @Indexed
    String getFieldName();
    
    @Indexed
    Integer getFieldType();
    
    Timestamp getUpdateDate();
    
    void setUpdateDate(final Timestamp p0);
    
    void setConnection(final Integer p0);
    
    void setContractName(final String p0);
    
    void setFieldName(final String p0);
    
    void setFieldType(final Integer p0);
}
