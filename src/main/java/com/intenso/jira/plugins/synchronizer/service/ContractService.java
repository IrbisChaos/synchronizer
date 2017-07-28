// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import com.atlassian.jira.event.issue.IssueEvent;
import com.intenso.jira.plugins.synchronizer.entity.ContractStatus;
import java.util.Set;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import com.intenso.jira.plugins.synchronizer.entity.Contract;

public interface ContractService extends SyncAwareService<Contract>
{
    ContractEvents addContractEvent(final Integer p0, final Long p1, final EventType p2);
    
    ContractEvents[] removeContractEvent(final Integer p0, final Long p1, final EventType p2);
    
    List<ContractEvents> getEventsForContract(final Integer p0, final EventType p1);
    
    List<Contract> getContractsForEvent(final Long p0, final EventType p1);
    
    List<Contract> getContracts(final Long p0, final String p1, final Long p2, final EventType p3);
    
    Set<Integer> getContractsIds(final Long p0, final String p1, final Long p2, final EventType p3);
    
    List<Contract> getContractsWithWorkflow(final String p0, final Long p1, final Long p2);
    
    Contract createContract(final Integer p0, final String p1);
    
    Contract createContract(final Integer p0, final String p1, final ContractStatus p2);
    
    List<ContractChangeItem> changes(final Contract p0, final IssueEvent p1);
    
    List<Contract> findAll();
    
    List<Contract> findByContext(final Long p0, final String p1);
    
    List<Contract> findByContextAndStatus(final Long p0, final String p1, final ContractStatus p2);
    
    List<Contract> findByContextAndStatusAndComments(final Long p0, final String p1, final ContractStatus p2, final Integer p3);
    
    List<Contract> findByConnection(final Integer p0);
    
    List<Contract> findByConnectionAndName(final Integer p0, final String p1);
    
    List<Contract> findContracts(final Project p0, final IssueType p1);
    
    List<Contract> findByContext(final Integer p0, final Long p1, final String p2);
    
    List<Contract> findByName(final String p0);
    
    List<Contract> findByContractName(final String p0);
    
    void save(final Contract p0);
    
    void updateContractEvents(final Integer p0, final List<Long> p1, final EventType p2);
    
    Integer countByContext(final Integer p0, final Long p1, final String p2);
    
    List<ContractChangeItem> changes(final Contract p0, final Issue p1);
}
