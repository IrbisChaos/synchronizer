// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "connectionId", "contractName", "enableExternalComment", "synchronizeAllComments", "synchronizeAllAttachments" })
@Table("contract")
public interface Contract extends Entity
{
    @Indexed
    String getContractName();
    
    @Indexed
    Integer getConnectionId();
    
    Long getProjectId();
    
    String getIssueType();
    
    String getRemoteContextName();
    
    @Deprecated
    Integer getFieldMapping();
    
    Integer getWorkflowMapping();
    
    Integer getContractType();
    
    Integer getStatus();
    
    Integer getComments();
    
    Integer getAttachments();
    
    Integer getSynchronizeAllAttachments();
    
    Integer getAddPrefixToAttachments();
    
    String getJqlConstraints();
    
    Integer getEnableExternalComment();
    
    Integer getSynchronizeAllComments();
    
    Integer getAllCommentRestrictions();
    
    Integer getWorklogs();
    
    void setContractName(final String p0);
    
    void setConnectionId(final Integer p0);
    
    void setProjectId(final Long p0);
    
    void setIssueType(final String p0);
    
    void setRemoteContextName(final String p0);
    
    @Deprecated
    void setFieldMapping(final Integer p0);
    
    void setWorkflowMapping(final Integer p0);
    
    void setContractType(final Integer p0);
    
    void setStatus(final Integer p0);
    
    void setComments(final Integer p0);
    
    void setAttachments(final Integer p0);
    
    void setSynchronizeAllAttachments(final Integer p0);
    
    void setAddPrefixToAttachments(final Integer p0);
    
    void setJqlConstraints(final String p0);
    
    void setEnableExternalComment(final Integer p0);
    
    void setSynchronizeAllComments(final Integer p0);
    
    void setAllCommentRestrictions(final Integer p0);
    
    void setWorklogs(final Integer p0);
}
