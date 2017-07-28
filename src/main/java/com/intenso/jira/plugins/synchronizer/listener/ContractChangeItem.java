// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.listener;

import java.util.Map;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldType;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import org.boon.json.annotations.JsonIgnore;
import com.atlassian.jira.issue.fields.Field;

public class ContractChangeItem
{
    @JsonIgnore
    private Field field;
    @JsonIgnore
    private CustomFieldType customFieldType;
    private String typeInString;
    private FieldType type;
    private String jiraFieldId;
    private String fieldName;
    private String text;
    private String date;
    private Double number;
    private Object value;
    private Object[] values;
    private Map<String, String> valuesMap;
    
    public ContractChangeItem() {
    }
    
    public ContractChangeItem(final Field field, final String fieldName, final String text) {
        this.field = field;
        this.fieldName = fieldName;
        this.text = text;
    }
    
    public ContractChangeItem(final Field field, final String fieldName, final String text, final FieldType type) {
        this.field = field;
        this.fieldName = fieldName;
        this.text = text;
        this.type = type;
    }
    
    public Object getValueGeneral() {
        if (this.text != null) {
            return this.text;
        }
        if (this.date != null) {
            return this.date;
        }
        if (this.number != null) {
            return this.number;
        }
        if (this.values != null) {
            return this.values;
        }
        if (this.valuesMap != null) {
            return this.valuesMap;
        }
        return this.value;
    }
    
    public boolean isCustomField() {
        return this.fieldName.startsWith("customfield_");
    }
    
    public CustomFieldType getCustomFieldType() {
        return this.customFieldType;
    }
    
    public Field getField() {
        return this.field;
    }
    
    public void setField(final Field field) {
        this.field = field;
    }
    
    public void setCustomFieldType(final CustomFieldType field) {
        this.customFieldType = field;
    }
    
    public FieldType getType() {
        return this.type;
    }
    
    public void setType(final FieldType type) {
        this.type = type;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public String getDate() {
        return this.date;
    }
    
    public void setDate(final String date) {
        this.date = date;
    }
    
    public Double getNumber() {
        return this.number;
    }
    
    public void setNumber(final Double number) {
        this.number = number;
    }
    
    public Object[] getValues() {
        return this.values;
    }
    
    public Map<String, String> getValuesMap() {
        return this.valuesMap;
    }
    
    public void setValues(final Object[] values) {
        this.values = values;
    }
    
    public void setValuesMap(final Map<String, String> values) {
        this.valuesMap = values;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    public String getTypeInString() {
        return this.typeInString;
    }
    
    public void setTypeInString(final String typeInString) {
        this.typeInString = typeInString;
    }
    
    public String getJiraFieldId() {
        return this.jiraFieldId;
    }
    
    public void setJiraFieldId(final String jiraFieldId) {
        this.jiraFieldId = jiraFieldId;
    }
}
