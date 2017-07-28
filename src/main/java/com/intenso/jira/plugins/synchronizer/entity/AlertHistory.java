// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.entity;

import java.sql.Timestamp;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import net.java.ao.Entity;

@Table("alertHistory")
public interface AlertHistory extends Entity
{
    @StringLength(-1)
    String getMessage();
    
    Timestamp getLast();
    
    void setMessage(final String p0);
    
    void setLast(final Timestamp p0);
}
