// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.google.gson.Gson;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import com.atlassian.upm.api.license.PluginLicenseManager;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.ConfigurationJIRAClient;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingService;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.CustomFieldManager;

public class FieldMappingConfigAction extends GenericConfigAction
{
    private static final long serialVersionUID = -5834335936444033124L;
    private static final String FIELD_MAPPING_CONFIG_ACTION = "/secure/admin/FieldMappingConfigAction!default.jspa";
    private CustomFieldManager customFieldManager;
    private FieldManager fieldManager;
    private FieldMappingService fieldMappingService;
    private FieldMappingEntryService fieldMappingEntryService;
    private ConnectionService connectionService;
    private ConfigurationJIRAClient remoteJiraClient;
    private List<Connection> connections;
    private Map<String, String> fields;
    private String jsonFieldTypes;
    private Integer id;
    private Integer connectionId;
    private String mappingName;
    private String localFieldId;
    private String localFieldName;
    private String remoteFieldName;
    
    public FieldMappingConfigAction(final FieldMappingService fieldMappingService, final ConnectionService connectionService, final ConfigurationJIRAClient remoteJiraClient, final CustomFieldManager customFieldManager, final FieldManager fieldManager, final FieldMappingEntryService fieldMappingEntryService, final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.fieldMappingService = fieldMappingService;
        this.connectionService = connectionService;
        this.remoteJiraClient = remoteJiraClient;
        this.customFieldManager = customFieldManager;
        this.fieldManager = fieldManager;
        this.fieldMappingEntryService = fieldMappingEntryService;
    }
    
    @Override
    public String doDefault() throws Exception {
        this.connections = this.connectionService.getAll();
        this.fields = FieldMappingUtils.prepareFields();
        final Gson gson = new Gson();
        this.jsonFieldTypes = gson.toJson(FieldMappingUtils.prepareFieldTypes(this.fields));
        return super.doDefault();
    }
    
    public void doValidation() {
        if (this.connectionId == null) {
            this.getErrors().put("connectionId", "Connection is required");
        }
        if (this.mappingName == null || this.mappingName.isEmpty()) {
            this.getErrors().put("mappingName", "Mapping name is required");
        }
        if (this.localFieldId == null || this.localFieldId.isEmpty()) {
            this.getErrors().put("localFieldId", "Local field is required");
        }
        if (this.localFieldName == null || this.localFieldName.isEmpty()) {
            this.getErrors().put("localFieldName", "Local field name is required");
        }
        if (this.getErrors().size() > 0) {
            try {
                this.doDefault();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @RequiresXsrfCheck
    public String doExecute() throws Exception {
        Integer mappingId = null;
        final String integer = "\\d+";
        if (this.getMappingName().matches(integer)) {
            mappingId = Integer.parseInt(this.getMappingName());
        }
        else {
            final FieldMapping mapping = this.fieldMappingService.save(this.getMappingName());
            mappingId = mapping.getID();
        }
        if (mappingId == null) {
            this.addErrorMessage("An error occurs. Problem when saving message.");
            return "none";
        }
        this.fieldMappingEntryService.save(mappingId, this.localFieldId, this.localFieldName, this.remoteFieldName);
        this.addMessageToResponse("Mapping saved", JiraWebActionSupport.MessageType.SUCCESS.name(), true, (String)null);
        return this.getRedirect("/secure/admin/FieldMappingConfig!default.jspa");
    }
    
    public String doRemove() {
        if (this.id != null) {
            this.getFieldMappingService().delete(this.id);
        }
        return this.getRedirect("/secure/admin/FieldMappingConfigAction!default.jspa");
    }
    
    public ConnectionService getConnectionService() {
        return this.connectionService;
    }
    
    public void setConnectionService(final ConnectionService connectionService) {
        this.connectionService = connectionService;
    }
    
    public ConfigurationJIRAClient getRemoteJiraClient() {
        return this.remoteJiraClient;
    }
    
    public void setRemoteJiraClient(final ConfigurationJIRAClient remoteJiraClient) {
        this.remoteJiraClient = remoteJiraClient;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public FieldMappingService getFieldMappingService() {
        return this.fieldMappingService;
    }
    
    public void setFieldMappingService(final FieldMappingService fieldMappingService) {
        this.fieldMappingService = fieldMappingService;
    }
    
    public List<Connection> getConnections() {
        return this.connections;
    }
    
    public void setConnections(final List<Connection> connections) {
        this.connections = connections;
    }
    
    public String getMappingName() {
        return this.mappingName;
    }
    
    public void setMappingName(final String mappingName) {
        this.mappingName = mappingName;
    }
    
    public String getLocalFieldId() {
        return this.localFieldId;
    }
    
    public void setLocalFieldId(final String localFieldId) {
        this.localFieldId = localFieldId;
    }
    
    public String getRemoteFieldName() {
        return this.remoteFieldName;
    }
    
    public void setRemoteFieldName(final String remoteFieldName) {
        this.remoteFieldName = remoteFieldName;
    }
    
    public Map<String, String> getFields() {
        return this.fields;
    }
    
    public void setFields(final Map<String, String> fields) {
        this.fields = fields;
    }
    
    public CustomFieldManager getCustomFieldManager() {
        return this.customFieldManager;
    }
    
    public void setCustomFieldManager(final CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
    
    public FieldManager getFieldManager() {
        return this.fieldManager;
    }
    
    public void setFieldManager(final FieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }
    
    public Integer getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final Integer connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getLocalFieldName() {
        return this.localFieldName;
    }
    
    public void setLocalFieldName(final String localFieldName) {
        this.localFieldName = localFieldName;
    }
    
    public FieldMappingEntryService getFieldMappingEntryService() {
        return this.fieldMappingEntryService;
    }
    
    public void setFieldMappingEntryService(final FieldMappingEntryService fieldMappingEntryService) {
        this.fieldMappingEntryService = fieldMappingEntryService;
    }
    
    public String getJsonFieldTypes() {
        return this.jsonFieldTypes;
    }
    
    public void setJsonFieldTypes(final String jsonFieldTypes) {
        this.jsonFieldTypes = jsonFieldTypes;
    }
}
