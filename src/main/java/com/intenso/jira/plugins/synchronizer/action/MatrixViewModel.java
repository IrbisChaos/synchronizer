// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.intenso.jira.plugins.synchronizer.utils.MapEntryImpl;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class MatrixViewModel implements Serializable
{
    private static final long serialVersionUID = 2441737366463930639L;
    private Integer contractId;
    private String contractName;
    private List<Map.Entry<Long, Boolean>> allEventsList;
    
    public MatrixViewModel(final Integer contractId, final String contractName) {
        this.allEventsList = new ArrayList<Map.Entry<Long, Boolean>>();
        this.contractId = contractId;
        this.contractName = contractName;
    }
    
    public void addEvent(final Long eventId, final Boolean isSet) {
        this.allEventsList.add(new MapEntryImpl<Long, Boolean>(eventId, isSet));
    }
    
    public List<Map.Entry<Long, Boolean>> getAllEventsList() {
        return this.allEventsList;
    }
    
    public String getContractName() {
        return this.contractName;
    }
    
    public void setContractName(final String contractName) {
        this.contractName = contractName;
    }
    
    public Integer getContractId() {
        return this.contractId;
    }
}
