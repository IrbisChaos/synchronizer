// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.util.ArrayList;
import java.util.List;
import org.ofbiz.core.entity.GenericEntityException;
import java.util.HashMap;
import org.ofbiz.core.entity.GenericValue;
import java.util.Map;

public class ChangeHistoryHelper
{
    private static Map<String, String> MAPPING_CHANGELOG_FIELDS;
    public static final String ATTACHMENT_CHANGELOG_FIELDNAME = "Attachment";
    public static final String ATTACHMENT_CHANGELOG_FIELDTYPE = "jira";
    
    public static String fieldName2FieldId(final String name) {
        if (ChangeHistoryHelper.MAPPING_CHANGELOG_FIELDS.containsKey(name)) {
            return ChangeHistoryHelper.MAPPING_CHANGELOG_FIELDS.get(name);
        }
        return name;
    }
    
    public static Integer[] getChangeHistoryStatusChange(final GenericValue changelog) {
        if (changelog == null) {
            return null;
        }
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("group", changelog.get("id"));
            params.put("fieldtype", "jira");
            params.put("field", "status");
            final List<GenericValue> statusChangelog = (List<GenericValue>)changelog.internalDelegator.findByAnd("ChangeItem", (Map)params);
            if (statusChangelog != null && statusChangelog.size() > 0) {
                final GenericValue entry = statusChangelog.get(0);
                final String oldvalue = entry.getString("oldvalue");
                final String newvalue = entry.getString("newvalue");
                return new Integer[] { Integer.parseInt(oldvalue), Integer.parseInt(newvalue) };
            }
        }
        catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<GenericValue> getChangeHistory(final GenericValue changelog) {
        if (changelog == null) {
            return new ArrayList<GenericValue>();
        }
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("group", changelog.get("id"));
            return (List<GenericValue>)changelog.internalDelegator.findByAnd("ChangeItem", (Map)params);
        }
        catch (GenericEntityException e) {
            e.printStackTrace();
            return new ArrayList<GenericValue>();
        }
    }
    
    static {
        (ChangeHistoryHelper.MAPPING_CHANGELOG_FIELDS = new HashMap<String, String>()).put("Component", "components");
        ChangeHistoryHelper.MAPPING_CHANGELOG_FIELDS.put("Version", "versions");
        ChangeHistoryHelper.MAPPING_CHANGELOG_FIELDS.put("Fix Version", "fixVersions");
    }
}
