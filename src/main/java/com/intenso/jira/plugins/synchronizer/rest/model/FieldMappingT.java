// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model;

import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMappingT
{
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<FieldMappingEntryT> entries;
    
    public FieldMappingT() {
        this.entries = new ArrayList<FieldMappingEntryT>();
    }
    
    public FieldMappingT(final Integer id, final String name) {
        this.entries = new ArrayList<FieldMappingEntryT>();
        this.id = id;
        this.name = name;
    }
    
    public FieldMappingT(final FieldMapping result) {
        this.entries = new ArrayList<FieldMappingEntryT>();
        this.id = result.getID();
        this.name = result.getName();
    }
    
    public FieldMappingT(final FieldMapping result, final List<FieldMappingEntry> entries) {
        this(result);
        for (final FieldMappingEntry fe : entries) {
            this.entries.add(new FieldMappingEntryTBuilder(fe).build());
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public List<FieldMappingEntryT> getEntries() {
        return this.entries;
    }
    
    public void setEntries(final List<FieldMappingEntryT> entries) {
        this.entries = entries;
    }
}
