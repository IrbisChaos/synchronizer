// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.historytab;

import com.intenso.jira.plugins.synchronizer.service.comm.ResponseStateDTO;

public class HistoryTabRecord
{
    private String createDate;
    private String queueType;
    private String arrowDirection;
    private String contractName;
    private String createDateFormatted;
    private String messageType;
    private String queueStatus;
    private String queueStatusLozenge;
    private String queueId;
    private String contractId;
    private ResponseStateDTO responseState;
    
    public String getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final String createDate) {
        this.createDate = createDate;
    }
    
    public String getQueueType() {
        return this.queueType;
    }
    
    public void setQueueType(final String queueType) {
        this.queueType = queueType;
    }
    
    public String getArrowDirection() {
        return this.arrowDirection;
    }
    
    public void setArrowDirection(final String arrowDirection) {
        this.arrowDirection = arrowDirection;
    }
    
    public String getContractName() {
        return this.contractName;
    }
    
    public void setContractName(final String contractName) {
        this.contractName = contractName;
    }
    
    public String getCreateDateFormatted() {
        return this.createDateFormatted;
    }
    
    public void setCreateDateFormatted(final String createDateFormatted) {
        this.createDateFormatted = createDateFormatted;
    }
    
    public String getMessageType() {
        return this.messageType;
    }
    
    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }
    
    public String getQueueStatus() {
        return this.queueStatus;
    }
    
    public void setQueueStatus(final String queueStatus) {
        this.queueStatus = queueStatus;
    }
    
    public String getQueueStatusLozenge() {
        return this.queueStatusLozenge;
    }
    
    public void setQueueStatusLozenge(final String queueStatusLozenge) {
        this.queueStatusLozenge = queueStatusLozenge;
    }
    
    public String getQueueId() {
        return this.queueId;
    }
    
    public void setQueueId(final String queueId) {
        this.queueId = queueId;
    }
    
    public String getContractId() {
        return this.contractId;
    }
    
    public void setContractId(final String contractId) {
        this.contractId = contractId;
    }
    
    public ResponseStateDTO getResponseState() {
        return this.responseState;
    }
    
    public void setResponseState(final ResponseStateDTO responseState) {
        this.responseState = responseState;
    }
}
