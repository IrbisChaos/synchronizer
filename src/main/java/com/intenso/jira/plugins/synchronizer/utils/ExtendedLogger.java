// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.NotificationService;
import java.text.ParseException;
import java.util.Date;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import java.util.Calendar;
import com.intenso.jira.plugins.synchronizer.entity.AlertHistory;
import com.intenso.jira.plugins.synchronizer.service.AlertHistoryService;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.AlertsService;
import org.apache.log4j.Logger;

public class ExtendedLogger
{
    public static final String DISCR = "|";
    private Logger log;
    
    public ExtendedLogger(final Logger log) {
        this.log = log;
    }
    
    private String buildMessage(final ExtendedLoggerMessageType type, final String msg, final String... params) {
        String paramString = "";
        if (params != null) {
            for (String p : params) {
                if (p != null) {
                    p = p.replace('~', '-');
                    paramString = paramString + p + "|";
                }
            }
        }
        return type.ordinal() + "|" + msg + "|" + paramString;
    }
    
    public void warn(final ExtendedLoggerMessageType type, final String msg, final String... params) {
        this.log.warn((Object)this.buildMessage(type, msg, params));
    }
    
    public void info(final ExtendedLoggerMessageType type, final String msg, final String... params) {
        this.log.info((Object)this.buildMessage(type, msg, params));
    }
    
    public void debug(final ExtendedLoggerMessageType type, final String msg, final String... params) {
        this.log.debug((Object)this.buildMessage(type, msg, params));
    }
    
    public void error(final ExtendedLoggerMessageType type, final String msg, final String... params) {
        this.log.error((Object)this.buildMessage(type, msg, params));
        this.alert(this.buildMessage(type, msg, params));
    }
    
    public void warn(final ExtendedLoggerMessageType type, final String msg) {
        this.log.warn((Object)this.buildMessage(type, msg, (String[])null));
    }
    
    public void info(final ExtendedLoggerMessageType type, final String msg) {
        this.log.info((Object)this.buildMessage(type, msg, (String[])null));
    }
    
    public void debug(final ExtendedLoggerMessageType type, final String msg) {
        this.log.debug((Object)this.buildMessage(type, msg, (String[])null));
    }
    
    public void error(final ExtendedLoggerMessageType type, final String msg) {
        this.log.error((Object)this.buildMessage(type, msg, (String[])null));
        this.alert(this.buildMessage(type, msg, (String[])null));
    }
    
    private void alert(final String message) {
        final AlertsService alertsService = (AlertsService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)AlertsService.class);
        final Boolean flag = Boolean.parseBoolean(alertsService.getConfiguration().getErrors());
        if (flag != null && flag) {
            final AlertHistoryService alertHistoryService = (AlertHistoryService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)AlertHistoryService.class);
            final List<AlertHistory> list = alertHistoryService.findByMessage(message);
            boolean skip = false;
            if (!list.isEmpty()) {
                final AlertHistory alert = list.get(0);
                try {
                    final Calendar cal = Calendar.getInstance();
                    String cronExpr = ((AlertsService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)AlertsService.class)).getConfiguration().getCron();
                    if (cronExpr == null || cronExpr.toString().isEmpty()) {
                        cronExpr = "0 0 1 * * ? *";
                    }
                    final CronTrigger cronTrigger = new CronTrigger();
                    cronTrigger.setCronExpression(cronExpr.toString());
                    cronTrigger.triggered(null);
                    CronExpression cronExpression = null;
                    cronExpression = new CronExpression(cronTrigger.getCronExpression());
                    final Date next = cronExpression.getNextValidTimeAfter(alert.getLast());
                    if (next.after(cal.getTime())) {
                        skip = true;
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!skip) {
                alertHistoryService.create(message);
                ((NotificationService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)NotificationService.class)).notifyAboutAlert(message);
            }
        }
    }
}
