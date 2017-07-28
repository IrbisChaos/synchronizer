// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Map;
import java.util.HashMap;
import java.sql.Timestamp;
import java.util.Calendar;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.Statistic;

public class StatisticServiceImpl extends GenericServiceImpl<Statistic> implements StatisticService
{
    public static final String COL_DATE = "DATE";
    public static final String COL_CONNECTION = "CONNECTION_ID";
    public static final String COL_CONTRACT = "CONTRACT_ID";
    public static final String COL_MSG_TYPE = "MESSAGE_TYPE";
    public static final String COL_QUEUE = "IN_OUT";
    public static final String COL_PROCESSING_TIME = "PROCESSING_TIME";
    public static final String COL_MSG_SIZE = "MSG_SIZE";
    public static final String COL_ATT_SIZE = "ATTACHMENT_SIZE";
    
    public StatisticServiceImpl(final ActiveObjects dao) {
        super(dao, Statistic.class);
    }
    
    @Override
    public Statistic create(final Integer connectionId, final Integer contractId, final MessageType msgType, final QueueType queue, final Long processingTime, final Integer msgSize, final Long attachmentSize) {
        final Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return this.create(new Timestamp(cal.getTime().getTime()), connectionId, contractId, msgType, queue, processingTime, msgSize, attachmentSize);
    }
    
    @Override
    public Statistic create(final Timestamp date, final Integer connectionId, final Integer contractId, final MessageType msgType, final QueueType queue, final Long processingTime, final Integer msgSize, final Long attachmentSize) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("DATE", date);
        params.put("CONNECTION_ID", connectionId);
        params.put("CONTRACT_ID", contractId);
        if (msgType != null) {
            params.put("MESSAGE_TYPE", msgType.ordinal());
        }
        if (queue != null) {
            params.put("IN_OUT", queue.ordinal());
        }
        params.put("PROCESSING_TIME", processingTime);
        params.put("MSG_SIZE", msgSize);
        params.put("ATTACHMENT_SIZE", attachmentSize);
        final Statistic entry = (Statistic)this.getDao().create((Class)Statistic.class, (Map)params);
        return entry;
    }
}
