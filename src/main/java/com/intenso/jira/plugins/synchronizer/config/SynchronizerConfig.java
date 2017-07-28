// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.config;

import java.util.Iterator;
import java.util.Arrays;
import java.util.List;

public class SynchronizerConfig
{
    private String cronOutgoingJob;
    private String cronOutgoingResponseJob;
    private String cronIncomingJob;
    private String cronIncomingResponseJob;
    private String cronArchivizationJob;
    private String cronPullJob;
    private String cronPullResponsesJob;
    private String cronPullConfigurationJob;
    private String cronClearOldQueueLogsJob;
    private Integer queueIn;
    private Integer queueOut;
    private Integer workflowRestApi;
    private Integer genDiagnosticPanel;
    private Integer genRemoteIssuePanel;
    private List<String> genExtCommnetsTabPerm;
    private List<String> genCreateOnDemandPerm;
    private final Integer retryCount;
    
    public SynchronizerConfig(final SynchronizerConfigBuilder builder) {
        this.cronOutgoingJob = builder.getCronOutgoingJob();
        this.cronOutgoingResponseJob = builder.getCronOutgoingResponseJob();
        this.cronIncomingJob = builder.getCronIncomingJob();
        this.cronIncomingResponseJob = builder.getCronIncomingResponseJob();
        this.cronArchivizationJob = builder.getCronArchivizationJob();
        this.retryCount = builder.getRetryCount();
        this.cronPullJob = builder.getCronPullJob();
        this.cronPullResponsesJob = builder.getCronPullResponseJob();
        this.cronPullConfigurationJob = builder.getCronPullConfigurationJob();
        this.cronClearOldQueueLogsJob = builder.getCronClearOldQueueLogsJob();
        this.queueIn = builder.getQueueIn();
        this.queueOut = builder.getQueueOut();
        this.workflowRestApi = builder.getWorkflowRestApi();
        this.genDiagnosticPanel = builder.getGenDiagnosticPanel();
        this.genRemoteIssuePanel = builder.getGenRemoteIssuePanel();
        this.genExtCommnetsTabPerm = ((builder.getGenExtCommnetsTabPerm() != null) ? Arrays.asList(builder.getGenExtCommnetsTabPerm()) : null);
        this.genCreateOnDemandPerm = ((builder.getGenCreateOnDemandPerm() != null) ? Arrays.asList(builder.getGenCreateOnDemandPerm()) : null);
    }
    
    public String getCronOutgoingJob() {
        return this.cronOutgoingJob;
    }
    
    public String getCronOutgoingResponseJob() {
        return this.cronOutgoingResponseJob;
    }
    
    public String getCronIncomingJob() {
        return this.cronIncomingJob;
    }
    
    public String getCronIncomingResponseJob() {
        return this.cronIncomingResponseJob;
    }
    
    public String getCronArchivizationJob() {
        return this.cronArchivizationJob;
    }
    
    public Integer getRetryCount() {
        return this.retryCount;
    }
    
    public String getCronPullJob() {
        return this.cronPullJob;
    }
    
    public String getCronPullResponsesJob() {
        return this.cronPullResponsesJob;
    }
    
    public String getCronPullConfigurationJob() {
        return this.cronPullConfigurationJob;
    }
    
    public String getCronClearOldQueueLogsJob() {
        return this.cronClearOldQueueLogsJob;
    }
    
    public Integer getQueueIn() {
        return this.queueIn;
    }
    
    public Integer getQueueOut() {
        return this.queueOut;
    }
    
    public void setCronOutgoingJob(final String value) {
        this.cronOutgoingJob = value;
    }
    
    public void setCronOutgoingResponseJob(final String value) {
        this.cronOutgoingResponseJob = value;
    }
    
    public void setCronIncomingJob(final String value) {
        this.cronIncomingJob = value;
    }
    
    public void setCronIncomingResponseJob(final String value) {
        this.cronIncomingResponseJob = value;
    }
    
    public void setCronArchivizationJob(final String value) {
        this.cronArchivizationJob = value;
    }
    
    public void setCronPullJob(final String value) {
        this.cronPullJob = value;
    }
    
    public void setCronPullResponsesJob(final String value) {
        this.cronPullResponsesJob = value;
    }
    
    public void setCronPullConfigurationJob(final String value) {
        this.cronPullConfigurationJob = value;
    }
    
    public void setCronClearOldQueueLogsJob(final String value) {
        this.cronClearOldQueueLogsJob = value;
    }
    
    public void setQueueIn(final Integer value) {
        this.queueIn = value;
    }
    
    public void setQueueOut(final Integer value) {
        this.queueOut = value;
    }
    
    public Integer getGenDiagnosticPanel() {
        return this.genDiagnosticPanel;
    }
    
    public void setGenDiagnosticPanel(final Integer genDiagnosticPanel) {
        this.genDiagnosticPanel = genDiagnosticPanel;
    }
    
    public List<String> getGenExtCommnetsTabPerm() {
        return this.genExtCommnetsTabPerm;
    }
    
    public String getGenExtCommnetsTabPermString() {
        if (this.genExtCommnetsTabPerm != null && this.genExtCommnetsTabPerm.size() > 0) {
            final StringBuilder strBuilder = new StringBuilder();
            for (final String s : this.genExtCommnetsTabPerm) {
                strBuilder.append("\\").append(s);
            }
            final String newString = strBuilder.toString();
            return newString.substring(1);
        }
        return null;
    }
    
    public List<String> getGenCreateOnDemandPerm() {
        return this.genCreateOnDemandPerm;
    }
    
    public String getGenCreateOnDemandPermString() {
        if (this.genCreateOnDemandPerm != null && this.genCreateOnDemandPerm.size() > 0) {
            final StringBuilder strBuilder = new StringBuilder();
            for (final String s : this.genCreateOnDemandPerm) {
                strBuilder.append("\\").append(s);
            }
            final String newString = strBuilder.toString();
            return newString.substring(1);
        }
        return null;
    }
    
    public void setGenExtCommnetsTabPerm(final List<String> genExtCommnetsTabPerm) {
        this.genExtCommnetsTabPerm = genExtCommnetsTabPerm;
    }
    
    public Integer getGenRemoteIssuePanel() {
        return this.genRemoteIssuePanel;
    }
    
    public void setGenRemoteIssuePanel(final Integer genRemoteIssuePanel) {
        this.genRemoteIssuePanel = genRemoteIssuePanel;
    }
    
    public Integer getWorkflowRestApi() {
        return this.workflowRestApi;
    }
    
    public void setWorkflowRestApi(final Integer workflowRestApi) {
        this.workflowRestApi = workflowRestApi;
    }
}
