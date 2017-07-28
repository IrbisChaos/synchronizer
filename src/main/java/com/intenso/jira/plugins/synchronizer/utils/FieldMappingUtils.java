// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import org.ofbiz.core.util.UtilMisc;
import com.intenso.jira.plugins.synchronizer.entity.RemoteFieldMapping;
import com.intenso.jira.plugins.synchronizer.service.RemoteFieldMappingService;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.Field;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import java.util.Set;
import com.atlassian.jira.issue.fields.FieldException;
import com.atlassian.jira.issue.fields.NavigableField;
import java.util.TreeMap;
import java.util.Map;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import java.util.Iterator;
import java.util.Collection;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import java.util.List;

public class FieldMappingUtils
{
    public static final List<String> EXCLUDED_JIRA_FIELDS;
    
    public static CustomField getCustomFieldByNameAndContext(final String name, final Issue issue) {
        final CustomFieldManager cfm = (CustomFieldManager)ComponentAccessor.getComponent((Class)CustomFieldManager.class);
        final Collection<CustomField> cfList = (Collection<CustomField>)cfm.getCustomFieldObjectsByName(name);
        CustomField result = null;
        if (cfList == null) {
            return result;
        }
        for (final CustomField cf : cfList) {
            final FieldConfig config = cf.getRelevantConfig(issue);
            if (config != null) {
                result = cf;
                break;
            }
        }
        return result;
    }
    
    public static Map<String, String> prepareFields() {
        final Map<String, String> maps = new TreeMap<String, String>();
        final List<CustomField> customfields = (List<CustomField>)ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
        for (final CustomField cf : customfields) {
            maps.put(cf.getId(), cf.getName());
        }
        try {
            final Set<NavigableField> navigableFields = (Set<NavigableField>)ComponentAccessor.getFieldManager().getAllAvailableNavigableFields();
            for (final NavigableField nf : navigableFields) {
                if (!FieldMappingUtils.EXCLUDED_JIRA_FIELDS.contains(nf.getId())) {
                    maps.put(nf.getId(), nf.getName());
                }
            }
        }
        catch (FieldException e) {
            e.printStackTrace();
        }
        return maps;
    }
    
    public static Map<String, FieldType> prepareFieldTypes(final Map<String, String> fields) {
        final Map<String, FieldType> fieldTypes = new HashMap<String, FieldType>();
        for (final String id : fields.keySet()) {
            NavigableField f = (NavigableField)ComponentAccessor.getFieldManager().getField(id);
            if (f != null && !ComponentAccessor.getFieldManager().isCustomField((Field)f)) {
                fieldTypes.put(f.getId(), FieldType.TYPE_NATIVE);
            }
            else {
                if (f == null) {
                    continue;
                }
                f = (NavigableField)ComponentAccessor.getCustomFieldManager().getCustomFieldObject(id);
                final CustomField cf = (CustomField)f;
                fieldTypes.put(id, (f != null) ? FieldType.getCustomFieldTypeFromClass((Field)cf) : null);
            }
        }
        return fieldTypes;
    }
    
    public static boolean checkIfFieldOrCustomFieldExists(final String localFieldId) {
        final FieldManager fieldManager = ComponentAccessor.getFieldManager();
        final CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        Field field = fieldManager.getField(localFieldId);
        if (field == null) {
            field = (Field)customFieldManager.getCustomFieldObject(localFieldId);
        }
        return field != null;
    }
    
    public static Map<String, Map<String, String>> validateFieldMappingExtended(final Contract contract, final List<ContractFieldMappingEntry> mapping) {
        final Map<String, Map<String, String>> validation = new HashMap<String, Map<String, String>>();
        final RemoteFieldMappingService remoteFieldMappingService = (RemoteFieldMappingService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)RemoteFieldMappingService.class);
        final Map<String, String> fields = prepareFields();
        final Map<String, FieldType> types = prepareFieldTypes(fields);
        for (final ContractFieldMappingEntry entry : mapping) {
            final Map<String, String> tmp = new HashMap<String, String>();
            if (!checkIfFieldOrCustomFieldExists(entry.getLocalFieldId())) {
                tmp.put("local-field-id-error", "Local field no longer exists.");
            }
            if (entry.getRemoteFieldName() != null && !entry.getRemoteFieldName().trim().isEmpty()) {
                final RemoteFieldMapping fm;
                synchronized (RemoteFieldMapping.class) {
                    fm = remoteFieldMappingService.findByContractAndConnectionAndName(contract.getRemoteContextName(), contract.getConnectionId(), entry.getRemoteFieldName());
                }
                if (fm == null) {
                    tmp.put("remote-field-name-error", "Field no longer available in remote mapping.");
                }
                else if (fm == null || types.get(entry.getLocalFieldId()) == null || !fm.getFieldType().equals(types.get(entry.getLocalFieldId()).ordinal())) {}
            }
            validation.put(new Integer(entry.getID()).toString(), tmp);
        }
        return validation;
    }
    
    public static String validateFieldMapping(final Contract contract, final List<ContractFieldMappingEntry> mapping) {
        final RemoteFieldMappingService remoteFieldMappingService = (RemoteFieldMappingService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)RemoteFieldMappingService.class);
        boolean localChanged = false;
        boolean remoteChanged = false;
        for (final ContractFieldMappingEntry entry : mapping) {
            if (!checkIfFieldOrCustomFieldExists(entry.getLocalFieldId())) {
                localChanged = true;
            }
            if (entry.getRemoteFieldName() != null && !entry.getRemoteFieldName().trim().isEmpty()) {
                final RemoteFieldMapping fm;
                synchronized (RemoteFieldMapping.class) {
                    fm = remoteFieldMappingService.findByContractAndConnectionAndName(contract.getRemoteContextName(), contract.getConnectionId(), entry.getRemoteFieldName());
                }
                if (fm != null) {
                    continue;
                }
                remoteChanged = true;
            }
        }
        String validationError = null;
        if (localChanged) {
            validationError = "One of the local field not exists any more in configuration";
        }
        if (remoteChanged) {
            validationError = ((validationError != null) ? (validationError + ", ") : "") + "One of the remote field not exists any more in configuration.";
        }
        return validationError;
    }
    
    static {
        EXCLUDED_JIRA_FIELDS = UtilMisc.toList((Object)"issuetype", (Object)"project", (Object)"attachment", (Object)"comment", (Object)"resolution");
    }
}
