// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.io.InputStream;
import java.text.MessageFormat;
import org.jfree.util.Log;
import java.io.IOException;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.cluster.logging.LoggingManager;
import org.apache.log4j.Appender;
import org.apache.log4j.Priority;
import org.apache.log4j.Level;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import com.atlassian.jira.logging.JiraHomeAppender;
import org.apache.log4j.Logger;
import java.util.Properties;

public class ExtendedLoggerFactory
{
    private static Properties p;
    
    public static ExtendedLogger getLogger(final Class clazz) {
        final Logger log = Logger.getLogger(clazz);
        final String appenderName = "intenso-synchronizerlog";
        Appender appender = log.getAppender(appenderName);
        if (appender == null) {
            synchronized (ExtendedLoggerFactory.class) {
                appender = log.getAppender(appenderName);
                if (appender == null) {
                    final JiraHomeAppender jiraHomeAppender = new JiraHomeAppender();
                    jiraHomeAppender.setName(appenderName);
                    jiraHomeAppender.setFile("intenso-synchronizer.log");
                    jiraHomeAppender.setMaxFileSize("20480KB");
                    jiraHomeAppender.setMaxBackupIndex(10);
                    jiraHomeAppender.setLayout((Layout)new PatternLayout("%d{yyyy-MM-dd hh:mm:ss}|%-5p|%m %n"));
                    jiraHomeAppender.setThreshold((Priority)Level.DEBUG);
                    jiraHomeAppender.setAppend(true);
                    jiraHomeAppender.activateOptions();
                    jiraHomeAppender.setImmediateFlush(true);
                    log.addAppender((Appender)jiraHomeAppender);
                    ((LoggingManager)ComponentAccessor.getComponent((Class)LoggingManager.class)).setLogLevel("com.intenso.jira.plugins.synchronizer", "DEBUG");
                }
            }
        }
        log.setAdditivity(false);
        return new ExtendedLogger(log);
    }
    
    public static String decorateLine(final String line) {
        if (ExtendedLoggerFactory.p == null) {
            final InputStream is = ClassLoaderUtils.getResourceAsStream("synchronizer-extended-logger.properties", (Class)ExtendedLoggerFactory.class);
            ExtendedLoggerFactory.p = new Properties();
            try {
                ExtendedLoggerFactory.p.load(is);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        String result = "";
        if (line != null && !ExtendedLoggerFactory.p.isEmpty()) {
            final String[] elements = line.split("\\|");
            if (elements.length > 2) {
                String type = elements[2];
                try {
                    type = ExtendedLoggerMessageType.fromValue(Integer.parseInt(type)).name();
                }
                catch (Exception e2) {
                    Log.error((Object)e2.getMessage());
                }
                result = result + elements[0] + "|" + elements[1] + "|" + type;
            }
            if (elements.length > 3) {
                String msg = ExtendedLoggerFactory.p.getProperty(elements[3], elements[3]);
                if (elements.length > 4) {
                    final String[] params = new String[elements.length - 4];
                    for (int i = 4; i < elements.length; ++i) {
                        params[i - 4] = elements[i];
                    }
                    try {
                        msg = MessageFormat.format(msg, (Object[])params);
                    }
                    catch (Exception ex) {}
                }
                result = result + "|" + msg;
            }
        }
        if (result == null || result.isEmpty()) {
            result = line;
        }
        return result;
    }
}
