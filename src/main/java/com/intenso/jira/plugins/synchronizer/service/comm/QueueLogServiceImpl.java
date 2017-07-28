// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import net.java.ao.RawEntity;
import org.joda.time.DateTime;
import java.util.Iterator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.database.DatabaseConfigurationManager;
import java.util.ArrayList;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import net.java.ao.Entity;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.atlassian.jira.issue.Issue;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.entity.QueueLogLevel;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.IssueManager;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.intenso.jira.plugins.synchronizer.entity.QueueLog;
import com.intenso.jira.plugins.synchronizer.service.GenericServiceImpl;

public class QueueLogServiceImpl extends GenericServiceImpl<QueueLog> implements QueueLogService
{
    public static final String COL_TYPE = "QUEUE_TYPE";
    public static final String COL_MSG = "MSG_ID";
    public static final String COL_LOG_LEVEL = "LOG_LEVEL";
    public static final String COL_STATUS = "RESPONSE_STATUS";
    public static final String COL_CREATE_DATE = "CREATE_DATE";
    public static final String COL_ISSUE_ID = "ISSUE_ID";
    public static final String COL_ISSUE_KEY = "ISSUE_KEY";
    public static final String COL_CONTRACT_ID = "CONTRACT_ID";
    public static final String COL_LOG_MESSAGE = "LOG_MESSAGE";
    public static final String COL_LOG_DATA = "LOG_DATA";
    private ExtendedLogger logger;
    private final IssueManager issueManager;
    
    public QueueLogServiceImpl(final ActiveObjects dao, final IssueManager issueManager) {
        super(dao, QueueLog.class);
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.issueManager = issueManager;
    }
    
