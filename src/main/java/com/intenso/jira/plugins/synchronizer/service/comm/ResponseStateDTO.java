// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

public class ResponseStateDTO
{
    private boolean successful;
    private List<String> errorMessages;
    private Integer responseForType;
    private Timestamp responseDate;
    private JiraResponseErrorsDTO jiraResponseErrors;
    
    public ResponseStateDTO() {
        this.responseDate = new Timestamp(new Date().getTime());
    }
    
    public ResponseStateDTO(final MessageType responseForType, final List<String> errorMessages, final JiraResponseErrorsDTO jiraResponseErrors) {
        this.responseDate = new Timestamp(new Date().getTime());
        this.successful = false;
        this.errorMessages = errorMessages;
        this.responseForType = responseForType.ordinal();
        this.jiraResponseErrors = jiraResponseErrors;
    }
    
    public boolean isSuccessful() {
        return this.successful;
    }
    
    public void setSuccessful(final boolean successful) {
        this.successful = successful;
    }
    
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public void setErrorMessages(final List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    public Integer getResponseForType() {
        return this.responseForType;
    }
    
    public void setResponseForType(final Integer responseForType) {
        this.responseForType = responseForType;
    }
    
    public Timestamp getResponseDate() {
        return this.responseDate;
    }
    
    public void setResponseDate(final Timestamp responseDate) {
        this.responseDate = responseDate;
    }
    
    public JiraResponseErrorsDTO getJiraResponseErrors() {
        return this.jiraResponseErrors;
    }
    
    public void setJiraResponseErrors(final JiraResponseErrorsDTO jiraResponseErrors) {
        this.jiraResponseErrors = jiraResponseErrors;
    }
}
