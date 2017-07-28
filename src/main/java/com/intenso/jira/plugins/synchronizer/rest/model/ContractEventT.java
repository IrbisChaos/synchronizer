// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractEventT
{
    @JsonProperty
    private Integer contractId;
    @JsonProperty
    private Long eventId;
    @JsonProperty
    private Integer eventType;
    
    public Integer getContractId() {
        return this.contractId;
    }
    
    public ContractEventT(final Integer contractId, final Long eventId, final Integer eventType) {
        this.contractId = contractId;
        this.eventId = eventId;
        this.eventType = eventType;
    }
    
    public ContractEventT() {
    }
    
    public ContractEventT(final ContractEvents ce) {
        this.contractId = ce.getContractId();
        this.eventId = ce.getEventId();
        this.eventType = ce.getEventType();
    }
    
    public ContractEventT contractId(final Integer contractId) {
        this.contractId = contractId;
        return this;
    }
    
    public Long getEventId() {
        return this.eventId;
    }
    
    public ContractEventT eventId(final Long eventId) {
        this.eventId = eventId;
        return this;
    }
    
    public Integer getEventType() {
        return this.eventType;
    }
    
    public ContractEventT eventType(final Integer eventType) {
        this.eventType = eventType;
        return this;
    }
}
