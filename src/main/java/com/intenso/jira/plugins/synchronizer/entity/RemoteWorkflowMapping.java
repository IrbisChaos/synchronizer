// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.sql.Timestamp;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "connection", "workflowMappingId", "workflowMappingDisplayName", "inTransitionText", "resolution", "updateDate" })
@Table("remote_workflow")
public interface RemoteWorkflowMapping extends Entity
{
    Integer getConnection();
    
    Integer getWorkflowMappingId();
    
    String getWorkflowMappingDisplayName();
    
    String getInTransitionText();
    
    Integer getResolution();
    
    Timestamp getUpdateDate();
    
    void setConnection(final Integer p0);
    
    void setWorkflowMappingId(final Integer p0);
    
    void setWorkflowMappingDisplayName(final String p0);
    
    void setInTransitionText(final String p0);
    
    void setResolution(final Integer p0);
    
    void setUpdateDate(final Timestamp p0);
}
