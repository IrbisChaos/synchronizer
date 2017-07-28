// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

public class FieldErrorDTO
{
    private String field;
    private String message;
    
    public FieldErrorDTO(final String field, final String message) {
        this.field = field;
        this.message = message;
    }
    
    public String getField() {
        return this.field;
    }
    
    public void setField(final String field) {
        this.field = field;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
