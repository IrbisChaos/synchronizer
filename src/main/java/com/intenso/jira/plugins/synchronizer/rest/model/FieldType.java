// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.lang.reflect.Method;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.impl.DateTimeCFType;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.customfields.impl.MultiUserCFType;
import com.atlassian.jira.issue.customfields.impl.MultiGroupCFType;
import com.atlassian.jira.issue.customfields.impl.ProjectCFType;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.impl.LabelsCFType;
import com.atlassian.jira.issue.customfields.impl.CascadingSelectCFType;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.Field;

public enum FieldType
{
    TYPE_LIMITED_TEXT, 
    TYPE_UNLIMITED_TEXT, 
    TYPE_DATE, 
    TYPE_DECIMAL, 
    TYPE_NATIVE, 
    TYPE_STATUS, 
    TYPE_ATTACHMENT, 
    TYPE_DATE_TIME, 
    TYPE_SELECT, 
    TYPE_LABELS, 
    TYPE_RESOLUTION, 
    TYPE_PROJECT, 
    TYPE_EPIC_LINK, 
    TYPE_MULTI_GROUP, 
    TYPE_SINGLE_GROUP, 
    TYPE_MULTI_USER, 
    TYPE_CASCADING_SELECT, 
    TYPE_MULTI_SELECT, 
    TYPE_SINGLE_USER;
    
    public static FieldType getCustomFieldTypeFromClass(final Field field) {
        final FieldManager fieldManager = (FieldManager)ComponentAccessor.getComponent((Class)FieldManager.class);
        if (!fieldManager.isCustomField(field)) {
            return FieldType.TYPE_NATIVE;
        }
        final CustomField customField = (CustomField)field;
        if (customField.getCustomFieldType() instanceof GenericTextCFType) {
            return FieldType.TYPE_LIMITED_TEXT;
        }
        if (customField.getCustomFieldType() instanceof MultiSelectCFType) {
            return FieldType.TYPE_MULTI_SELECT;
        }
        if (customField.getCustomFieldType() instanceof SelectCFType) {
            return FieldType.TYPE_SELECT;
        }
        if (customField.getCustomFieldType() instanceof CascadingSelectCFType) {
            return FieldType.TYPE_CASCADING_SELECT;
        }
        if (customField.getCustomFieldType() instanceof LabelsCFType) {
            return FieldType.TYPE_LABELS;
        }
        if (customField.getCustomFieldType() instanceof NumberCFType) {
            return FieldType.TYPE_DECIMAL;
        }
        if (customField.getCustomFieldType() instanceof ProjectCFType) {
            return FieldType.TYPE_PROJECT;
        }
        if (customField.getCustomFieldType() instanceof MultiGroupCFType) {
            if (((MultiGroupCFType)customField.getCustomFieldType()).isMultiple()) {
                return FieldType.TYPE_MULTI_GROUP;
            }
            return FieldType.TYPE_SINGLE_GROUP;
        }
        else {
            if (customField.getCustomFieldType() instanceof MultiUserCFType) {
                return FieldType.TYPE_MULTI_USER;
            }
            if (customField.getCustomFieldType() instanceof UserCFType) {
                return FieldType.TYPE_SINGLE_USER;
            }
            if (customField.getCustomFieldType().toString().contains("EpicLinkCFType")) {
                return FieldType.TYPE_EPIC_LINK;
            }
            final CustomFieldType t = customField.getCustomFieldType();
            final Class clazz = customField.getCustomFieldType().getClass();
            Object result = null;
            try {
                final Method[] list = clazz.getDeclaredMethods();
                boolean found = false;
                for (int i = 0; i < list.length; ++i) {
                    if (list[i].getName().equals("getDatabaseType")) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    final Method m = clazz.getDeclaredMethod("getDatabaseType", (Class[])new Class[0]);
                    if (m != null) {
                        m.setAccessible(true);
                        result = m.invoke(t, new Object[0]);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (result == null) {
                return FieldType.TYPE_NATIVE;
            }
            if (result.equals(PersistenceFieldType.TYPE_DATE)) {
                if (customField.getCustomFieldType() instanceof DateTimeCFType) {
                    return FieldType.TYPE_DATE_TIME;
                }
                return FieldType.TYPE_DATE;
            }
            else {
                if (result.equals(PersistenceFieldType.TYPE_DECIMAL)) {
                    return FieldType.TYPE_DECIMAL;
                }
                if (result.equals(PersistenceFieldType.TYPE_UNLIMITED_TEXT)) {
                    return FieldType.TYPE_UNLIMITED_TEXT;
                }
                if (result.equals(PersistenceFieldType.TYPE_LIMITED_TEXT)) {
                    return FieldType.TYPE_LIMITED_TEXT;
                }
                return FieldType.TYPE_NATIVE;
            }
        }
    }
}
