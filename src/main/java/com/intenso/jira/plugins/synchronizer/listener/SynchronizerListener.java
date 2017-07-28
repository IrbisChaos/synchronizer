// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.listener;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.event.type.EventType;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicationService;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class SynchronizerListener implements InitializingBean, DisposableBean
{
    private final EventPublisher eventPublisher;
    private CommunicationService communicationService;
    private PluginLicenseManager pluginLicenseManager;
    
    public SynchronizerListener(final EventPublisher eventPublisher, final PluginLicenseManager pluginLicenseManager) {
        this.eventPublisher = eventPublisher;
        this.pluginLicenseManager = pluginLicenseManager;
    }
    
    @EventListener
    public void onIssueEvent(final IssueEvent issueEvent) {
        final SimpleErrorCollection errorCollection = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (!errorCollection.hasAnyErrors()) {
            this.getCommunicationService().send(issueEvent);
            final String eventSource = (String) issueEvent.getParams().get("eventsource");
            if (eventSource != null && "workflow".equalsIgnoreCase(eventSource)) {
                this.getCommunicationService().sendTransition(issueEvent);
            }
            if (issueEvent.getEventTypeId() == EventType.ISSUE_WORKLOGGED_ID) {
                this.getCommunicationService().sendWorklog(issueEvent);
            }
        }
    }
    
    private CommunicationService getCommunicationService() {
        if (this.communicationService == null) {
            this.communicationService = (CommunicationService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)CommunicationService.class);
        }
        return this.communicationService;
    }
    
    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }
    
    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}
