// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import org.boon.json.annotations.JsonProperty;

public class AlertsT
{
    @JsonProperty("emails")
    public String emails;
    @JsonProperty("jobs")
    public String jobs;
    @JsonProperty("errors")
    public String errors;
    @JsonProperty("cron")
    public String cron;
    
    public String getEmails() {
        return this.emails;
    }
    
    public void setEmails(final String emails) {
        this.emails = emails;
    }
    
    public String getJobs() {
        return this.jobs;
    }
    
    public void setJobs(final String jobs) {
        this.jobs = jobs;
    }
    
    public String getErrors() {
        return this.errors;
    }
    
    public void setErrors(final String errors) {
        this.errors = errors;
    }
    
    public String getCron() {
        return this.cron;
    }
    
    public void setCron(final String cron) {
        this.cron = cron;
    }
}
