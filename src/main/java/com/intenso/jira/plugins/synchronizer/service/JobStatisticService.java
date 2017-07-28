// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.sql.Timestamp;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.JobStatistic;

public interface JobStatisticService extends GenericService<JobStatistic>
{
    JobStatistic create(final String p0);
    
    List<JobStatistic> findByName(final String p0);
    
    Timestamp getLastRunDate(final String p0);
}
