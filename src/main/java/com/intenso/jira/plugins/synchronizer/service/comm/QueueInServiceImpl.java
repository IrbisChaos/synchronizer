// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.Iterator;
import net.java.ao.RawEntity;
import com.atlassian.jira.issue.Issue;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Arrays;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import net.java.ao.Query;
import java.util.List;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.service.GenericServiceImpl;

public class QueueInServiceImpl extends GenericServiceImpl<QueueIn> implements QueueInService
{
    public static final String COL_CONNECTION = "CONNECTION_ID";
    public static final String COL_CONTRACT = "CONTRACT_ID";
    public static final String COL_ISSUE_ID = "ISSUE_ID";
    public static final String COL_JSON = "JSON_MSG";
    public static final String COL_STATUS = "STATUS";
    public static final String COL_MSG_TYPE = "MSG_TYPE";
    public static final String COL_MATCH_QUEUE_ID = "MATCH_QUEUE_ID";
    public static final String COL_CREATE_DATE = "CREATE_DATE";
    public static final String COL_ID = "ID";
    private static final Object lock;
    
    public QueueInServiceImpl(final ActiveObjects dao) {
        super(dao, QueueIn.class);
    }
    
    @Override
    public List<QueueIn> getAllResponses() {
        synchronized (QueueInServiceImpl.lock) {
            final QueueIn[] list = (QueueIn[])this.getDao().find((Class)QueueIn.class, Query.select().where("(MSG_TYPE = ? OR MSG_TYPE = ? OR MSG_TYPE = ? ) AND (STATUS = ? OR STATUS = ? )", new Object[] { MessageType.RESPONSE.ordinal(), MessageType.RESPONSE_COMMENT.ordinal(), MessageType.IN_RESPONSE.ordinal(), QueueStatus.NEW.ordinal(), QueueStatus.RETRY.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public QueueIn create(final Integer contractId, final Integer connectionId, final MessageType msgType, final String json, final Integer queueEntryId) {
        synchronized (QueueInServiceImpl.lock) {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("CONNECTION_ID", connectionId);
            params.put("CONTRACT_ID", contractId);
            params.put("JSON_MSG", json);
            params.put("STATUS", QueueStatus.NEW.ordinal());
            params.put("MATCH_QUEUE_ID", queueEntryId);
            params.put("CREATE_DATE", new Date());
            if (msgType != null) {
                params.put("MSG_TYPE", msgType.ordinal());
            }
            final QueueIn entry = (QueueIn)this.getDao().create((Class)QueueIn.class, (Map)params);
            return entry;
        }
    }
    
    @Override
    public List<QueueIn> getAllBy(final MessageType type) {
        synchronized (QueueInServiceImpl.lock) {
            final QueueIn[] list = (QueueIn[])this.getDao().find((Class)QueueIn.class, Query.select().where("MSG_TYPE= ? AND (STATUS = ? OR STATUS = ?)", new Object[] { type.ordinal(), QueueStatus.NEW, QueueStatus.RETRY }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueIn> getAllBy(final MessageType type, final QueueStatus status) {
        synchronized (QueueInServiceImpl.lock) {
            final QueueIn[] list = (QueueIn[])this.getDao().find((Class)QueueIn.class, Query.select().where("MSG_TYPE= ? AND STATUS = ? ", new Object[] { type.ordinal(), status.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public synchronized List<QueueIn> getAllByStatusAndMsgTypeNull(final QueueStatus status) {
        synchronized (QueueInServiceImpl.lock) {
            final QueueIn[] list = (QueueIn[])this.getDao().find((Class)QueueIn.class, Query.select().where("MSG_TYPE IS NULL AND STATUS = ? ", new Object[] { status.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueIn> findByStatus(final QueueStatus status) {
        synchronized (QueueInServiceImpl.lock) {
            final QueueIn[] list = (QueueIn[])this.getDao().find((Class)QueueIn.class, Query.select().where("STATUS = ? ", new Object[] { status.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueIn> findByIssue(final Issue issue) {
        synchronized (QueueInServiceImpl.lock) {
            final QueueIn[] list = (QueueIn[])this.getDao().find((Class)QueueIn.class, Query.select().where("ISSUE_ID = ? ", new Object[] { issue.getId() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public void updateAll(final List<QueueIn> queue, final QueueStatus status) {
        synchronized (QueueInServiceImpl.lock) {
            for (final QueueIn entity : queue) {
                entity.setStatus((status != null) ? status.ordinal() : null);
                entity.setUpdateDate(new Date());
                entity.save();
            }
            this.getDao().flush((RawEntity[])queue.toArray((RawEntity[])new QueueIn[queue.size()]));
        }
    }
    
    @Override
    public void deleteAll(final List<QueueIn> queue) {
        synchronized (QueueInServiceImpl.lock) {
            for (final QueueIn in : queue) {
                this.delete(Integer.valueOf(in.getID()));
            }
        }
    }
    
    static {
        lock = new Object();
    }
}
