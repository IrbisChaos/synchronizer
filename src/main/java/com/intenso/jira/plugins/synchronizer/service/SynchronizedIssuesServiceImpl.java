// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Map;
import java.util.HashMap;
import com.atlassian.jira.issue.MutableIssue;
import com.google.gson.Gson;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutResponseDTO;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;

public class SynchronizedIssuesServiceImpl extends GenericServiceImpl<SyncIssue> implements SynchronizedIssuesService
{
    public static final String COL_CONTRACT = "CONTRACT_ID";
    public static final String COL_ISSUE = "ISSUE_ID";
    public static final String COL_REMOTE_ISSUE = "REMOTE_ISSUE_ID";
    public static final String COL_REMOTE_ISSUE_KEY = "REMOTE_ISSUE_KEY";
    private IssueManager issueManager;
    private CustomFieldManager customFieldManager;
    
    public SynchronizedIssuesServiceImpl(final ActiveObjects dao, final IssueManager issueManager, final CustomFieldManager customFieldManager) {
        super(dao, SyncIssue.class);
        this.setIssueManager(issueManager);
        this.setCustomFieldManager(customFieldManager);
    }
    
    @Override
    public List<SyncIssue> findByContract(final Integer contractId) {
        final SyncIssue[] list = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("CONTRACT_ID = ? ", new Object[] { contractId }));
        return Arrays.asList(list);
    }
    
    @Override
    public List<SyncIssue> findByContract(final Integer contractId, final Long issueId) {
        final SyncIssue[] list = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("CONTRACT_ID = ? AND ISSUE_ID = ?", new Object[] { contractId, issueId }));
        return Arrays.asList(list);
    }
    
    @Override
    public SyncIssue findByContractSingleResult(final Integer contractId, final Long issueId) {
        final SyncIssue[] list = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("CONTRACT_ID = ? AND ISSUE_ID = ?", new Object[] { contractId, issueId }));
        return (list.length > 0) ? list[0] : null;
    }
    
    @Override
    public SyncIssue findByContractAndRemoteIssueIdSingleResult(final Integer contractId, final Long remoteIssueId) {
        final SyncIssue[] list = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("CONTRACT_ID = ? AND REMOTE_ISSUE_ID = ?", new Object[] { contractId, remoteIssueId }));
        return (list.length > 0) ? list[0] : null;
    }
    
    @Override
    public SyncIssue update(final QueueOutResponseDTO response) {
        MutableIssue mutableIssue = null;
        if (response.getIssueId() != null) {
            mutableIssue = this.issueManager.getIssueObject(response.getIssueId());
        }
        if (mutableIssue == null) {
            this.getLogger().error(ExtendedLoggerMessageType.JOB, "Issue is null according to matched queue id");
            return null;
        }
        final String contractName = response.getContractName();
        final Contract[] ctList = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("CONTRACT_NAME = ?", new Object[] { contractName }));
        if (ctList == null) {
            final Gson gson = new Gson();
            this.getLogger().error(ExtendedLoggerMessageType.JOB, "Contract name not set  " + gson.toJson(response));
            return null;
        }
        Contract properContract = null;
        for (final Contract c : ctList) {
            if (mutableIssue.getProjectObject().getId().equals(c.getProjectId()) && mutableIssue.getIssueTypeObject().getId().equals(c.getIssueType())) {
                properContract = c;
                break;
            }
        }
        if (properContract == null) {
            return null;
        }
        return this.update(properContract.getID(), mutableIssue.getId(), response.getRemoteIssueId(), response.getRemoteIssueKey());
    }
    
    @Override
    public SyncIssue save(final Integer contractId, final Long issueId, final Long remoteIssueId, final String remoteIssueKey) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRACT_ID", contractId);
        params.put("ISSUE_ID", issueId);
        params.put("REMOTE_ISSUE_ID", remoteIssueId);
        params.put("REMOTE_ISSUE_KEY", remoteIssueKey);
        return (SyncIssue)this.getDao().create((Class)SyncIssue.class, (Map)params);
    }
    
    @Override
    public SyncIssue update(final Integer contractId, final Long issueId, final Long remoteIssueId, final String remoteIssueKey) {
        final List<SyncIssue> entities = this.findByContract(contractId, issueId);
        if (entities != null && entities.size() > 0) {
            final SyncIssue syncIssue = entities.get(0);
            syncIssue.setRemoteIssueId(remoteIssueId);
            syncIssue.setRemoteIssueKey(remoteIssueKey);
            syncIssue.save();
            return syncIssue;
        }
        return this.save(contractId, issueId, remoteIssueId, remoteIssueKey);
    }
    
    @Override
    public void delete(final Integer contractId, final Long issueId) {
        final SyncIssue syncIssue = this.findByContractSingleResult(contractId, issueId);
        if (syncIssue != null) {
            this.delete(syncIssue);
        }
    }
    
    public IssueManager getIssueManager() {
        return this.issueManager;
    }
    
    public void setIssueManager(final IssueManager issueManager) {
        this.issueManager = issueManager;
    }
    
    public CustomFieldManager getCustomFieldManager() {
        return this.customFieldManager;
    }
    
    public void setCustomFieldManager(final CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
    
    @Override
    public List<SyncIssue> findByIssue(final Long issueId) {
        final SyncIssue[] syncs = (SyncIssue[])this.getDao().find((Class)SyncIssue.class, Query.select().where("ISSUE_ID = ? ", new Object[] { issueId }));
        return Arrays.asList(syncs);
    }
}
