// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.providers;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ConnectionDiagnosticModel implements Serializable
{
    private static final long serialVersionUID = 6767784784471L;
    public String name;
    public int id;
    public List<ContractDiagnosticModel> contracts;
    public boolean passiveMode;
    
    public ConnectionDiagnosticModel() {
        this.contracts = new ArrayList<ContractDiagnosticModel>();
        this.passiveMode = false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getId() {
        return this.id;
    }
    
    public List<ContractDiagnosticModel> getContracts() {
        return this.contracts;
    }
    
    public boolean isPassiveMode() {
        return this.passiveMode;
    }
}