    @Override
    public QueueLog createQueueLog(final QueueType type, final QueueLogLevel level, final String logMessage, final String logData, final Integer msgId, final Integer contractId, final Long issueId, final Integer status) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("QUEUE_TYPE", type.ordinal());
        params.put("MSG_ID", msgId);
        params.put("LOG_LEVEL", level.ordinal());
        params.put("RESPONSE_STATUS", status);
        params.put("LOG_MESSAGE", logMessage);
        params.put("LOG_DATA", logData);
        params.put("CREATE_DATE", new Date());
        params.put("CONTRACT_ID", contractId);
        params.put("ISSUE_ID", issueId);
        if (issueId != null) {
            final Issue issue = (Issue)this.issueManager.getIssueObject(issueId);
            params.put("ISSUE_KEY", (issue != null) ? issue.getKey() : null);
        }
        final QueueLog entry = (QueueLog)this.getDao().create((Class)QueueLog.class, (Map)params);
        return entry;
    }
    
    @Override
    public QueueLog createOutLog(final QueueOut queueOut) {
        try {
            final Integer status = 200;
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("QUEUE_TYPE", QueueType.OUT.ordinal());
            params.put("MSG_ID", queueOut.getID());
            params.put("LOG_LEVEL", QueueLogLevel.getQueueLogByHttpResponseStatus(status).ordinal());
            params.put("RESPONSE_STATUS", status);
            final MessageType msgType = MessageType.values()[queueOut.getMsgType()];
            if (msgType.equals(MessageType.RESPONSE) || msgType.equals(MessageType.RESPONSE_COMMENT)) {
                params.put("LOG_MESSAGE", QueueDefaultLogText.SEND_RESPONSE.getText() + " (" + MessageType.getByOrdinal(queueOut.getMsgType()) + ")");
                params.put("LOG_DATA", queueOut.getJsonMsg());
            }
            else {
                params.put("LOG_MESSAGE", QueueDefaultLogText.NEW_ADDED.getText() + " (" + MessageType.getByOrdinal(queueOut.getMsgType()) + ")");
                params.put("LOG_DATA", queueOut.getJsonMsg());
            }
            params.put("CREATE_DATE", new Date());
            params.put("CONTRACT_ID", queueOut.getContractId());
            params.put("ISSUE_ID", queueOut.getIssueId());
            if (queueOut.getIssueId() != null) {
                final Issue issue = (Issue)this.issueManager.getIssueObject(queueOut.getIssueId());
                params.put("ISSUE_KEY", (issue != null) ? issue.getKey() : null);
            }
            final QueueLog entry = (QueueLog)this.getDao().create((Class)QueueLog.class, (Map)params);
            return entry;
        }
        catch (Exception e) {
            this.logger.warn(ExtendedLoggerMessageType.OTHER, "QueueLog not saved: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public QueueLog createQueueLog(final Response response, final Entity queueEntity, final Integer contractId, final Long issueId) {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("QUEUE_TYPE", (queueEntity instanceof QueueOut) ? QueueType.OUT.ordinal() : QueueType.IN.ordinal());
            params.put("MSG_ID", queueEntity.getID());
            params.put("LOG_LEVEL", QueueLogLevel.getQueueLogByHttpResponseStatus(response.getStatus()).ordinal());
            params.put("RESPONSE_STATUS", response.getStatus());
            final String description = response.getJson();
            params.put("LOG_MESSAGE", (description == null) ? null : description.replaceAll("[^\\x00-\\x7F]", ""));
            params.put("CREATE_DATE", new Date());
            params.put("CONTRACT_ID", contractId);
            params.put("ISSUE_ID", issueId);
            if (issueId != null) {
                final Issue issue = (Issue)this.issueManager.getIssueObject(issueId);
                params.put("ISSUE_KEY", (issue != null) ? issue.getKey() : null);
            }
            final QueueLog entry = (QueueLog)this.getDao().create((Class)QueueLog.class, (Map)params);
            return entry;
        }
        catch (Exception e) {
            this.logger.warn(ExtendedLoggerMessageType.OTHER, "QueueLog not saved: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public QueueLog createQueueLog(final Integer status, final String errorMessage, final QueueIn queueIn) {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("QUEUE_TYPE", QueueType.IN.ordinal());
            params.put("MSG_ID", queueIn.getID());
            params.put("LOG_LEVEL", QueueLogLevel.getQueueLogByHttpResponseStatus(status).ordinal());
            params.put("RESPONSE_STATUS", status);
            params.put("LOG_MESSAGE", errorMessage);
            params.put("CREATE_DATE", new Date());
            params.put("CONTRACT_ID", queueIn.getContractId());
            params.put("ISSUE_ID", queueIn.getIssueId());
            if (queueIn.getIssueId() != null) {
                final Issue issue = (Issue)this.issueManager.getIssueObject(queueIn.getIssueId());
                params.put("ISSUE_KEY", (issue != null) ? issue.getKey() : null);
            }
            final QueueLog entry = (QueueLog)this.getDao().create((Class)QueueLog.class, (Map)params);
            return entry;
        }
        catch (Exception e) {
            this.logger.warn(ExtendedLoggerMessageType.OTHER, "QueueLog not saved: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public QueueLog createQueueLog(final Integer status, final String message, final String data, final QueueIn queueIn) {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("QUEUE_TYPE", QueueType.IN.ordinal());
            params.put("MSG_ID", queueIn.getID());
            params.put("LOG_LEVEL", QueueLogLevel.getQueueLogByHttpResponseStatus(status).ordinal());
            params.put("RESPONSE_STATUS", status);
            params.put("LOG_MESSAGE", message);
            params.put("LOG_DATA", data);
            params.put("CREATE_DATE", new Date());
            params.put("CONTRACT_ID", queueIn.getContractId());
            params.put("ISSUE_ID", queueIn.getIssueId());
            if (queueIn.getIssueId() != null) {
                final Issue issue = (Issue)this.issueManager.getIssueObject(queueIn.getIssueId());
                params.put("ISSUE_KEY", (issue != null) ? issue.getKey() : null);
            }
            final QueueLog entry = (QueueLog)this.getDao().create((Class)QueueLog.class, (Map)params);
            return entry;
        }
        catch (Exception e) {
            this.logger.warn(ExtendedLoggerMessageType.OTHER, "QueueLog not saved: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public QueueLog createQueueLog(final Integer status, final String errorMessage, final QueueOut queueOut) {
        if (queueOut == null) {
            return null;
        }
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("QUEUE_TYPE", QueueType.OUT.ordinal());
            params.put("MSG_ID", queueOut.getID());
            params.put("LOG_LEVEL", QueueLogLevel.getQueueLogByHttpResponseStatus(status).ordinal());
            params.put("RESPONSE_STATUS", status);
            params.put("LOG_MESSAGE", errorMessage);
            params.put("CREATE_DATE", new Date());
            params.put("CONTRACT_ID", queueOut.getContractId());
            params.put("ISSUE_ID", queueOut.getIssueId());
            if (queueOut.getIssueId() != null) {
                final Issue issue = (Issue)this.issueManager.getIssueObject(queueOut.getIssueId());
                params.put("ISSUE_KEY", (issue != null) ? issue.getKey() : null);
            }
            final QueueLog entry = (QueueLog)this.getDao().create((Class)QueueLog.class, (Map)params);
            return entry;
        }
        catch (Exception e) {
            this.logger.warn(ExtendedLoggerMessageType.OTHER, "QueueLog not saved: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<QueueLog> findByQueueType(final QueueType out, final Integer offset, final Integer limit) {
        final QueueLog[] logs = (QueueLog[])this.getDao().find((Class)QueueLog.class, Query.select().where("QUEUE_TYPE = ?", new Object[] { out.ordinal() }).offset(offset * limit).limit((int)limit).order("CREATE_DATE DESC"));
        return (logs != null) ? Arrays.asList(logs) : new ArrayList<QueueLog>();
    }
    
    @Override
    public Integer countByType(final QueueType in) {
        return this.getDao().count((Class)QueueLog.class, Query.select().where("QUEUE_TYPE = ?", new Object[] { in.ordinal() }));
    }
    
    private String prepareQuery(final Map<String, Object> filters) {
        if (filters == null || filters.keySet().isEmpty()) {
            return "";
        }
        final List<String> conditions = new ArrayList<String>();
        final String databaseType = ((DatabaseConfigurationManager)ComponentAccessor.getComponent((Class)DatabaseConfigurationManager.class)).getDatabaseConfiguration().getDatabaseType();
        boolean quotationMarkInQuery = false;
        if (databaseType.contains("postgres") || databaseType.contains("oracle") || databaseType.contains("hsql") || databaseType.contains("mssql") || databaseType.contains("h2")) {
            quotationMarkInQuery = true;
        }
        int i = 0;
        for (final String field : filters.keySet()) {
            String term = null;
            if (filters.get(field) instanceof List) {
                final StringBuilder listIDs = new StringBuilder("(");
                    if (i > 0) {
                        listIDs.append(",");
                    }
                    listIDs.append(filters.get(field));
                    ++i;
                listIDs.append(")");
                if (listIDs != null && !listIDs.toString().equals("()")) {
                    term = (quotationMarkInQuery ? ("\"" + field + "\"" + " in " + (Object)listIDs) : (field + " in " + (Object)listIDs));
                }
            }
            else {
                term = null;
                if (filters.get(field) instanceof Number) {
                    term = (quotationMarkInQuery ? ("\"" + field + "\"" + " =  ") : (field + " =  "));
                    term += " ";
                    term += filters.get(field).toString();
                }
                else if (filters.get(field) instanceof Enum) {
                    term = (quotationMarkInQuery ? ("\"" + field + "\"" + " =  ") : (field + " =  "));
                    term += "";
                    term += filters.get(field).ordinal();
                }
                else {
                    term = (quotationMarkInQuery ? ("\"" + field + "\"" + " like ") : (field + " like "));
                    term += (quotationMarkInQuery ? "'%" : "\"%");
                    term += filters.get(field).toString();
                    term += (quotationMarkInQuery ? "%'" : "%\"");
                }
            }
            if (term != null) {
                conditions.add(term);
            }
        }
        final StringBuilder query = new StringBuilder("");
        int j = 0;
        for (final String cond : conditions) {
            if (j > 0) {
                query.append((CharSequence)query.append(" AND "));
            }
            query.append(cond);
            ++j;
        }
        return query.toString();
    }
    
    @Override
    public List<QueueLog> findAllByFilter(final Map<String, Object> filters, final Integer offset, final Integer limit) {
        String timeQuery = "";
        if (filters.containsKey("dateFrom") && filters.get("dateFrom") != null && !filters.get("dateFrom").toString().isEmpty()) {
            timeQuery = timeQuery + "CREATE_DATE" + " >= '" + filters.get("dateFrom").toString() + "'";
        }
        if (filters.containsKey("dateTo") && filters.get("dateTo") != null && !filters.get("dateTo").toString().isEmpty()) {
            if (!timeQuery.isEmpty()) {
                timeQuery += " AND ";
            }
            timeQuery = timeQuery + "CREATE_DATE" + " <= '" + filters.get("dateTo").toString() + "'";
        }
        final Object dateFrom = filters.remove("dateFrom");
        final Object dateTo = filters.remove("dateTo");
        String queryParams = this.prepareQuery(filters);
        if (queryParams.isEmpty()) {
            queryParams += timeQuery;
        }
        else if (timeQuery != null && !timeQuery.isEmpty()) {
            queryParams = queryParams + " AND " + timeQuery;
        }
        QueueLog[] logs = null;
        if (queryParams == null || queryParams.isEmpty()) {
            logs = (QueueLog[])this.getDao().find((Class)QueueLog.class, Query.select().offset(offset * limit).limit((int)limit).order("CREATE_DATE DESC"));
        }
        else {
            logs = (QueueLog[])this.getDao().find((Class)QueueLog.class, Query.select().where(" " + queryParams, new Object[0]).offset(offset * limit).limit((int)limit).order("CREATE_DATE DESC"));
        }
        if (dateFrom != null) {
            filters.put("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            filters.put("dateTo", dateTo);
        }
        return (logs == null) ? new ArrayList<QueueLog>() : Arrays.asList(logs);
    }
    
    @Override
    public Integer countAllByFilter(final Map<String, Object> filters) {
        String timeQuery = "";
        if (filters.containsKey("dateFrom") && filters.get("dateFrom") != null && !filters.get("dateFrom").toString().isEmpty()) {
            timeQuery = timeQuery + "CREATE_DATE" + " >= '" + filters.get("dateFrom").toString() + "'";
        }
        if (filters.containsKey("dateTo") && filters.get("dateTo") != null && !filters.get("dateTo").toString().isEmpty()) {
            if (!timeQuery.isEmpty()) {
                timeQuery += " AND ";
            }
            timeQuery = timeQuery + "CREATE_DATE" + " <= '" + filters.get("dateTo").toString() + "'";
        }
        final Object dateFrom = filters.remove("dateFrom");
        final Object dateTo = filters.remove("dateTo");
        String queryParams = this.prepareQuery(filters);
        if (queryParams.isEmpty()) {
            queryParams += timeQuery;
        }
        else if (timeQuery != null && !timeQuery.isEmpty()) {
            queryParams += " AND ";
            queryParams += timeQuery;
        }
        Integer count = null;
        if (queryParams == null || queryParams.isEmpty()) {
            count = this.getDao().count((Class)QueueLog.class);
        }
        else {
            count = this.getDao().count((Class)QueueLog.class, Query.select().where(" " + queryParams, new Object[0]));
        }
        if (dateFrom != null) {
            filters.put("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            filters.put("dateTo", dateTo);
        }
        return (count == null) ? 0 : count;
    }
    
    @Override
    public QueueLog createQueueLog(final Integer status, final String message) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("RESPONSE_STATUS", status);
        params.put("LOG_MESSAGE", message);
        params.put("CREATE_DATE", new Date());
        final QueueLog entry = (QueueLog)this.getDao().create((Class)QueueLog.class, (Map)params);
        return entry;
    }
    
    @Override
    public void clearOutdated(final Integer daysValidity) {
        final Date daysValidityAgo = new DateTime().minusDays((int)daysValidity).toDate();
        final QueueLog[] logs = (QueueLog[])this.getDao().find((Class)QueueLog.class, "CREATE_DATE < ? ", new Object[] { daysValidityAgo });
        if (logs != null && logs.length > 0) {
            this.getDao().delete((RawEntity[])logs);
        }
    }
}
