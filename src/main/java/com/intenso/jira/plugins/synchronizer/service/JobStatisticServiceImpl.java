// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.JobStatistic;

public class JobStatisticServiceImpl extends GenericServiceImpl<JobStatistic> implements JobStatisticService
{
    public static final String COL_NAME = "NAME";
    public static final String COL_LAST = "LAST";
    
    public JobStatisticServiceImpl(final ActiveObjects dao) {
        super(dao, JobStatistic.class);
    }
    
    @Override
    public JobStatistic create(final String name) {
        final List<JobStatistic> list = this.findByName(name);
        JobStatistic entry = null;
        if (list.isEmpty()) {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("NAME", name);
            params.put("LAST", new Timestamp(new Date().getTime()));
            entry = (JobStatistic)this.getDao().create((Class)JobStatistic.class, (Map)params);
        }
        else {
            entry = list.get(0);
            entry.setLast(new Timestamp(new Date().getTime()));
            entry = this.update(entry);
        }
        return entry;
    }
    
    @Override
    public List<JobStatistic> findByName(final String name) {
        final JobStatistic[] entries = (JobStatistic[])this.getDao().find((Class)JobStatistic.class, Query.select().where("NAME = ?", new Object[] { name }));
        return Arrays.asList(entries);
    }
    
    @Override
    public Timestamp getLastRunDate(final String jobName) {
        final List<JobStatistic> jobStatistics = this.findByName(jobName);
        if (jobStatistics.size() == 1) {
            return jobStatistics.get(0).getLast();
        }
        return null;
    }
}
