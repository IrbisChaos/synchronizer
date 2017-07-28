// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.customfield;

import org.ofbiz.core.entity.GenericEntityException;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemBuilder;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItem;
import com.atlassian.jira.config.managedconfiguration.ConfigurationItemAccessLevel;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.issuetype.IssueType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.CustomFieldManager;

public class InitCustomFieldType
{
    private CustomFieldManager customFieldManager;
    
    protected boolean existCustomField(final Class<? extends PreinstallCFType> clazz) {
        final List<CustomField> cfList = (List<CustomField>)this.getCustomFieldManager().getCustomFieldObjects();
        for (final CustomField cf : cfList) {
            if (cf.getCustomFieldType().getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }
    
    protected CustomField createCustomField(final String label, final String description, final String fieldKey) throws GenericEntityException {
        final List<IssueType> issueTypes = new ArrayList<IssueType>();
        issueTypes.add(null);
        final List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
        contexts.add(GlobalIssueContext.getInstance());
        final CustomField cField = this.getCustomFieldManager().createCustomField(label, description, this.getCustomFieldManager().getCustomFieldType(fieldKey), (CustomFieldSearcher)null, (List)contexts, (List)issueTypes);
        final ManagedConfigurationItemService managedConfigurationItemService = (ManagedConfigurationItemService)ComponentAccessor.getComponentOfType((Class)ManagedConfigurationItemService.class);
        final ManagedConfigurationItem mci = managedConfigurationItemService.getManagedCustomField(cField);
        if (mci != null) {
            final ManagedConfigurationItemBuilder mcib = mci.newBuilder();
            final ManagedConfigurationItem mcii = mcib.setManaged(true).setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.LOCKED).build();
            managedConfigurationItemService.updateManagedConfigurationItem(mcii);
        }
        return cField;
    }
    
    public CustomFieldManager getCustomFieldManager() {
        if (this.customFieldManager == null) {
            this.customFieldManager = (CustomFieldManager)ComponentAccessor.getComponent((Class)CustomFieldManager.class);
        }
        return this.customFieldManager;
    }
}
