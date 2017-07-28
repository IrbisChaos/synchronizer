// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.config;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.intenso.jira.plugins.synchronizer.utils.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SynchronizerConfigBuilder implements Builder<SynchronizerConfig>
{
    @JsonProperty
    private String cronOutgoingJob;
    @JsonProperty
    private String cronOutgoingResponseJob;
    @JsonProperty
    private String cronIncomingJob;
    @JsonProperty
    private String cronIncomingResponseJob;
    @JsonProperty
    private String cronPullJob;
    @JsonProperty
    private String cronPullResponsesJob;
    @JsonProperty
    private String cronPullConfigurationJob;
    @JsonProperty
    private String cronArchivizationJob;
    @JsonProperty
    private String cronClearOldQueueLogsJob;
    @JsonProperty
    private Integer retryCount;
    @JsonProperty
    private Integer queueIn;
    @JsonProperty
    private Integer queueOut;
    @JsonProperty
    private Integer workflowRestApi;
    @JsonProperty
    private Integer genDiagnosticPanel;
    @JsonProperty
    private Integer genRemoteIssuePanel;
    @JsonProperty
    private String[] genExtCommnetsTabPerm;
    @JsonProperty
    private String[] genCreateOnDemandPerm;
    
    @Override
    public SynchronizerConfig build() {
        return new SynchronizerConfig(this);
    }
    
    public String getCronOutgoingJob() {
        return this.cronOutgoingJob;
    }
    
    public SynchronizerConfigBuilder cronOutgoingJob(final String cronOutgoingJob) {
        this.cronOutgoingJob = cronOutgoingJob;
        return this;
    }
    
    public String getCronOutgoingResponseJob() {
        return this.cronOutgoingResponseJob;
    }
    
    public SynchronizerConfigBuilder cronOutgoingResponseJob(final String cronOutgoingResponseJob) {
        this.cronOutgoingResponseJob = cronOutgoingResponseJob;
        return this;
    }
    
    public String getCronIncomingJob() {
        return this.cronIncomingJob;
    }
    
    public SynchronizerConfigBuilder cronIncomingJob(final String cronIncomingJob) {
        this.cronIncomingJob = cronIncomingJob;
        return this;
    }
    
    public String getCronIncomingResponseJob() {
        return this.cronIncomingResponseJob;
    }
    
    public SynchronizerConfigBuilder cronIncomingResponseJob(final String cronIncomingResponseJob) {
        this.cronIncomingResponseJob = cronIncomingResponseJob;
        return this;
    }
    
    public String getCronArchivizationJob() {
        return this.cronArchivizationJob;
    }
    
    public SynchronizerConfigBuilder cronArchivizationJob(final String cronArchivizationJob) {
        this.cronArchivizationJob = cronArchivizationJob;
        return this;
    }
    
    public Integer getRetryCount() {
        return this.retryCount;
    }
    
    public SynchronizerConfigBuilder retryCount(final Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }
    
    public String getCronPullJob() {
        return this.cronPullJob;
    }
    
    public String getCronPullResponseJob() {
        return this.cronPullResponsesJob;
    }
    
    public String getCronPullResponsesJob() {
        return this.cronPullResponsesJob;
    }
    
    public SynchronizerConfigBuilder cronPullResponsesJob(final String cronPullResponsesJob) {
        this.cronPullResponsesJob = cronPullResponsesJob;
        return this;
    }
    
    public SynchronizerConfigBuilder cronPullJob(final String cronPullJob) {
        this.cronPullJob = cronPullJob;
        return this;
    }
    
    public SynchronizerConfigBuilder cronPullConfigurationJob(final String cronPullConfigurationJob) {
        this.cronPullConfigurationJob = cronPullConfigurationJob;
        return this;
    }
    
    public String getCronPullConfigurationJob() {
        return this.cronPullConfigurationJob;
    }
    
    public SynchronizerConfigBuilder cronClearOldQueueLogsJob(final String cronClearOldQueueLogsJob) {
        this.cronClearOldQueueLogsJob = cronClearOldQueueLogsJob;
        return this;
    }
    
    public String getCronClearOldQueueLogsJob() {
        return this.cronClearOldQueueLogsJob;
    }
    
    public Integer getQueueIn() {
        return this.queueIn;
    }
    
    public SynchronizerConfigBuilder queueIn(final Integer queueIn) {
        this.queueIn = queueIn;
        return this;
    }
    
    public Integer getQueueOut() {
        return this.queueOut;
    }
    
    public SynchronizerConfigBuilder queueOut(final Integer queueOut) {
        this.queueOut = queueOut;
        return this;
    }
    
    public Integer getGenDiagnosticPanel() {
        return this.genDiagnosticPanel;
    }
    
    public SynchronizerConfigBuilder genDiagnosticPanel(final Integer genDiagnosticPanel) {
        this.genDiagnosticPanel = genDiagnosticPanel;
        return this;
    }
    
    public String[] getGenExtCommnetsTabPerm() {
        return this.genExtCommnetsTabPerm;
    }
    
    public SynchronizerConfigBuilder genExtCommnetsTabPerm(final String genExtCommnetsTabPerm) {
        if (genExtCommnetsTabPerm != null && !genExtCommnetsTabPerm.isEmpty()) {
            this.genExtCommnetsTabPerm = genExtCommnetsTabPerm.split("\\\\");
        }
        else {
            this.genExtCommnetsTabPerm = null;
        }
        return this;
    }
    
    public String[] getGenCreateOnDemandPerm() {
        return this.genCreateOnDemandPerm;
    }
    
    public SynchronizerConfigBuilder genCreateOnDemandPerm(final String genCreateOnDemandPerm) {
        if (genCreateOnDemandPerm != null && !genCreateOnDemandPerm.isEmpty()) {
            this.genCreateOnDemandPerm = genCreateOnDemandPerm.split("\\\\");
        }
        else {
            this.genCreateOnDemandPerm = null;
        }
        return this;
    }
    
    public Integer getGenRemoteIssuePanel() {
        return this.genRemoteIssuePanel;
    }
    
    public SynchronizerConfigBuilder genRemoteIssuePanel(final Integer genRemoteIssuePanel) {
        this.genRemoteIssuePanel = genRemoteIssuePanel;
        return this;
    }
    
    public Integer getWorkflowRestApi() {
        return this.workflowRestApi;
    }
    
    public SynchronizerConfigBuilder workflowRestApi(final Integer workflowRestApi) {
        this.workflowRestApi = workflowRestApi;
        return this;
    }
}
