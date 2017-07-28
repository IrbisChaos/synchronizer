// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.ArrayList;
import java.util.List;

public class JiraResponseErrorsDTO
{
    private List<String> fieldErrors;
    private List<String> errorMessages;
    
    public JiraResponseErrorsDTO() {
        this.fieldErrors = new ArrayList<String>();
        this.errorMessages = new ArrayList<String>();
    }
    
    public static JiraResponseErrorsDTO withErrorMessages(final String... messages) {
        final JiraResponseErrorsDTO responseErrors = new JiraResponseErrorsDTO();
        for (final String message : messages) {
            responseErrors.getErrorMessages().add(message);
        }
        return responseErrors;
    }
    
    public List<String> getFieldErrors() {
        return this.fieldErrors;
    }
    
    public void setFieldErrors(final List<String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public void setErrorMessages(final List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
