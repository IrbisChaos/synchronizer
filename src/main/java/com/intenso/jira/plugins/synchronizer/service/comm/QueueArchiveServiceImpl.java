// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import net.java.ao.RawEntity;
import net.java.ao.Entity;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Arrays;
import com.intenso.jira.plugins.synchronizer.entity.QueueArchive;
import java.util.List;
import com.atlassian.activeobjects.external.ActiveObjects;

public class QueueArchiveServiceImpl implements QueueArchiveService
{
    public static final String COL_CONNECTION = "CONNECTION_ID";
    public static final String COL_CONTRACT = "CONTRACT_ID";
    public static final String COL_JSON = "JSON_MSG";
    public static final String COL_STATUS = "STATUS";
    public static final String COL_MSG_TYPE = "MSG_TYPE";
    public static final String COL_ISSUE_ID = "ISSUE_ID";
    public static final String COL_MATCH_QUEUE_ID = "MATCH_QUEUE_ID";
    public static final String COL_CREATE_DATE = "CREATE_DATE";
    public static final String COL_UPDATE_DATE = "UPDATE_DATE";
    public static final String COL_ARCHIVED_DATE = "ARCHIVED_DATE";
    public static final String COL_QUEUE_TYPE = "QUEUE_TYPE";
    public static final String COL_QUEUE_ID = "QUEUE_ID";
    private final ActiveObjects ao;
    private final QueueInService queueInService;
    private final QueueOutService queueOutService;
    
    public QueueArchiveServiceImpl(final ActiveObjects ao, final QueueInService queueInService, final QueueOutService queueOutService) {
        this.ao = ao;
        this.queueInService = queueInService;
        this.queueOutService = queueOutService;
    }
    
    @Override
    public List<QueueArchive> getAll() {
        return Arrays.asList((QueueArchive[])this.ao.find((Class)QueueArchive.class));
    }
    
    @Override
    public QueueArchive create(final int id, final Integer connectionId, final Integer contractId, final Date createDate, final Long issueId, final String jsonMsg, final Integer matchQueueId, final Integer msgType, final Integer status, final Date updateDate) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONNECTION_ID", connectionId);
        params.put("CONTRACT_ID", contractId);
        params.put("JSON_MSG", jsonMsg);
        params.put("STATUS", status);
        params.put("MSG_TYPE", msgType);
        params.put("ISSUE_ID", issueId);
        params.put("MATCH_QUEUE_ID", matchQueueId);
        params.put("CREATE_DATE", createDate);
        params.put("UPDATE_DATE", updateDate);
        params.put("ARCHIVED_DATE", new Date());
        params.put("QUEUE_TYPE", 1);
        params.put("QUEUE_ID", id);
        final QueueArchive entry = (QueueArchive)this.ao.create((Class)QueueArchive.class, (Map)params);
        return entry;
    }
    
    @Override
    public QueueArchive create(final int id, final Integer connectionId, final Integer contractId, final Date createDate, final String jsonMsg, final Integer matchQueueId, final Integer msgType, final Integer status, final Date updateDate) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONNECTION_ID", connectionId);
        params.put("CONTRACT_ID", contractId);
        params.put("JSON_MSG", jsonMsg);
        params.put("STATUS", status);
        params.put("MSG_TYPE", msgType);
        params.put("MATCH_QUEUE_ID", matchQueueId);
        params.put("CREATE_DATE", createDate);
        params.put("UPDATE_DATE", updateDate);
        params.put("ARCHIVED_DATE", new Date());
        params.put("QUEUE_TYPE", 0);
        params.put("QUEUE_ID", id);
        final QueueArchive entry = (QueueArchive)this.ao.create((Class)QueueArchive.class, (Map)params);
        return entry;
    }
    
    @Override
    public void archiveAllFromQueueOut() {
        final List<QueueOut> all = this.queueOutService.findByStatus(QueueStatus.DONE);
        for (final QueueOut out : all) {
            this.create(out.getID(), out.getConnectionId(), out.getContractId(), out.getCreateDate(), out.getIssueId(), out.getJsonMsg(), out.getMatchQueueId(), out.getMsgType(), out.getStatus(), out.getUpdateDate());
            this.queueOutService.delete(Integer.valueOf(out.getID()));
        }
    }
    
    @Override
    public void archiveAllFromQueueIn() {
        final List<QueueIn> all = this.queueInService.findByStatus(QueueStatus.DONE);
        for (final QueueIn in : all) {
            this.create(in.getID(), in.getConnectionId(), in.getContractId(), in.getCreateDate(), in.getJsonMsg(), in.getMatchQueueId(), in.getMsgType(), in.getStatus(), in.getUpdateDate());
            this.queueInService.delete(Integer.valueOf(in.getID()));
        }
    }
    
    @Override
    public void purge() {
        final List<QueueArchive> all = this.getAll();
        for (final QueueArchive in : all) {
            final Entity e = (Entity)this.ao.get((Class)QueueArchive.class, (Object)in.getID());
            this.ao.delete(new RawEntity[] { e });
        }
    }
}
