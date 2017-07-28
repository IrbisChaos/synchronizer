// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

public class RemoteResponse
{
    private Integer result;
    private Boolean unauthorizedStatus;
    
    public RemoteResponse() {
    }
    
    public RemoteResponse(final Integer result, final Boolean unauthorizedStatus) {
        this.result = result;
        this.unauthorizedStatus = unauthorizedStatus;
    }
    
    public Integer getResult() {
        return this.result;
    }
    
    public void setResult(final Integer result) {
        this.result = result;
    }
    
    public Boolean getUnauthorizedStatus() {
        return this.unauthorizedStatus;
    }
    
    public void setUnauthorizedStatus(final Boolean unauthorizedStatus) {
        this.unauthorizedStatus = unauthorizedStatus;
    }
}
