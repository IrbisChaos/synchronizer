// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.condition;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;

public class JiraSynchronizerConfigurationCondition extends AbstractWebCondition
{
    public boolean shouldDisplay(final ApplicationUser user, final JiraHelper jiraHelper) {
        if (user != null) {
            try {
                return ComponentAccessor.getPermissionManager().hasPermission(44, user);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
