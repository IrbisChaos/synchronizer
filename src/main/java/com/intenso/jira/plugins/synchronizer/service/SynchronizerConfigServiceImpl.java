// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.atlassian.crowd.embedded.api.Group;
import java.util.Iterator;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.component.ComponentAccessor;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.jiraservice.TasksSchedulerImpl;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfigBuilder;
import org.apache.commons.lang.ObjectUtils;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import java.util.concurrent.ConcurrentHashMap;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class SynchronizerConfigServiceImpl implements SynchronizerConfigService
{
    private PluginSettingsFactory pluginSettingsFactory;
    private static final String CONFIG_KEY = "SYNCHRONIZER_GLOBAL_CONFIG";
    private final ConcurrentHashMap<String, SynchronizerConfig> configCache;
    private static final String PLUGIN_STORAGE_KEY = "INTENSO.JIRA.SYNCHRONIZER.";
    public static final String SYNCHRONIZER_SETTINGS_KEY = "com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService";
    protected static final String SETTING_CRON_OUTGOING_JOB = "outgoingJobCron";
    protected static final String SETTING_CRON_OUTGOING_RESPONSE_JOB = "outgoingResponseJobCron";
    protected static final String SETTING_CRON_INCOMING_JOB = "incomingJobCron";
    protected static final String SETTING_CRON_INCOMING_RESPONSE_JOB = "incomingResponseJobCron";
    protected static final String SETTING_CRON_ARCHIVIZATION_JOB = "archivizationJobCron";
    protected static final String SETTING_CRON_PULL_JOB = "pullJobCron";
    protected static final String SETTING_CRON_PULL_RESPONSE_JOB = "pullResponseJobCron";
    protected static final String SETTING_CRON_PULL_CONFIGURATION_JOB = "pullConfigurationJobCron";
    protected static final String SETTING_CRON_CLEAR_OLD_QUEUE_LOGS_JOB = "clearOldQueueLogsJobCron";
    protected static final String SETTING_RETRY_COUNT = "retryCount";
    protected static final String SETTING_QUEUE_IN_ARCHIVIZATION = "queueInArchivization";
    protected static final String SETTING_QUEUE_OUT_ARCHIVIZATION = "queueOutArchivization";
    protected static final String SETTING_WORKFLOW_REST_API = "workflowRestApi";
    protected static final String SETTING_GEN_DIAGNOSTIC_PANEL_DISABLED = "genDiagnosticPanel";
    protected static final String SETTING_GEN_REMOTE_ISSUE_PANEL = "genRemoteIssuePanel";
    protected static final String SETTING_GEN_EXT_COMMENTS_TAB_PERM = "genExtCommnetsTabPerm";
    protected static final String SETTING_GEN_CREATE_ON_DEMAND_PERM = "genCreateOnDemandPerm";
    
    public SynchronizerConfigServiceImpl(final PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.configCache = new ConcurrentHashMap<String, SynchronizerConfig>();
    }
    
    @Override
    public PluginSettings saveConfig(final SynchronizerConfig config) {
        final PluginSettings settings = this.getSettings();
        settings.put("outgoingJobCron", (Object)config.getCronOutgoingJob());
        settings.put("outgoingResponseJobCron", (Object)config.getCronOutgoingResponseJob());
        settings.put("incomingJobCron", (Object)config.getCronIncomingJob());
        settings.put("incomingResponseJobCron", (Object)config.getCronIncomingResponseJob());
        settings.put("archivizationJobCron", (Object)config.getCronArchivizationJob());
        settings.put("retryCount", (Object)config.getRetryCount());
        settings.put("pullJobCron", (Object)config.getCronPullJob());
        settings.put("pullResponseJobCron", (Object)config.getCronPullResponsesJob());
        settings.put("pullConfigurationJobCron", (Object)config.getCronPullConfigurationJob());
        settings.put("clearOldQueueLogsJobCron", (Object)config.getCronClearOldQueueLogsJob());
        settings.put("queueInArchivization", (Object)((config.getQueueIn() != null) ? config.getQueueIn().toString() : ""));
        settings.put("queueOutArchivization", (Object)((config.getQueueOut() != null) ? config.getQueueOut().toString() : ""));
        settings.put("workflowRestApi", (Object)((config.getWorkflowRestApi() != null) ? config.getWorkflowRestApi().toString() : ""));
        settings.put("genDiagnosticPanel", (Object)((config.getGenDiagnosticPanel() != null) ? config.getGenDiagnosticPanel().toString() : ""));
        settings.put("genRemoteIssuePanel", (Object)((config.getGenRemoteIssuePanel() != null) ? config.getGenRemoteIssuePanel().toString() : ""));
        settings.put("genExtCommnetsTabPerm", (Object)((config.getGenExtCommnetsTabPermString() != null) ? config.getGenExtCommnetsTabPermString() : ""));
        settings.put("genCreateOnDemandPerm", (Object)((config.getGenCreateOnDemandPermString() != null) ? config.getGenCreateOnDemandPermString() : ""));
        this.clearCache();
        return settings;
    }
    
    @Override
    public void clearCache() {
        this.configCache.remove("SYNCHRONIZER_GLOBAL_CONFIG");
    }
    
    private void rebuildCache() {
        final PluginSettings settings = this.getSettings();
        final String retryCount = ObjectUtils.toString(settings.get("retryCount"));
        final String queueIn = ObjectUtils.toString(settings.get("queueInArchivization"));
        final String queueOut = ObjectUtils.toString(settings.get("queueOutArchivization"));
        final String workflowRestApi = ObjectUtils.toString(settings.get("workflowRestApi"));
        final String genDiagnosticPanel = ObjectUtils.toString(settings.get("genDiagnosticPanel"));
        final String genRemoteIssuePanel = ObjectUtils.toString(settings.get("genRemoteIssuePanel"));
        final SynchronizerConfigBuilder builder = new SynchronizerConfigBuilder();
        final SynchronizerConfig config = builder.cronOutgoingJob(ObjectUtils.toString(settings.get("outgoingJobCron"))).cronOutgoingResponseJob(ObjectUtils.toString(settings.get("outgoingResponseJobCron"))).cronIncomingJob(ObjectUtils.toString(settings.get("incomingJobCron"))).cronIncomingResponseJob(ObjectUtils.toString(settings.get("incomingResponseJobCron"))).cronArchivizationJob(ObjectUtils.toString(settings.get("archivizationJobCron"))).cronPullJob(ObjectUtils.toString(settings.get("pullJobCron"))).cronPullResponsesJob(ObjectUtils.toString(settings.get("pullResponseJobCron"))).cronPullConfigurationJob(ObjectUtils.toString(settings.get("pullConfigurationJobCron"))).cronClearOldQueueLogsJob(ObjectUtils.toString(settings.get("clearOldQueueLogsJobCron"))).retryCount((retryCount == null || retryCount.isEmpty()) ? null : Integer.parseInt(retryCount)).queueIn((queueIn == null || queueIn.isEmpty()) ? null : Integer.parseInt(queueIn)).queueOut((queueOut == null || queueOut.isEmpty()) ? null : Integer.parseInt(queueOut)).workflowRestApi((workflowRestApi == null || workflowRestApi.isEmpty()) ? null : Integer.parseInt(workflowRestApi)).genDiagnosticPanel((genDiagnosticPanel == null || genDiagnosticPanel.isEmpty()) ? null : Integer.parseInt(genDiagnosticPanel)).genRemoteIssuePanel((genRemoteIssuePanel == null || genRemoteIssuePanel.isEmpty()) ? null : Integer.parseInt(genRemoteIssuePanel)).genExtCommnetsTabPerm(ObjectUtils.toString(settings.get("genExtCommnetsTabPerm"))).genCreateOnDemandPerm(ObjectUtils.toString(settings.get("genCreateOnDemandPerm"))).build();
        this.configCache.put("SYNCHRONIZER_GLOBAL_CONFIG", config);
    }
    
    @Override
    public SynchronizerConfig getConfig() {
        if (this.configCache.contains("SYNCHRONIZER_GLOBAL_CONFIG") && this.configCache.get("SYNCHRONIZER_GLOBAL_CONFIG") != null) {
            return this.configCache.get("SYNCHRONIZER_GLOBAL_CONFIG");
        }
        this.rebuildCache();
        return this.configCache.get("SYNCHRONIZER_GLOBAL_CONFIG");
    }
    
    private PluginSettings getSettings() {
        final PluginSettings settings = this.pluginSettingsFactory.createSettingsForKey("com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService");
        return settings;
    }
    
    @Override
    public Long getLastRun(final String jobName) {
        final PluginSettings pluginSettings = this.pluginSettingsFactory.createSettingsForKey("com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService");
        final Object value = pluginSettings.get("INTENSO.JIRA.SYNCHRONIZER." + jobName + ".LAST_RUN");
        return (value == null || value.toString().isEmpty()) ? null : Long.parseLong(value.toString());
    }
    
    @Override
    public void setLastRun(final String jobName, final Long time) {
        final PluginSettings pluginSettings = this.pluginSettingsFactory.createSettingsForKey("com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService");
        pluginSettings.remove("INTENSO.JIRA.SYNCHRONIZER." + jobName + ".LAST_RUN");
        pluginSettings.put("INTENSO.JIRA.SYNCHRONIZER." + jobName + ".LAST_RUN", (Object)time.toString());
    }
    
    @Override
    public String getCronByJobName(final String jobName) {
        if (jobName == null) {
            return null;
        }
        if (jobName.equals(TasksSchedulerImpl.ARCHIVIZATION_JOB_NAME)) {
            return this.getConfig().getCronArchivizationJob();
        }
        if (jobName.equals(TasksSchedulerImpl.INCOMING_JOB_NAME)) {
            return this.getConfig().getCronIncomingJob();
        }
        if (jobName.equals(TasksSchedulerImpl.INCOMING_RESPONSE_JOB_NAME)) {
            return this.getConfig().getCronIncomingResponseJob();
        }
        if (jobName.equals(TasksSchedulerImpl.OUTGOING_JOB_NAME)) {
            return this.getConfig().getCronOutgoingJob();
        }
        if (jobName.equals(TasksSchedulerImpl.OUTGOING_RESPONSE_JOB_NAME)) {
            return this.getConfig().getCronOutgoingResponseJob();
        }
        if (jobName.equals(TasksSchedulerImpl.PULL_JOB_NAME)) {
            return this.getConfig().getCronPullJob();
        }
        if (jobName.equals(TasksSchedulerImpl.PULL_RESPONSE_JOB_NAME)) {
            return this.getConfig().getCronPullResponsesJob();
        }
        if (jobName.equals(TasksSchedulerImpl.PULL_CONFIGURATION_JOB_NAME)) {
            return this.getConfig().getCronPullConfigurationJob();
        }
        if (jobName.equals(TasksSchedulerImpl.CLEAR_OLD_QUEUE_LOGS_JOB_NAME)) {
            return this.getConfig().getCronClearOldQueueLogsJob();
        }
        return null;
    }
    
    private Boolean isInGroup(final List<String> groupNames) {
        final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        if (groupNames != null && groupNames.size() > 0) {
            final GroupManager gm = ComponentAccessor.getGroupManager();
            for (final String groupName : groupNames) {
                final Group group = gm.getGroup(groupName);
                if (group != null && gm.isUserInGroup(user, group)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    @Override
    public Boolean canCreateRemoteIssueOnDemand() {
        final SynchronizerConfig config = this.getConfig();
        return this.isInGroup((config != null) ? config.getGenCreateOnDemandPerm() : null);
    }
    
    @Override
    public Boolean canExternalComments() {
        final SynchronizerConfig config = this.getConfig();
        return this.isInGroup((config != null) ? config.getGenExtCommnetsTabPerm() : null);
    }
}
