// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.customfield;

import com.atlassian.event.api.EventListener;
import org.ofbiz.core.entity.GenericEntityException;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class SynchronizedIssueCFTypeInit extends InitCustomFieldType implements InitializingBean, DisposableBean
{
    private EventPublisher publisher;
    private ExtendedLogger logger;
    
    public SynchronizedIssueCFTypeInit(final EventPublisher publisher) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.setPublisher(publisher);
    }
    
    public void afterPropertiesSet() throws Exception {
        this.publisher.register((Object)this);
    }
    
    public void destroy() throws Exception {
        this.publisher.unregister((Object)this);
    }
    
    @EventListener
    public void onJIRAEvent(final PluginEnabledEvent event) throws GenericEntityException {
        if (event.getPlugin().getKey().equals("com.intenso.jira.plugins.synchronizer")) {
            try {
                if ("com.intenso.jira.plugins.synchronizer".equals(event.getPlugin().getKey())) {
                    if (!this.existCustomField(SynchronizedIssueCFType.class)) {
                        this.createCustomField("JIRA Synchronized Issues", "Shows remote issues synchronized with this issue", "com.intenso.jira.plugins.synchronizer:synchronized-issues");
                        this.logger.debug(ExtendedLoggerMessageType.CFG, "syn012");
                    }
                    else {
                        this.logger.debug(ExtendedLoggerMessageType.CFG, "syn013");
                    }
                }
            }
            catch (Exception e) {
                this.logger.error(ExtendedLoggerMessageType.CFG, "syn014");
            }
        }
    }
    
    public EventPublisher getPublisher() {
        return this.publisher;
    }
    
    public void setPublisher(final EventPublisher publisher) {
        this.publisher = publisher;
    }
}
