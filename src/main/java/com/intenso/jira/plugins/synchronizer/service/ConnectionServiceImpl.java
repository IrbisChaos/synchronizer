// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Arrays;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import net.java.ao.Query;
import java.util.Map;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.Connection;

public class ConnectionServiceImpl extends SyncAwareServiceImpl<Connection> implements ConnectionService
{
    public static final String COL_NAME = "CONNECTION_NAME";
    public static final String COL_USERNAME = "USERNAME";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_REMOTE_AUTH_KEY = "REMOTE_AUTH_KEY";
    public static final String COL_LOCAL_AUTH_KEY = "LOCAL_AUTH_KEY";
    public static final String COL_REMOTE_JIRA_URL = "REMOTE_JIRA_URL";
    public static final String COL_PASSIVE = "PASSIVE";
    public static final String COL_ALTERNATIVE_BASEURL = "ALTER_BASE_URL";
    public static final String COL_REMOTE_JIRA_TYPE = "REMOTE_JIRA_TYPE";
    public static final String COL_PROXY = "PROXY";
    
    public ConnectionServiceImpl(final ActiveObjects dao) {
        super(dao, Connection.class);
    }
    
    @Override
    public Connection saveConnection(final String connectionName, final String username, final String localAuthKey, final String remoteAuthKey, final String remoteJiraUrl, final Integer passive, final String password, final String alternativeBaseUrl, final Integer remoteJiraType, final String proxy) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONNECTION_NAME", connectionName);
        params.put("USERNAME", username);
        params.put("REMOTE_AUTH_KEY", remoteAuthKey);
        params.put("LOCAL_AUTH_KEY", localAuthKey);
        params.put("REMOTE_JIRA_URL", remoteJiraUrl);
        params.put("PASSIVE", passive);
        params.put("PASSWORD", password);
        params.put("ALTER_BASE_URL", alternativeBaseUrl);
        params.put("REMOTE_JIRA_TYPE", remoteJiraType);
        params.put("PROXY", proxy);
        final Connection connection = (Connection)this.getDao().create((Class)Connection.class, (Map)params);
        return connection;
    }
    
    @Override
    public Connection get(final String connectionName) {
        final Connection[] list = (Connection[])this.getDao().find((Class)Connection.class, Query.select().where("CONNECTION_NAME = ?", new Object[] { connectionName }));
        if (list != null && list.length > 0) {
            return list[0];
        }
        return null;
    }
    
    @Override
    public Connection findByUrl(final String jiraUrl) {
        final Connection[] list = (Connection[])this.getDao().find((Class)Connection.class, Query.select().where("REMOTE_JIRA_URL = ? ", new Object[] { jiraUrl }));
        return (list != null && list.length > 0) ? list[0] : null;
    }
    
    @Override
    public Connection findByAppKey(final String appKey) {
        final Connection[] list = (Connection[])this.getDao().find((Class)Connection.class, Query.select().where("LOCAL_AUTH_KEY = ? ", new Object[] { appKey }));
        return (list != null && list.length > 0) ? list[0] : null;
    }
    
    @Override
    public Connection getConnectionForContract(final Integer contractId) {
        final Contract c = (Contract)this.getDao().get((Class)Contract.class, (Object)contractId);
        if (c == null) {
            return null;
        }
        final Integer connectionId = c.getConnectionId();
        final Connection connection = (Connection)this.getDao().get((Class)Connection.class, (Object)connectionId);
        return connection;
    }
    
    @Override
    public Integer countByAppKey(final String appKey) {
        return this.getDao().count((Class)Connection.class, Query.select().where("LOCAL_AUTH_KEY = ?", new Object[] { appKey }));
    }
    
    @Override
    public List<Connection> findAllByAppKey(final String appKey) {
        final Connection[] connections = (Connection[])this.getDao().find((Class)Connection.class, Query.select().where("LOCAL_AUTH_KEY = ?", new Object[] { appKey }));
        return Arrays.asList(connections);
    }
    
    @Override
    public List<Connection> getActiveModeConnectionsList() {
        final Connection[] activeConnections = (Connection[])this.getDao().find((Class)Connection.class, Query.select().where("PASSIVE IS NULL OR PASSIVE != ?", new Object[] { 1 }));
        return Arrays.asList(activeConnections);
    }
    
    @Override
    public List<Connection> getPassiveModeConnectionList() {
        final Connection[] pasiveConnections = (Connection[])this.getDao().find((Class)Connection.class, Query.select().where("PASSIVE = ? ", new Object[] { 1 }));
        return Arrays.asList(pasiveConnections);
    }
    
    @Override
    public int countAll() {
        final Integer count = this.getDao().count((Class)Connection.class, Query.select());
        return count;
    }
}
