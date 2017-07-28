// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Iterator;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.AlertHistory;

public class AlertHistoryServiceImpl extends GenericServiceImpl<AlertHistory> implements AlertHistoryService
{
    public static final String COL_MESSAGE = "MESSAGE";
    public static final String COL_LAST = "LAST";
    
    public AlertHistoryServiceImpl(final ActiveObjects dao) {
        super(dao, AlertHistory.class);
    }
    
    @Override
    public AlertHistory create(String message) {
        final List<AlertHistory> list = this.findByMessage(message);
        AlertHistory entry = null;
        if (list.isEmpty()) {
            final Map<String, Object> params = new HashMap<String, Object>();
            if (message.length() > 255) {
                message = message.substring(0, 254);
            }
            params.put("MESSAGE", message);
            params.put("LAST", new Timestamp(new Date().getTime()));
            entry = (AlertHistory)this.getDao().create((Class)AlertHistory.class, (Map)params);
        }
        else {
            entry = list.get(0);
            entry.setLast(new Timestamp(new Date().getTime()));
            entry = this.update(entry);
        }
        return entry;
    }
    
    @Override
    public List<AlertHistory> findByMessage(String message) {
        if (message.length() > 255) {
            message = message.substring(0, 254);
        }
        final AlertHistory[] entries = (AlertHistory[])this.getDao().find((Class)AlertHistory.class, Query.select().where("MESSAGE = ?", new Object[] { message }));
        return Arrays.asList(entries);
    }
    
    @Override
    public void clean() {
        final List<AlertHistory> all = this.getAll();
        for (final AlertHistory in : all) {
            this.delete(Integer.valueOf(in.getID()));
        }
    }
}
