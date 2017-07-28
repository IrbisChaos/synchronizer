// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import java.text.SimpleDateFormat;
import com.atlassian.jira.component.ComponentAccessor;
import java.util.Date;
import java.io.Serializable;
import com.intenso.jira.plugins.synchronizer.config.IssueSyncCloudUtil;
import java.util.Comparator;
import java.util.ArrayList;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.RemoteFieldMappingService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import java.util.List;

public class ConnectionConfigAction extends GenericConfigAction
{
    private static final long serialVersionUID = -8949879539459629644L;
    private static final String CONNECTION_CONFIG = "ConnectionConfigAction.jspa";
    private List<ConnectionView> connectionViews;
    private final ConnectionService connectionService;
    private final ContractFieldMappingEntryService contractFieldMappingEntryService;
    private final ContractService contractService;
    private final RemoteFieldMappingService remoteConfigurationService;
    private Integer id;
    
    public ConnectionConfigAction(final ConnectionService connectionService, final ContractService contractService, final RemoteFieldMappingService remoteConfigurationService, final ContractFieldMappingEntryService contractFieldMappingEntryService, final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.connectionService = connectionService;
        this.contractService = contractService;
        this.remoteConfigurationService = remoteConfigurationService;
        this.contractFieldMappingEntryService = contractFieldMappingEntryService;
    }
    
    @Override
    protected String doExecute() throws Exception {
        final List<Connection> connections = this.connectionService.getAll();
        if (connections != null && connections.size() > 0) {
            this.createConnectionViews(connections.toArray(new Connection[connections.size()]));
        }
        return super.doExecute();
    }
    
    public String doSynchronize() throws Exception {
        Connection connection = null;
        if (this.id != null) {
            connection = this.connectionService.get(this.id);
        }
        if (connection != null) {
            if (connection.getPassive() == null || !connection.getPassive().equals(1)) {
                final Boolean result = this.remoteConfigurationService.sendConfiguration(connection);
                if (result != null && result.equals(Boolean.TRUE)) {
                    this.getConnectionService().makeConnectionInSync(connection.getID());
                    this.addMessage("Synchronization of configuration done");
                }
                else {
                    this.addCustomErrors("Synchronization of configuration failed!");
                }
            }
        }
        else {
            this.addCustomErrors("Synchronization of configuration failed!");
        }
        return this.doExecute();
    }
    
    @RequiresXsrfCheck
    public String doRemove() {
        Connection connection = null;
        if (this.id != null) {
            connection = this.connectionService.get(this.id);
        }
        final List<Contract> contracts = this.contractService.findByConnection(connection.getID());
        if (contracts != null) {
            for (final Contract c : contracts) {
                this.contractService.delete(c);
                this.contractFieldMappingEntryService.deleteByContract(c.getID());
            }
        }
        if (connection != null) {
            this.connectionService.delete(connection);
        }
        return this.getRedirect("ConnectionConfigAction.jspa");
    }
    
    private void createConnectionViews(final Connection[] connections) {
        this.connectionViews = new ArrayList<ConnectionView>();
        if (connections != null) {
            for (final Connection connection : connections) {
                this.connectionViews.add(new ConnectionView(connection.getID(), connection.getConnectionName(), connection.getUsername(), connection.getLocalAuthKey(), connection.getRemoteAuthKey(), connection.getRemoteJiraURL(), connection.getPassive(), connection.getOutOfSync(), connection.getLastTest()));
            }
            this.connectionViews.sort(new Comparator<ConnectionView>() {
                @Override
                public int compare(final ConnectionView o1, final ConnectionView o2) {
                    if ((o1 == null || o1.connectionName == null) && (o2 == null || o2.connectionName == null)) {
                        return 0;
                    }
                    if (o1 == null || o1.connectionName == null) {
                        return 1;
                    }
                    if (o2 == null || o2.connectionName == null) {
                        return -1;
                    }
                    return o1.connectionName.compareTo(o2.connectionName);
                }
            });
        }
    }
    
    @Override
    public boolean isConnectionConfig() {
        return true;
    }
    
    public List<ConnectionView> getConnectionViews() {
        return this.connectionViews;
    }
    
    public Boolean hasAnyConnections() {
        return this.getConnectionViews() != null && !this.getConnectionViews().isEmpty();
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public ConnectionService getConnectionService() {
        return this.connectionService;
    }
    
    public void setConnectionViews(final List<ConnectionView> connectionViews) {
        this.connectionViews = connectionViews;
    }
    
    public ContractService getContractService() {
        return this.contractService;
    }
    
    public RemoteFieldMappingService getRemoteConfigurationService() {
        return this.remoteConfigurationService;
    }
    
    public boolean getIsCloudServerProd() {
        return IssueSyncCloudUtil.isProdUrl();
    }
    
    public String getCloudServerUrl() {
        return IssueSyncCloudUtil.getCloudServerUrl();
    }
    
    public static final class ConnectionView implements Serializable
    {
        private static final long serialVersionUID = 8254534384938599380L;
        private final String connectionName;
        private final String username;
        private final String localAuthKey;
        private final String remoteAuthKey;
        private final String remoteJiraURL;
        private final Integer id;
        private final Integer passive;
        private final Integer outOfSync;
        private final String lastTest;
        
        public ConnectionView(final Integer id, final String connectionName, final String username, final String localAuthKey, final String remoteAuthKey, final String remoteJiraURL, final Integer passive, final Integer outOfSync, final Date lastTest) {
            this.connectionName = connectionName;
            this.username = username;
            this.localAuthKey = localAuthKey;
            this.remoteAuthKey = remoteAuthKey;
            this.remoteJiraURL = remoteJiraURL;
            this.id = id;
            this.passive = passive;
            this.outOfSync = outOfSync;
            this.lastTest = ((lastTest != null) ? new SimpleDateFormat("dd/MMM/yy hh:mm aaa", ComponentAccessor.getJiraAuthenticationContext().getLocale()).format(lastTest) : "");
        }
        
        public static long getSerialversionuid() {
            return 8254534384938599380L;
        }
        
        public String getConnectionName() {
            return this.connectionName;
        }
        
        public String getUsername() {
            return this.username;
        }
        
        public String getLocalAuthKey() {
            return this.localAuthKey;
        }
        
        public String getRemoteAuthKey() {
            return this.remoteAuthKey;
        }
        
        public String getRemoteJiraURL() {
            return this.remoteJiraURL;
        }
        
        public Integer getId() {
            return this.id;
        }
        
        public Integer getPassive() {
            return this.passive;
        }
        
        public Integer getOutOfSync() {
            return this.outOfSync;
        }
        
        public String getLastTest() {
            return this.lastTest;
        }
    }
}
