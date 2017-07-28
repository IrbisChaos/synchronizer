// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import java.io.Serializable;

public class WorkflowMapping implements Serializable
{
    private static final long serialVersionUID = -2420832431718964127L;
    private String id;
    private String name;
    private String workflow;
    
    public WorkflowMapping() {
    }
    
    public WorkflowMapping(final String id, final String name, final String workflow) {
        this.id = id;
        this.name = name;
        this.workflow = workflow;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getWorkflow() {
        return this.workflow;
    }
    
    public void setWorkflow(final String workflow) {
        this.workflow = workflow;
    }
}
