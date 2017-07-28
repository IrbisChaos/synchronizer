// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import com.intenso.jira.plugins.synchronizer.utils.HashUtils;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteContractT
{
    @JsonProperty
    private Integer hash;
    @JsonProperty
    private String contract;
    @JsonProperty
    private Integer comments;
    @JsonProperty
    private Integer attachments;
    @JsonProperty
    private Integer worklogs;
    @JsonProperty
    private Integer create;
    @JsonProperty
    private Integer update;
    @JsonProperty
    private Integer delete;
    
    public RemoteContractT() {
    }
    
    public RemoteContractT(final Contract c, final List<ContractEvents> createEvents, final List<ContractEvents> updateEvents, final List<ContractEvents> deleteEvents) {
        this.contract = c.getContractName();
        this.hash = HashUtils.getHash(c, createEvents, updateEvents);
        this.comments = ((c.getComments() != null && c.getComments().equals(1)) ? 1 : 0);
        this.attachments = ((c.getAttachments() != null && c.getAttachments().equals(1)) ? 1 : 0);
        this.worklogs = ((c.getWorklogs() != null && c.getWorklogs().equals(1)) ? 1 : 0);
        this.create = ((createEvents != null && createEvents.size() != 0) ? 1 : 0);
        this.update = ((updateEvents != null && updateEvents.size() != 0) ? 1 : 0);
        this.delete = ((deleteEvents != null && deleteEvents.size() != 0) ? 1 : 0);
    }
    
    public String getContract() {
        return this.contract;
    }
    
    public void setContract(final String contract) {
        this.contract = contract;
    }
    
    public Integer getComments() {
        return this.comments;
    }
    
    public void setComments(final Integer comments) {
        this.comments = comments;
    }
    
    public Integer getAttachments() {
        return this.attachments;
    }
    
    public void setAttachments(final Integer attachments) {
        this.attachments = attachments;
    }
    
    public Integer getCreate() {
        return this.create;
    }
    
    public void setCreate(final Integer create) {
        this.create = create;
    }
    
    public Integer getUpdate() {
        return this.update;
    }
    
    public void setUpdate(final Integer update) {
        this.update = update;
    }
    
    public Integer getDelete() {
        return this.delete;
    }
    
    public void setDelete(final Integer delete) {
        this.delete = delete;
    }
    
    public Integer getHash() {
        return this.hash;
    }
    
    public void setHash(final Integer hash) {
        this.hash = hash;
    }
    
    public Integer getWorklogs() {
        return this.worklogs;
    }
    
    public void setWorklogs(final Integer worklogs) {
        this.worklogs = worklogs;
    }
}
