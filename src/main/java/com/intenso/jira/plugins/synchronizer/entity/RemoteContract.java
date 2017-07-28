// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.sql.Timestamp;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "connection", "contract", "hash" })
@Table("remote_contract")
public interface RemoteContract extends Entity
{
    Integer getHash();
    
    @Indexed
    Integer getConnection();
    
    @Indexed
    String getContract();
    
    @Indexed
    Timestamp getUpdateDate();
    
    Integer getCreateEnabled();
    
    Integer getUpdateEnabled();
    
    Integer getDeleteEnabled();
    
    Integer getCommentsEnabled();
    
    Integer getAttachmentsEnabled();
    
    Integer getWorklogsEnabled();
    
    void setHash(final Integer p0);
    
    void setAttachmentsEnabled(final Integer p0);
    
    void setCommentsEnabled(final Integer p0);
    
    void setUpdateEnabled(final Integer p0);
    
    void setCreateEnabled(final Integer p0);
    
    void setDeleteEnabled(final Integer p0);
    
    void setUpdateDate(final Timestamp p0);
    
    void setConnection(final Integer p0);
    
    void setContract(final String p0);
    
    void setWorklogsEnabled(final Integer p0);
}
