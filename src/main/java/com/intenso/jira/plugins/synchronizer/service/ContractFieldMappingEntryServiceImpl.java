// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import net.java.ao.Entity;
import java.util.ArrayList;
import java.util.Iterator;
import net.java.ao.RawEntity;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;

public class ContractFieldMappingEntryServiceImpl extends SyncAwareServiceImpl<ContractFieldMappingEntry> implements ContractFieldMappingEntryService
{
    public static final String COL_CONTRACT_ID = "CONTRACT_ID";
    public static final String COL_LOCAL_FIELD_ID = "LOCAL_FIELD_ID";
    public static final String COL_LOCAL_FIELD_NAME = "LOCAL_FIELD_NAME";
    public static final String COL_REMOTE_FIELD_NAME = "REMOTE_FIELD_NAME";
    
    public ContractFieldMappingEntryServiceImpl(final ActiveObjects dao) {
        super(dao, ContractFieldMappingEntry.class);
    }
    
    private Integer getConnectionFromContract(final Integer contract) {
        final Contract contractObject = (Contract)this.getDao().get((Class)Contract.class, (Object)contract);
        if (contractObject == null) {
            return null;
        }
        return contractObject.getConnectionId();
    }
    
    @Override
    public ContractFieldMappingEntry create(final Integer contract, final String localFieldId, final String localFieldName, final String remoteFieldName) {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("CONTRACT_ID", contract);
        parameters.put("LOCAL_FIELD_ID", localFieldId);
        parameters.put("LOCAL_FIELD_NAME", localFieldName);
        parameters.put("REMOTE_FIELD_NAME", remoteFieldName);
        final ContractFieldMappingEntry result = (ContractFieldMappingEntry)this.getDao().create((Class)ContractFieldMappingEntry.class, (Map)parameters);
        this.makeConnectionOutOfSync(this.getConnectionFromContract(contract));
        return result;
    }
    
    @Override
    public List<ContractFieldMappingEntry> findByContract(final Integer contract) {
        final ContractFieldMappingEntry[] entries = (ContractFieldMappingEntry[])this.getDao().find((Class)ContractFieldMappingEntry.class, Query.select().where("CONTRACT_ID = ?", new Object[] { contract }));
        return Arrays.asList(entries);
    }
    
    @Override
    public void delete(final ContractFieldMappingEntry entity) {
        super.delete(entity);
        this.makeConnectionOutOfSync(this.getConnectionFromContract(entity.getContractId()));
    }
    
    @Override
    public void delete(final Integer id) {
        final ContractFieldMappingEntry en = (ContractFieldMappingEntry)this.getDao().get((Class)ContractFieldMappingEntry.class, (Object)id);
        super.delete(id);
        this.makeConnectionOutOfSync(this.getConnectionFromContract(en.getContractId()));
    }
    
    @Override
    public void deleteByContract(final Integer contract) {
        final List<ContractFieldMappingEntry> entries = this.findByContract(contract);
        for (final ContractFieldMappingEntry ce : entries) {
            this.getDao().delete(new RawEntity[] { ce });
        }
        this.makeConnectionOutOfSync(this.getConnectionFromContract(contract));
    }
    
    @Override
    public Integer countByContract(final Integer contractId) {
        return this.getDao().count((Class)ContractFieldMappingEntry.class, Query.select().where("CONTRACT_ID = ?", new Object[] { contractId }));
    }
    
    @Override
    public List<ContractFieldMappingEntry> findByConnection(final Integer connection) {
        final ContractFieldMappingEntry[] entries = (ContractFieldMappingEntry[])this.getDao().find((Class)ContractFieldMappingEntry.class, Query.select().alias((Class)ContractFieldMappingEntry.class, "mapping").alias((Class)Contract.class, "contract").join((Class)Contract.class, "contract.ID = mapping.CONTRACT_ID").where("contract.CONNECTION_ID = ?", new Object[] { connection }));
        return (entries != null) ? Arrays.asList(entries) : new ArrayList<ContractFieldMappingEntry>();
    }
}
