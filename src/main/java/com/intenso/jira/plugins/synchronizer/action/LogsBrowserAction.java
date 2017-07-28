// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.intenso.jira.plugins.synchronizer.entity.QueueLogLevel;
import java.util.ArrayList;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.cluster.ClusterManager;
import java.util.HashMap;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.QueueLog;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;

public class LogsBrowserAction extends GenericConfigAction
{
    private static final long serialVersionUID = 5300583155463808656L;
    private final QueueLogService queueLogService;
    private final QueueInService queueInService;
    private final QueueOutService queueOutService;
    private final ContractService contractService;
    private final ConnectionService connectionService;
    private List<QueueLog> qlogs;
    private Integer limit;
    private Integer offset;
    private Integer total;
    private Integer lastPage;
    private Map<Integer, Contract> contracts;
    private Map<Integer, Connection> contractsConnection;
    private String dateFrom;
    private String dateTo;
    private String contract;
    private String issue;
    private Integer httpStatus;
    private String logMessage;
    private Integer status;
    private Integer queueType;
    
    public LogsBrowserAction(final QueueLogService queueLogService, final QueueOutService queueOutService, final QueueInService queueInService, final ContractService contractService, final PluginLicenseManager pluginLicenseManager, final ConnectionService connectionService) {
        super(pluginLicenseManager);
        this.limit = 10;
        this.offset = 0;
        this.contracts = new HashMap<Integer, Contract>();
        this.contractsConnection = new HashMap<Integer, Connection>();
        this.queueLogService = queueLogService;
        this.queueInService = queueInService;
        this.queueOutService = queueOutService;
        this.contractService = contractService;
        this.connectionService = connectionService;
    }
    
    public boolean isClustered() {
        return ((ClusterManager)ComponentAccessor.getComponent((Class)ClusterManager.class)).isClustered();
    }
    
    private Map<String, Object> prepareFilters() {
        final Map<String, Object> filters = new HashMap<String, Object>();
        if (this.dateFrom != null && !this.dateFrom.isEmpty()) {
            filters.put("dateFrom", this.dateFrom);
        }
        if (this.dateTo != null && !this.dateTo.isEmpty()) {
            filters.put("dateTo", this.dateTo);
        }
        if (this.contract != null && !this.contract.isEmpty()) {
            final List<Contract> contracts = this.contractService.findByName(this.contract);
            final List<Integer> contractIds = new ArrayList<Integer>();
            for (final Contract c : contracts) {
                contractIds.add(c.getID());
            }
            if (contractIds.isEmpty()) {
                contractIds.add(-1);
            }
            filters.put("CONTRACT_ID", contractIds);
        }
        if (this.issue != null && !this.issue.isEmpty()) {
            filters.put("ISSUE_KEY", this.issue);
        }
        if (this.httpStatus != null) {
            filters.put("RESPONSE_STATUS", this.httpStatus);
        }
        if (this.logMessage != null && !this.logMessage.isEmpty()) {
            filters.put("LOG_MESSAGE", this.logMessage);
        }
        if (this.status != null) {
            QueueLogLevel ll = null;
            if (this.status < QueueLogLevel.values().length) {
                ll = QueueLogLevel.values()[this.status];
            }
            if (ll != null) {
                filters.put("LOG_LEVEL", ll);
            }
        }
        if (this.queueType != null && this.queueType < QueueType.values().length) {
            filters.put("QUEUE_TYPE", QueueType.values()[this.queueType]);
        }
        return filters;
    }
    
    @Override
    public String doDefault() throws Exception {
        final Map<String, Object> filters = this.prepareFilters();
        this.doCommon();
        this.total = this.queueLogService.countAllByFilter(filters);
        this.lastPage = (int)(Object)new Double(Math.ceil(this.total * 1.0 / this.limit)) - 1;
        this.lastPage = ((this.lastPage < 0) ? 0 : this.lastPage);
        if (this.offset > this.lastPage) {
            this.offset = this.lastPage;
        }
        this.qlogs = this.queueLogService.findAllByFilter(filters, this.offset, this.limit);
        return super.doDefault();
    }
    
    private void doCommon() {
        this.validateLicense();
        final List<Contract> cts = this.contractService.getAll();
        for (final Contract c : cts) {
            this.contracts.put(c.getID(), c);
            if (!this.contractsConnection.containsKey(c.getID())) {
                this.contractsConnection.put(c.getID(), this.connectionService.get(c.getConnectionId()));
            }
        }
    }
    
    public String getLogLevel(final int level) {
        if (level > QueueLogLevel.values().length - 1) {
            return new Integer(level).toString();
        }
        return QueueLogLevel.values()[level].toString();
    }
    
    public QueueLogService getQueueLogService() {
        return this.queueLogService;
    }
    
    public QueueInService getQueueInService() {
        return this.queueInService;
    }
    
    public QueueOutService getQueueOutService() {
        return this.queueOutService;
    }
    
    public List<QueueLog> getQlogs() {
        return this.qlogs;
    }
    
    public void setQlogs(final List<QueueLog> qlogs) {
        this.qlogs = qlogs;
    }
    
    public Integer getLimit() {
        return this.limit;
    }
    
    public void setLimit(final Integer limit) {
        this.limit = limit;
    }
    
    public Integer getOffset() {
        return this.offset;
    }
    
    public void setOffset(final Integer offset) {
        this.offset = offset;
    }
    
    public Integer getLastPage() {
        return this.lastPage;
    }
    
    public void setLastPage(final Integer lastPage) {
        this.lastPage = lastPage;
    }
    
    public Integer getTotal() {
        return this.total;
    }
    
    public void setTotal(final Integer total) {
        this.total = total;
    }
    
    public ContractService getContractService() {
        return this.contractService;
    }
    
    public Map<Integer, Contract> getContracts() {
        return this.contracts;
    }
    
    public void setContracts(final Map<Integer, Contract> contracts) {
        this.contracts = contracts;
    }
    
    public String getDateFrom() {
        return this.dateFrom;
    }
    
    public void setDateFrom(final String dateFrom) {
        this.dateFrom = dateFrom;
    }
    
    public String getDateTo() {
        return this.dateTo;
    }
    
    public void setDateTo(final String dateTo) {
        this.dateTo = dateTo;
    }
    
    public String getContract() {
        return this.contract;
    }
    
    public void setContract(final String contract) {
        this.contract = contract;
    }
    
    public String getIssue() {
        return this.issue;
    }
    
    public void setIssue(final String issue) {
        this.issue = issue;
    }
    
    public Integer getHttpStatus() {
        return this.httpStatus;
    }
    
    public void setHttpStatus(final Integer httpStatus) {
        this.httpStatus = httpStatus;
    }
    
    public String getLogMessage() {
        return this.logMessage;
    }
    
    public void setLogMessage(final String logMessage) {
        this.logMessage = logMessage;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public static long getSerialversionuid() {
        return 5300583155463808656L;
    }
    
    public Map<Integer, Connection> getContractsConnection() {
        return this.contractsConnection;
    }
    
    public void setContractsConnection(final Map<Integer, Connection> contractsConnection) {
        this.contractsConnection = contractsConnection;
    }
    
    public ConnectionService getConnectionService() {
        return this.connectionService;
    }
    
    public Integer getQueueType() {
        return this.queueType;
    }
    
    public void setQueueType(final Integer queueType) {
        this.queueType = queueType;
    }
}
