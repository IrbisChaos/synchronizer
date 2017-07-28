// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import java.util.Map;

public class PluginSettingsUtil
{
    public static final String PLUGIN_SETTINGS_KEY = "com.intenso.jira.plugins-synchronizer";
    private static final String RAW_VALUE_KEY = "raw";
    private static final Map<String, Map<String, Object>> configCache;
    
    private static PluginSettings getSettings(final String pluginKey, final Long customFieldId, final Long fieldConfigId) {
        final PluginSettingsFactory settingsFactory = (PluginSettingsFactory)ComponentAccessor.getOSGiComponentInstanceOfType((Class)PluginSettingsFactory.class);
        try {
            final PluginSettings settings = settingsFactory.createSettingsForKey(pluginKey + customFieldId + "_" + fieldConfigId);
            return settings;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static PluginSettings getSettings(final Long customFieldId, final Long fieldConfigId) {
        return getSettings("com.intenso.jira.plugins-synchronizer", customFieldId, fieldConfigId);
    }
    
    private static void removeConfigFromSettings(final PluginSettings settings, final Long customFieldId, final Long fieldConfigId, final List<String> keys) {
        if (keys != null) {
            for (final String key : keys) {
                settings.remove(key);
            }
        }
    }
    
    private static Map<String, Object> getConfigFromSettings(final PluginSettings settings, final Long customFieldId, final Long fieldConfigId, final List<String> keys) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (settings != null) {
            if (keys != null) {
                for (final String key : keys) {
                    result.put(key, ObjectUtils.toString(settings.get(key)));
                }
            }
            else {
                final Object val = settings.get("raw");
                if (val instanceof Map) {
                    result = (Map<String, Object>)val;
                }
            }
        }
        return result;
    }
    
    private static void saveConfigToSettings(final PluginSettings settings, final Long customFieldId, final Long fieldConfigId, final Map<String, Object> config) {
        if (config != null && settings != null) {
            for (final Map.Entry<String, Object> entry : config.entrySet()) {
                settings.put((String)entry.getKey(), (Object)((entry.getValue() != null) ? entry.getValue().toString() : null));
            }
        }
    }
    
    private static void saveRawConfigToSettings(final PluginSettings settings, final Long customFieldId, final Long fieldConfigId, final Map<String, String> raw) {
        if (raw != null && settings != null) {
            settings.put("raw", (Object)raw);
        }
    }
    
    private static Map<String, Object> cacheConfig(final Long customFieldId, final Long fieldConfigId, final Map<String, Object> config) {
        return config;
    }
    
    private static String getCacheKey(final Long customFieldId, final Long fieldConfigId) {
        return customFieldId + "_" + fieldConfigId;
    }
    
    public static Map<String, Object> getConfig(final Long customFieldId, final Long fieldConfigId, final List<String> keys) {
        Map<String, Object> config = PluginSettingsUtil.configCache.get(getCacheKey(customFieldId, fieldConfigId));
        if (config == null) {
            config = getConfigFromSettings(getSettings(customFieldId, fieldConfigId), customFieldId, fieldConfigId, keys);
            cacheConfig(customFieldId, fieldConfigId, config);
        }
        return config;
    }
    
    public static void saveConfig(final Long customFieldId, final Long fieldConfigId, final Map<String, Object> config) {
        saveConfigToSettings(getSettings(customFieldId, fieldConfigId), customFieldId, fieldConfigId, config);
        cacheConfig(customFieldId, fieldConfigId, config);
    }
    
    public static void saveRawConfig(final Long customFieldId, final Long fieldConfigId, final Map<String, String> raw) {
        saveRawConfigToSettings(getSettings(customFieldId, fieldConfigId), customFieldId, fieldConfigId, raw);
    }
    
    public static void removeConfig(final Long customFieldId, final Long fieldConfigId, final List<String> keys) {
        removeConfigFromSettings(getSettings(customFieldId, fieldConfigId), customFieldId, fieldConfigId, keys);
        PluginSettingsUtil.configCache.remove(getCacheKey(customFieldId, fieldConfigId));
    }
    
    public static PluginSettings getSettings() {
        final PluginSettingsFactory settingsFactory = (PluginSettingsFactory)ComponentAccessor.getOSGiComponentInstanceOfType((Class)PluginSettingsFactory.class);
        final PluginSettings settings = settingsFactory.createSettingsForKey("com.intenso.jira.plugins-synchronizer");
        return settings;
    }
    
    public static String getString(final String key) {
        final PluginSettings settings = getSettings();
        return ObjectUtils.toString(settings.get(key));
    }
    
    public static Long getLong(final String key) {
        try {
            final PluginSettings settings = getSettings();
            final String longValue = ObjectUtils.toString(settings.get(key));
            if (StringUtils.isNotBlank(longValue)) {
                return Long.valueOf(longValue);
            }
        }
        catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
        return null;
    }
    
    public static void save(final String key, final String value) {
        try {
            final PluginSettings settings = getSettings();
            settings.put(key, (Object)((value != null) ? value.toString() : ""));
        }
        catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }
    
    public static void save(final String key, final Long value) {
        save(key, (value != null) ? value.toString() : "");
    }
    
    public static void remove(final String key) {
        getSettings().remove(key);
    }
    
    public static Map<String, Object> getConfig(final String pluginKey, final Long customFieldId, final Long fieldConfigId, final List<String> keys) {
        Map<String, Object> config = PluginSettingsUtil.configCache.get(getCacheKey(customFieldId, fieldConfigId));
        if (config == null) {
            config = getConfigFromSettings(getSettings(pluginKey, customFieldId, fieldConfigId), customFieldId, fieldConfigId, keys);
            cacheConfig(customFieldId, fieldConfigId, config);
        }
        return config;
    }
    
    public static Map<String, Object> getConfig(final String pluginKey, final Long customFieldId, final Long fieldConfigId) {
        return getConfig(pluginKey, customFieldId, fieldConfigId, null);
    }
    
    public static void saveConfig(final String pluginKey, final Long customFieldId, final Long fieldConfigId, final Map<String, Object> config) {
        saveConfigToSettings(getSettings(pluginKey, customFieldId, fieldConfigId), customFieldId, fieldConfigId, config);
        cacheConfig(customFieldId, fieldConfigId, config);
    }
    
    public static void saveRawConfig(final String pluginKey, final Long customFieldId, final Long fieldConfigId, final Map<String, String> raw) {
        saveRawConfigToSettings(getSettings(pluginKey, customFieldId, fieldConfigId), customFieldId, fieldConfigId, raw);
    }
    
    public static void removeConfig(final String pluginKey, final Long customFieldId, final Long fieldConfigId, final List<String> keys) {
        removeConfigFromSettings(getSettings(pluginKey, customFieldId, fieldConfigId), customFieldId, fieldConfigId, keys);
        PluginSettingsUtil.configCache.remove(getCacheKey(customFieldId, fieldConfigId));
    }
    
    public static void removeConfig(final String pluginKey, final Long customFieldId, final Long fieldConfigId) {
        getSettings(pluginKey, customFieldId, fieldConfigId).remove("raw");
        PluginSettingsUtil.configCache.remove(getCacheKey(customFieldId, fieldConfigId));
    }
    
    public static PluginSettings getSettings(final String pluginKey) {
        final PluginSettingsFactory settingsFactory = (PluginSettingsFactory)ComponentAccessor.getOSGiComponentInstanceOfType((Class)PluginSettingsFactory.class);
        final PluginSettings settings = settingsFactory.createSettingsForKey(pluginKey);
        return settings;
    }
    
    public static String getString(final String pluginKey, final String key) {
        final PluginSettings settings = getSettings(pluginKey);
        return ObjectUtils.toString(settings.get(key));
    }
    
    public static Long getLong(final String pluginKey, final String key) {
        try {
            final PluginSettings settings = getSettings(pluginKey);
            final String longValue = ObjectUtils.toString(settings.get(key));
            if (StringUtils.isNotBlank(longValue)) {
                return Long.valueOf(longValue);
            }
        }
        catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
        return null;
    }
    
    public static void save(final String pluginKey, final String key, final String value) {
        try {
            final PluginSettings settings = getSettings(pluginKey);
            settings.put(key, (Object)((value != null) ? value.toString() : ""));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void save(final String pluginKey, final String key, final Long value) {
        save(pluginKey, key, (value != null) ? value.toString() : "");
    }
    
    public static void remove(final String pluginKey, final String key) {
        getSettings(pluginKey).remove(key);
    }
    
    static {
        configCache = new ConcurrentHashMap<String, Map<String, Object>>();
    }
}
