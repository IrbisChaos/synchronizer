// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.util.Map;

public class MapEntryImpl<K, V> implements Map.Entry<K, V>
{
    public K key;
    public V value;
    
    public MapEntryImpl(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public K getKey() {
        return this.key;
    }
    
    @Override
    public V getValue() {
        return this.value;
    }
    
    @Override
    public V setValue(final V value) {
        return this.value = value;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.key == null) ? 0 : this.key.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MapEntryImpl<K, V> other = (MapEntryImpl<K, V>)obj;
        if (this.key == null) {
            if (other.key != null) {
                return false;
            }
        }
        else if (!this.key.equals(other.key)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "MapEntryImpl [value=" + this.value + "]";
    }
}
