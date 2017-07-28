// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class OperationT
{
    @JsonProperty
    private Long issueId;
    @JsonProperty
    private Integer contractId;
    
    public Long getIssueId() {
        return this.issueId;
    }
    
    public void setIssueId(final Long issueId) {
        this.issueId = issueId;
    }
    
    public Integer getContractId() {
        return this.contractId;
    }
    
    public void setContractId(final Integer contractId) {
        this.contractId = contractId;
    }
}
