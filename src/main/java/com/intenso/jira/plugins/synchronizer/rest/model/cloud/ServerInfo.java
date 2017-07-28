// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerInfo
{
    private String baseUrl;
    private String version;
    private String deploymentType;
    private String serverTitle;
    
    public ServerInfo() {
    }
    
    public ServerInfo(final String baseUrl, final String serverTitle) {
        this.baseUrl = baseUrl;
        this.serverTitle = serverTitle;
    }
    
    public String getBaseUrl() {
        return this.baseUrl;
    }
    
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getDeploymentType() {
        return this.deploymentType;
    }
    
    public void setDeploymentType(final String deploymentType) {
        this.deploymentType = deploymentType;
    }
    
    public String getServerTitle() {
        return this.serverTitle;
    }
    
    public void setServerTitle(final String serverTitle) {
        this.serverTitle = serverTitle;
    }
}
