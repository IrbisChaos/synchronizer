// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.jiraservice;

import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.service.comm.IssueIntDTO;

public class QueueInConfig
{
    private final IssueIntDTO dto;
    private final Connection connection;
    private final Contract contract;
    
    public QueueInConfig(final QueueInConfigBuilder builder) {
        this.dto = builder.getDto();
        this.connection = builder.getConnection();
        this.contract = builder.getContract();
    }
    
    public Contract getContract() {
        return this.contract;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public IssueIntDTO getDto() {
        return this.dto;
    }
}
