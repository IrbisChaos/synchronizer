// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.rest.model.FieldMappingRestrictedT;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.comm.RemoteAuthenticationKeyResult;

public interface ConfigurationJIRAClient
{
    boolean testLocalConnection(final String p0, final String p1, final String p2);
    
    RemoteResponse testRemoteConnection(final String p0, final String p1, final String p2);
    
    boolean testRemoteAuthenticationKey(final String p0, final String p1, final String p2);
    
    RemoteAuthenticationKeyResult testRemoteAuthenticationKeyCloud(final String p0, final String p1, final String p2);
    
    List<FieldMappingRestrictedT> getFieldMappingFrom(final Integer p0);
    
    List<String> getContractsFrom(final Integer p0);
}
