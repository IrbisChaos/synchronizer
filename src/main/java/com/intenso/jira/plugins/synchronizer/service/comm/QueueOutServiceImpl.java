// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import net.java.ao.RawEntity;
import com.atlassian.jira.issue.Issue;
import java.util.Iterator;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.service.GenericServiceImpl;

public class QueueOutServiceImpl extends GenericServiceImpl<QueueOut> implements QueueOutService
{
    public static final String COL_CONNECTION = "CONNECTION_ID";
    public static final String COL_CONTRACT = "CONTRACT_ID";
    public static final String COL_JSON = "JSON_MSG";
    public static final String COL_STATUS = "STATUS";
    public static final String COL_MSG_TYPE = "MSG_TYPE";
    public static final String COL_ISSUE_ID = "ISSUE_ID";
    public static final String COL_MATCH_QUEUE_ID = "MATCH_QUEUE_ID";
    public static final String COL_CREATE_DATE = "CREATE_DATE";
    public static final String COL_EVENT_DATE = "EVENT_DATE";
    public static final String COL_ID = "ID";
    private static final Object lock;
    
    public QueueOutServiceImpl(final ActiveObjects dao) {
        super(dao, QueueOut.class);
    }
    
    @Override
    public QueueOut create(final Contract contract, final MessageType msgType, final QueueStatus status, final String jsonMsg, final Long issueId, final Integer matchQueueEntryId) {
        synchronized (QueueOutServiceImpl.lock) {
            return this.create(contract.getConnectionId(), Integer.valueOf(contract.getID()), msgType, status, jsonMsg, issueId, matchQueueEntryId);
        }
    }
    
    @Override
    public QueueOut create(final Contract contract, final MessageType msgType, final QueueStatus status, final String jsonMsg, final Long issueId, final Integer matchQueueEntryId, final Date eventDate) {
        synchronized (QueueOutServiceImpl.lock) {
            return this.create(contract.getConnectionId(), contract.getID(), msgType, status, jsonMsg, issueId, matchQueueEntryId, eventDate);
        }
    }
    
    @Override
    public QueueOut create(final Integer connectionId, final Integer contractId, final MessageType msgType, final String jsonMsg, final Long issueId, final Integer matchQueueEntryId) {
        synchronized (QueueOutServiceImpl.lock) {
            return this.create(connectionId, contractId, msgType, QueueStatus.NEW, jsonMsg, issueId, matchQueueEntryId);
        }
    }
    
    @Override
    public QueueOut create(final Integer connectionId, final Integer contractId, final MessageType msgType, final QueueStatus status, final String jsonMsg, final Long issueId, final Integer matchQueueEntryId) {
        synchronized (QueueOutServiceImpl.lock) {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("CONNECTION_ID", connectionId);
            params.put("CONTRACT_ID", contractId);
            params.put("JSON_MSG", jsonMsg);
            params.put("STATUS", status.ordinal());
            params.put("MSG_TYPE", msgType.ordinal());
            params.put("ISSUE_ID", issueId);
            params.put("MATCH_QUEUE_ID", matchQueueEntryId);
            params.put("CREATE_DATE", new Date());
            final QueueOut entry = (QueueOut)this.getDao().create((Class)QueueOut.class, (Map)params);
            return entry;
        }
    }
    
    @Override
    public QueueOut create(final Integer connectionId, final Integer contractId, final MessageType msgType, final QueueStatus status, final String jsonMsg, final Long issueId, final Integer matchQueueEntryId, final Date eventDate) {
        synchronized (QueueOutServiceImpl.lock) {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("CONNECTION_ID", connectionId);
            params.put("CONTRACT_ID", contractId);
            params.put("JSON_MSG", jsonMsg);
            params.put("STATUS", status.ordinal());
            params.put("MSG_TYPE", msgType.ordinal());
            params.put("ISSUE_ID", issueId);
            params.put("MATCH_QUEUE_ID", matchQueueEntryId);
            params.put("CREATE_DATE", new Date());
            params.put("EVENT_DATE", eventDate);
            final QueueOut entry = (QueueOut)this.getDao().create((Class)QueueOut.class, (Map)params);
            return entry;
        }
    }
    
