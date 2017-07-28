// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import org.codehaus.jackson.map.ObjectMapper;
import com.intenso.jira.plugins.synchronizer.rest.model.AlertsT;
import com.intenso.jira.plugins.synchronizer.utils.PluginSettingsUtil;

public class AlertsServiceImpl implements AlertsService
{
    private static final String ALERTS_JSON_SETTINGS = "alerts-json";
    
    @Override
    public void saveConfigurationJSONString(final String json) {
        PluginSettingsUtil.save("alerts-json", json);
    }
    
    @Override
    public AlertsT getConfiguration() {
        final String json = this.getConfigurationJSONString();
        if (json != null) {
            try {
                final ObjectMapper objectMapper = new ObjectMapper();
                return (AlertsT)objectMapper.readValue(json, (Class)AlertsT.class);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new AlertsT();
    }
    
    private String getConfigurationJSONString() {
        final String json = PluginSettingsUtil.getString("alerts-json");
        if (json != null && (json.equals("null") || json.isEmpty())) {
            return null;
        }
        return json;
    }
}
