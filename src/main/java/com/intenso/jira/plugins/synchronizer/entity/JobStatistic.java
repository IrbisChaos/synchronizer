// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.sql.Timestamp;
import net.java.ao.schema.Table;
import net.java.ao.Entity;

@Table("jobStatistic")
public interface JobStatistic extends Entity
{
    String getName();
    
    Timestamp getLast();
    
    void setName(final String p0);
    
    void setLast(final Timestamp p0);
}
