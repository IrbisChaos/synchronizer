// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

public enum ContractStatus
{
    ENABLED("Enabled"), 
    DISABLED("Disabled");
    
    private String name;
    
    private ContractStatus(final String status) {
        this.setName(status);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
