// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action.dialog;

import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;
import java.util.Iterator;
import com.google.gson.Gson;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import java.util.ArrayList;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.service.ContractFieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.rest.model.ContractFieldMappingEntryT;
import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingService;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ContractFieldMappingAction extends JiraWebActionSupport
{
    private static final long serialVersionUID = 1L;
    private Integer contractId;
    private Integer connection;
    private FieldMappingService fieldMappingService;
    private FieldMappingEntryService fieldMappingEntryService;
    private ContractService contractService;
    private List<FieldMapping> mappingSchemes;
    private Integer template;
    private List<ContractFieldMappingEntryT> realEntries;
    private ContractFieldMappingEntryService contractFMEntryService;
    private Map<String, String> fields;
    private String jsonFieldTypes;
    private String contractMapping;
    private Boolean newOne;
    private Map<String, Map<String, String>> warnings;
    
    public ContractFieldMappingAction(final FieldMappingService fieldMappingService, final ContractFieldMappingEntryService contractFMEntryService, final FieldMappingEntryService fieldMappingEntryService, final ContractService contractService) {
        this.mappingSchemes = new ArrayList<FieldMapping>();
        this.realEntries = new ArrayList<ContractFieldMappingEntryT>();
        this.fieldMappingService = fieldMappingService;
        this.fieldMappingEntryService = fieldMappingEntryService;
        this.contractFMEntryService = contractFMEntryService;
        this.contractService = contractService;
    }
    
    public String doInput() {
        if (this.connection != null) {
            this.mappingSchemes = this.fieldMappingService.getAll();
        }
        return "input";
    }
    
    public String doEdit() throws Exception {
        if (this.connection != null && this.contractId != null) {
            final List<ContractFieldMappingEntry> cf = this.contractFMEntryService.findByContract(this.contractId);
            this.realEntries = new ArrayList<ContractFieldMappingEntryT>();
            for (final ContractFieldMappingEntry e : cf) {
                this.realEntries.add(new ContractFieldMappingEntryT(e));
            }
            final Contract contract = this.contractService.get(this.contractId);
            if (contract != null) {
                this.warnings = FieldMappingUtils.validateFieldMappingExtended(contract, cf);
            }
        }
        this.setNewOne(false);
        this.fields = FieldMappingUtils.prepareFields();
        final Gson gson = new Gson();
        this.jsonFieldTypes = gson.toJson(FieldMappingUtils.prepareFieldTypes(this.fields));
        return "edit";
    }
    
    public String doDefault() throws Exception {
        this.setNewOne(true);
        if (this.template != null) {
            final List<FieldMappingEntry> templateEnties = this.getFieldMappingEntryService().findByMapping(this.template);
            this.realEntries = new ArrayList<ContractFieldMappingEntryT>();
            for (final FieldMappingEntry e : templateEnties) {
                this.realEntries.add(new ContractFieldMappingEntryT(e));
            }
        }
        this.fields = FieldMappingUtils.prepareFields();
        final Gson gson = new Gson();
        this.jsonFieldTypes = gson.toJson(FieldMappingUtils.prepareFieldTypes(this.fields));
        return "edit";
    }
    
    protected String doExecute() throws Exception {
        final Gson gson = new Gson();
        final ContractFieldMappingEntryT[] mappings = gson.fromJson(this.contractMapping, ContractFieldMappingEntryT[].class);
        final List<ContractFieldMappingEntry> entries = this.getContractFMEntryService().findByContract(this.contractId);
        for (final ContractFieldMappingEntry cfmeExisting : entries) {
            boolean found = false;
            for (final ContractFieldMappingEntryT newOne : mappings) {
                if (newOne.getId() != null && newOne.getId().equals(cfmeExisting.getID())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                try {
                    this.getContractFMEntryService().delete(cfmeExisting);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (final ContractFieldMappingEntry current : entries) {
            for (final ContractFieldMappingEntryT newOne2 : mappings) {
                if (newOne2.getId() != null && newOne2.getId().equals(current.getID())) {
                    current.setLocalFieldName(newOne2.getLocalFieldName());
                    current.setRemoteFieldName(newOne2.getRemoteFieldName());
                    this.getContractFMEntryService().update(current);
                    break;
                }
            }
        }
        for (final ContractFieldMappingEntryT ent : mappings) {
            if (ent.getId() == null || ent.getId().equals(-1)) {
                this.getContractFMEntryService().create(this.contractId, ent.getLocalFieldId(), ent.getLocalFieldName(), ent.getRemoteFieldName());
            }
        }
        return this.returnCompleteWithInlineRedirect("ContractConfig!default.jspa?scontract=" + this.contractId + "&sconnection=" + this.connection);
    }
    
    public Integer getConnection() {
        return this.connection;
    }
    
    public void setConnection(final Integer connection) {
        this.connection = connection;
    }
    
    public FieldMappingService getFieldMappingService() {
        return this.fieldMappingService;
    }
    
    public void setFieldMappingService(final FieldMappingService fieldMappingService) {
        this.fieldMappingService = fieldMappingService;
    }
    
    public List<FieldMapping> getMappingSchemes() {
        return this.mappingSchemes;
    }
    
    public void setMappingSchemes(final List<FieldMapping> mappingSchemes) {
        this.mappingSchemes = mappingSchemes;
    }
    
    public Integer getContractId() {
        return this.contractId;
    }
    
    public void setContractId(final Integer contractId) {
        this.contractId = contractId;
    }
    
    public ContractFieldMappingEntryService getContractFMEntryService() {
        return this.contractFMEntryService;
    }
    
    public void setContractFMEntryService(final ContractFieldMappingEntryService contractFMEntryService) {
        this.contractFMEntryService = contractFMEntryService;
    }
    
    public Integer getTemplate() {
        return this.template;
    }
    
    public void setTemplate(final Integer template) {
        this.template = template;
    }
    
    public FieldMappingEntryService getFieldMappingEntryService() {
        return this.fieldMappingEntryService;
    }
    
    public void setFieldMappingEntryService(final FieldMappingEntryService fieldMappingEntryService) {
        this.fieldMappingEntryService = fieldMappingEntryService;
    }
    
    public Map<String, String> getFields() {
        return this.fields;
    }
    
    public void setFields(final Map<String, String> fields) {
        this.fields = fields;
    }
    
    public String getJsonFieldTypes() {
        return this.jsonFieldTypes;
    }
    
    public void setJsonFieldTypes(final String jsonFieldTypes) {
        this.jsonFieldTypes = jsonFieldTypes;
    }
    
    public String getContractMapping() {
        return this.contractMapping;
    }
    
    public void setContractMapping(final String contractMapping) {
        this.contractMapping = contractMapping;
    }
    
    public List<ContractFieldMappingEntryT> getRealEntries() {
        return this.realEntries;
    }
    
    public void setRealEntries(final List<ContractFieldMappingEntryT> realEntries) {
        this.realEntries = realEntries;
    }
    
    public Boolean getNewOne() {
        return this.newOne;
    }
    
    public void setNewOne(final Boolean newOne) {
        this.newOne = newOne;
    }
    
    public Map<String, Map<String, String>> getWarnings() {
        return this.warnings;
    }
    
    public void setWarnings(final Map<String, Map<String, String>> warnings) {
        this.warnings = warnings;
    }
}
