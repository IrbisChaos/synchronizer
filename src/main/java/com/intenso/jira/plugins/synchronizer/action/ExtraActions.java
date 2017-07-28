// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import java.util.Map;
import com.intenso.jira.plugins.synchronizer.jiraservice.TasksSchedulerImpl;
import com.google.common.collect.ImmutableMap;
import com.intenso.jira.plugins.synchronizer.jiraservice.ArchiveQueuesTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.ConfigurationPullTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.PullResponseTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.PullTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.OutgoingResponseTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.IncomingResponseTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.OutgoingTask;
import com.intenso.jira.plugins.synchronizer.jiraservice.IncomingTask;
import com.atlassian.upm.api.license.PluginLicenseManager;

public class ExtraActions extends GenericConfigAction
{
    private static final long serialVersionUID = -4849455L;
    
    public ExtraActions(final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
    }
    
    public String doRunServices() {
        final IncomingTask incomingTask = new IncomingTask();
        final OutgoingTask outgoingTask = new OutgoingTask();
        final IncomingResponseTask incomingResponseTask = new IncomingResponseTask();
        final OutgoingResponseTask outgoingResponseTask = new OutgoingResponseTask();
        final PullTask pullTask = new PullTask();
        final PullResponseTask pullResponseTask = new PullResponseTask();
        final ConfigurationPullTask configurationPullTask = new ConfigurationPullTask();
        final ArchiveQueuesTask archiveQueuesTask = new ArchiveQueuesTask();
        incomingTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.INCOMING_JOB_NAME).build());
        outgoingTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.OUTGOING_JOB_NAME).build());
        incomingResponseTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.INCOMING_RESPONSE_JOB_NAME).build());
        outgoingResponseTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.OUTGOING_RESPONSE_JOB_NAME).build());
        pullTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.PULL_JOB_NAME).build());
        pullResponseTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.PULL_RESPONSE_JOB_NAME).build());
        configurationPullTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.PULL_CONFIGURATION_JOB_NAME).build());
        archiveQueuesTask.doExecute((Map<String, Object>)ImmutableMap.builder().put((Object)"FORCED", (Object)"true").put((Object)"synchronizerJobName", (Object)TasksSchedulerImpl.ARCHIVIZATION_JOB_NAME).build());
        return "runServices";
    }
}
