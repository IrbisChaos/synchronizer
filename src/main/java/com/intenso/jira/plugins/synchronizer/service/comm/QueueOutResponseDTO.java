// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.sql.Timestamp;
import org.boon.json.annotations.JsonProperty;

public class QueueOutResponseDTO
{
    @JsonProperty("issueId")
    private Long issueId;
    @JsonProperty("contractName")
    private String contractName;
    @JsonProperty("remoteContract")
    private String remoteContractName;
    @JsonProperty("remoteIssueId")
    private Long remoteIssueId;
    @JsonProperty("remoteIssueKey")
    private String remoteIssueKey;
    @JsonProperty("commentId")
    private Integer commentId;
    @JsonProperty("remoteCommentId")
    private Integer remoteCommentId;
    @JsonProperty("remoteCommentDate")
    private Timestamp remoteCommentDate;
    private Integer status;
    private Integer matchQueueId;
    private JIRAAttachmentResponse[] attachments;
    private ResponseStateDTO responseState;
    
    public Long getIssueId() {
        return this.issueId;
    }
    
    public void setIssueId(final Long issueId) {
        this.issueId = issueId;
    }
    
    public Long getRemoteIssueId() {
        return this.remoteIssueId;
    }
    
    public void setRemoteIssueId(final Long remoteIssueId) {
        this.remoteIssueId = remoteIssueId;
    }
    
    public String getRemoteIssueKey() {
        return this.remoteIssueKey;
    }
    
    public void setRemoteIssueKey(final String remoteIssueKey) {
        this.remoteIssueKey = remoteIssueKey;
    }
    
    public String getRemoteContractName() {
        return this.remoteContractName;
    }
    
    public void setRemoteContractName(final String remoteContractName) {
        this.remoteContractName = remoteContractName;
    }
    
    public String getContractName() {
        return this.contractName;
    }
    
    public void setContractName(final String contractName) {
        this.contractName = contractName;
    }
    
    public Integer getCommentId() {
        return this.commentId;
    }
    
    public void setCommentId(final Integer commentId) {
        this.commentId = commentId;
    }
    
    public Integer getRemoteCommentId() {
        return this.remoteCommentId;
    }
    
    public void setRemoteCommentId(final Integer remoteCommentId) {
        this.remoteCommentId = remoteCommentId;
    }
    
    public Timestamp getRemoteCommentDate() {
        return this.remoteCommentDate;
    }
    
    public void setRemoteCommentDate(final Timestamp remoteCommentDate) {
        this.remoteCommentDate = remoteCommentDate;
    }
    
    public JIRAAttachmentResponse[] getAttachments() {
        return this.attachments;
    }
    
    public void setAttachments(final JIRAAttachmentResponse[] attachments) {
        this.attachments = attachments;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public Integer getMatchQueueId() {
        return this.matchQueueId;
    }
    
    public void setMatchQueueId(final Integer matchQueueId) {
        this.matchQueueId = matchQueueId;
    }
    
    public ResponseStateDTO getResponseState() {
        return this.responseState;
    }
    
    public void setResponseState(final ResponseStateDTO responseState) {
        this.responseState = responseState;
    }
}
