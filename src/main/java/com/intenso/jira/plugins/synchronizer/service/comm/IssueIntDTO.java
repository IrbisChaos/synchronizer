// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.rest.model.WorklogT;
import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import org.boon.json.annotations.JsonProperty;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import java.util.List;

public class IssueIntDTO
{
    @JsonProperty("changes")
    private List<ContractChangeItem> changes;
    @JsonProperty("issue")
    private Long issueId;
    @JsonProperty("localContractId")
    private Integer contractId;
    @JsonProperty("remoteContractName")
    private String remoteContractName;
    @JsonProperty("type")
    private Integer msgType;
    @JsonProperty("parentIssueId")
    private Long parentIssueId;
    @JsonProperty("remoteParentIssueId")
    private Long remoteParentIssueId;
    @JsonProperty("remoteIssueId")
    private Long remoteIssueId;
    @JsonProperty("syncIssueId")
    private Integer syncIssueId;
    @JsonProperty("remoteIssueKey")
    private String remoteIssueKey;
    @JsonProperty("remoteParentIssueKey")
    private String remoteParentIssueKey;
    @JsonProperty("comment")
    private CommentT comment;
    @JsonProperty("worklog")
    private WorklogT worklog;
    
    public List<ContractChangeItem> getChanges() {
        return this.changes;
    }
    
    public void setChanges(final List<ContractChangeItem> changes) {
        this.changes = changes;
    }
    
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
    
    public Integer getMsgType() {
        return this.msgType;
    }
    
    public void setMsgType(final Integer msgType) {
        this.msgType = msgType;
    }
    
    public String getRemoteContractName() {
        return this.remoteContractName;
    }
    
    public void setRemoteContractName(final String remoteContractName) {
        this.remoteContractName = remoteContractName;
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
    
    public CommentT getComment() {
        return this.comment;
    }
    
    public void setComment(final CommentT comment) {
        this.comment = comment;
    }
    
    public Integer getSyncIssueId() {
        return this.syncIssueId;
    }
    
    public void setSyncIssueId(final Integer syncIssueId) {
        this.syncIssueId = syncIssueId;
    }
    
    public WorklogT getWorklog() {
        return this.worklog;
    }
    
    public void setWorklog(final WorklogT worklog) {
        this.worklog = worklog;
    }
    
    public Long getRemoteParentIssueId() {
        return this.remoteParentIssueId;
    }
    
    public void setRemoteParentIssueId(final Long remoteParentIssueId) {
        this.remoteParentIssueId = remoteParentIssueId;
    }
    
    public String getRemoteParentIssueKey() {
        return this.remoteParentIssueKey;
    }
    
    public void setRemoteParentIssueKey(final String remoteParentIssueKey) {
        this.remoteParentIssueKey = remoteParentIssueKey;
    }
    
    public Long getParentIssueId() {
        return this.parentIssueId;
    }
    
    public void setParentIssueId(final Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }
}
