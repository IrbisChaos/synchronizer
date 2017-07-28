// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import java.util.List;
import org.boon.json.JsonParserAndMapper;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.nio.charset.StandardCharsets;
import org.boon.json.JsonParserFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.service.comm.IssueIntDTO;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.intenso.jira.plugins.synchronizer.utils.Builder;

public class QueueInConfigBuilder implements Builder<QueueInConfig>
{
    private ExtendedLogger log;
    private IssueIntDTO dto;
    private Connection connection;
    private Contract contract;
    private QueueIn entry;
    
    public QueueInConfigBuilder(final QueueIn entry) {
        this.log = ExtendedLoggerFactory.getLogger(this.getClass());
        this.entry = entry;
    }
    
    @Override
    public QueueInConfig build() {
        if (this.entry.getJsonMsg() == null) {
            return null;
        }
        final JsonParserFactory jsonParserFactory = new JsonParserFactory().setCharset(StandardCharsets.UTF_8);
        final JsonParserAndMapper mapper = jsonParserFactory.create();
        this.dto = mapper.parse(IssueIntDTO.class, this.entry.getJsonMsg());
        if (this.dto.getRemoteIssueId() == null) {
            this.log.warn(ExtendedLoggerMessageType.JOB, "IncomingTask.process dto.getRemoteIssueId() is null");
            return null;
        }
        this.connection = this.getConnectionService().find(this.entry.getConnectionId());
        final List<Contract> contracts = this.getContractService().findByConnectionAndName(this.entry.getConnectionId(), this.dto.getRemoteContractName());
        this.contract = null;
        if (contracts.size() > 0) {
            this.contract = contracts.get(0);
        }
        if (this.contract == null || this.connection == null) {
            this.log.error(ExtendedLoggerMessageType.JOB, "Contract or connection is null. Probably contract from json message not exists.");
            return null;
        }
        return new QueueInConfig(this);
    }
    
    private ContractService getContractService() {
        return (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
    }
    
    private ConnectionService getConnectionService() {
        return (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
    }
    
    public IssueIntDTO getDto() {
        return this.dto;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public Contract getContract() {
        return this.contract;
    }
}