    @Override
    public List<QueueOut> findByStatus(final QueueStatus status) {
        synchronized (QueueOutServiceImpl.lock) {
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("STATUS = ? ", new Object[] { status.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueOut> findBy(final QueueStatus status, final MessageType msgType) {
        synchronized (QueueOutServiceImpl.lock) {
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("STATUS = ? AND MSG_TYPE = ?", new Object[] { status.ordinal(), msgType.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueOut> findBy(final QueueStatus status, final MessageType msgType, final List<Connection> connections) {
        synchronized (QueueOutServiceImpl.lock) {
            final List<String> connectionList = new ArrayList<String>();
            if (connections != null) {
                for (final Connection c : connections) {
                    connectionList.add(new Integer(c.getID()).toString());
                }
            }
            final String inConnection = Joiner.on(", ").join((Iterable)connectionList);
            String query = "";
            if (!connectionList.isEmpty()) {
                query = query + "CONNECTION_ID IN (" + inConnection + ")";
            }
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("STATUS = ? AND MSG_TYPE = ? " + (query.isEmpty() ? "" : (" AND " + query)), new Object[] { status.ordinal(), msgType.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueOut> findBy(final QueueStatus status, final MessageType msgType, final Long issueId, final Integer contractId) {
        synchronized (QueueOutServiceImpl.lock) {
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("STATUS = ? AND MSG_TYPE = ? AND ISSUE_ID = ? AND CONTRACT_ID = ?", new Object[] { status.ordinal(), msgType.ordinal(), issueId, contractId }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueOut> findBy(final QueueStatus status, final List<MessageType> msgTypes) {
        synchronized (QueueOutServiceImpl.lock) {
            String typesList = "";
            String queryMsgTypes = "";
            if (msgTypes.size() > 0) {
                for (final MessageType type : msgTypes) {
                    typesList = typesList + type.ordinal() + ",";
                }
                typesList = typesList.substring(0, typesList.length() - 1);
                queryMsgTypes = " AND MSG_TYPE IN (" + typesList + ")";
            }
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("STATUS = ? " + queryMsgTypes, new Object[] { status.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueOut> findBy(final QueueStatus status, final List<MessageType> msgTypes, final List<Connection> connections) {
        synchronized (QueueOutServiceImpl.lock) {
            final List<String> connectionList = new ArrayList<String>();
            if (connections != null) {
                for (final Connection c : connections) {
                    connectionList.add(new Integer(c.getID()).toString());
                }
            }
            final String inConnection = Joiner.on(", ").join((Iterable)connectionList);
            String typesList = "";
            String queryMsgTypes = "";
            if (msgTypes.size() > 0) {
                for (final MessageType type : msgTypes) {
                    typesList = typesList + type.ordinal() + ",";
                }
                typesList = typesList.substring(0, typesList.length() - 1);
                queryMsgTypes = " AND MSG_TYPE IN (" + typesList + ")";
            }
            if (!connectionList.isEmpty()) {
                queryMsgTypes = queryMsgTypes + (queryMsgTypes.isEmpty() ? " " : " AND ") + "CONNECTION_ID" + " IN (" + inConnection + ")";
            }
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("STATUS = ? " + queryMsgTypes, new Object[] { status.ordinal() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueOut> findByOrderByEventDate(final QueueStatus status, final List<MessageType> msgTypes, final List<Connection> connections) {
        synchronized (QueueOutServiceImpl.lock) {
            final List<String> connectionList = new ArrayList<String>();
            if (connections != null) {
                for (final Connection c : connections) {
                    connectionList.add(new Integer(c.getID()).toString());
                }
            }
            final String inConnection = Joiner.on(", ").join((Iterable)connectionList);
            String typesList = "";
            String queryMsgTypes = "";
            if (msgTypes.size() > 0) {
                for (final MessageType type : msgTypes) {
                    typesList = typesList + type.ordinal() + ",";
                }
                typesList = typesList.substring(0, typesList.length() - 1);
                queryMsgTypes = " AND MSG_TYPE IN (" + typesList + ")";
            }
            if (!connectionList.isEmpty()) {
                queryMsgTypes = queryMsgTypes + (queryMsgTypes.isEmpty() ? " " : " AND ") + "CONNECTION_ID" + " IN (" + inConnection + ")";
            }
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("STATUS = ? " + queryMsgTypes, new Object[] { status.ordinal() }).order("EVENT_DATE"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public List<QueueOut> findByIssue(final Issue issue) {
        synchronized (QueueOutServiceImpl.lock) {
            final QueueOut[] list = (QueueOut[])this.getDao().find((Class)QueueOut.class, Query.select().where("ISSUE_ID = ? ", new Object[] { issue.getId() }).order("ID"));
            return Arrays.asList(list);
        }
    }
    
    @Override
    public void updateAll(final List<QueueOut> queue, final QueueStatus status) {
        synchronized (QueueOutServiceImpl.lock) {
            for (final QueueOut entity : queue) {
                entity.setStatus((status != null) ? status.ordinal() : null);
                entity.setUpdateDate(new Date());
                entity.save();
            }
            this.getDao().flush((RawEntity[])queue.toArray((RawEntity[])new QueueOut[queue.size()]));
        }
    }
    
    @Override
    public QueueOut update(final Integer queueOutId, final QueueStatus status) {
        synchronized (QueueOutServiceImpl.lock) {
            final QueueOut qo = (QueueOut)this.getDao().get((Class)QueueOut.class, (Object)queueOutId);
            qo.setStatus(status.ordinal());
            qo.setUpdateDate(new Date());
            qo.save();
            return qo;
        }
    }
    
    @Override
    public void deleteAll(final List<QueueOut> queue) {
        synchronized (QueueOutServiceImpl.lock) {
            for (final QueueOut in : queue) {
                this.delete(Integer.valueOf(in.getID()));
            }
        }
    }
    
    static {
        lock = new Object();
    }
}
