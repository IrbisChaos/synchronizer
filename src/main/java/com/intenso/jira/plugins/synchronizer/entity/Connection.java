// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.util.Date;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import net.java.ao.Preload;
import net.java.ao.Entity;

@Preload({ "connectionName", "remoteJiraURL", "outOfSync" })
@Table("connection")
public interface Connection extends Entity
{
    @Indexed
    String getConnectionName();
    
    String getUsername();
    
    String getPassword();
    
    @Indexed
    String getRemoteJiraURL();
    
    String getLocalAuthKey();
    
    String getRemoteAuthKey();
    
    Integer getPassive();
    
    @Indexed
    Integer getOutOfSync();
    
    String getAlterBaseUrl();
    
    Integer getRemoteJiraType();
    
    String getProxy();
    
    Date getLastTest();
    
    void setOutOfSync(final Integer p0);
    
    void setPassive(final Integer p0);
    
    void setConnectionName(final String p0);
    
    void setUsername(final String p0);
    
    void setPassword(final String p0);
    
    void setRemoteJiraURL(final String p0);
    
    void setLocalAuthKey(final String p0);
    
    void setRemoteAuthKey(final String p0);
    
    void setAlterBaseUrl(final String p0);
    
    void setRemoteJiraType(final Integer p0);
    
    void setProxy(final String p0);
    
    void setLastTest(final Date p0);
}
