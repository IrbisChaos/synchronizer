// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.HashMap;
import java.util.Map;

public enum RemoteJiraType
{
    CLOUD("Cloud"), 
    SERVER("Server"), 
    UNKNOWN("Unknown");
    
    private final String name;
    private static final Map<String, RemoteJiraType> lookupForName;
    
    private RemoteJiraType(final String name) {
        this.name = name;
    }
    
    public static RemoteJiraType getByName(final String name) {
        return RemoteJiraType.lookupForName.get(name);
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isCloud() {
        return this.equals(RemoteJiraType.CLOUD);
    }
    
    public boolean isServer() {
        return this.equals(RemoteJiraType.SERVER);
    }
    
    static {
        lookupForName = new HashMap<String, RemoteJiraType>();
        for (final RemoteJiraType type : values()) {
            RemoteJiraType.lookupForName.put(type.getName(), type);
        }
    }
}
