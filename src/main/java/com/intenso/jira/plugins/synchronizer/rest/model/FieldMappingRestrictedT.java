// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import com.intenso.jira.plugins.synchronizer.entity.RemoteFieldMapping;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMappingRestrictedT
{
    @JsonProperty
    private String name;
    @JsonProperty
    private FieldType type;
    
    public FieldMappingRestrictedT(final String name, final FieldType type) {
        this.name = name;
        this.type = type;
    }
    
    public FieldMappingRestrictedT(final RemoteFieldMapping rc) {
        this.name = rc.getFieldName();
        if (rc.getFieldType() != null && rc.getFieldType() < FieldType.values().length) {
            this.type = FieldType.values()[rc.getFieldType()];
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public FieldType getType() {
        return this.type;
    }
    
    public void setType(final FieldType type) {
        this.type = type;
    }
}
