// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import net.java.ao.Query;
import java.util.List;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.RemoteContract;

public class RemoteContractServiceImpl extends GenericServiceImpl<RemoteContract> implements RemoteContractService
{
    public static final String COL_CONNECTION = "CONNECTION";
    public static final String COL_CONTRACT = "CONTRACT";
    public static final String COL_UPDATE_DATE = "UPDATE_DATE";
    public static final String COL_CREATE_ENABLED = "CREATE_ENABLED";
    public static final String COL_UPDATE_ENABLED = "UPDATE_ENABLED";
    public static final String COL_DELETE_ENABLED = "DELETE_ENABLED";
    public static final String COL_COMMENTS_ENABLED = "COMMENTS_ENABLED";
    public static final String COL_ATTACHMENTS_ENABLED = "ATTACHMENTS_ENABLED";
    public static final String COL_WORKLOGS_ENABLED = "WORKLOGS_ENABLED";
    public static final String COL_HASH = "HASH";
    
    public RemoteContractServiceImpl(final ActiveObjects dao) {
        super(dao, RemoteContract.class);
    }
    
    @Override
    public List<RemoteContract> findByConnection(final Integer connectionId) {
        final RemoteContract[] contracts = (RemoteContract[])this.getDao().find((Class)RemoteContract.class, Query.select().where("CONNECTION = ?", new Object[] { connectionId }));
        return (contracts == null) ? new ArrayList<RemoteContract>() : Arrays.asList(contracts);
    }
    
    @Override
    public void clearConfiguration(final Integer connection) {
        final ActiveObjects getDao = this.getDao();
        final RemoteContract[] contracts = (RemoteContract[])getDao.find((Class)RemoteContract.class, Query.select().where("CONNECTION = ? ", new Object[] { connection }));
        if (contracts != null) {
            for (final RemoteContract rc : contracts) {
                this.delete(Integer.valueOf(rc.getID()));
            }
        }
    }
    
    public void deleteContractByContractAndConnection(final String remoteName, final Integer connection) {
        final RemoteContract[] contracts = (RemoteContract[])this.getDao().find((Class)RemoteContract.class, Query.select().where("CONTRACT = ? AND CONNECTION = ? ", new Object[] { remoteName, connection }));
        if (contracts != null) {
            for (final RemoteContract rc : contracts) {
                this.delete(Integer.valueOf(rc.getID()));
            }
        }
    }
    
    @Override
    public RemoteContract create(final Integer connection, final String contract, final Integer create, final Integer update, final Integer delete, final Integer comments, final Integer attachments, final Integer worklogs, final Integer hash) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONNECTION", connection);
        params.put("CONTRACT", contract);
        params.put("UPDATE_DATE", new Timestamp(new Date().getTime()));
        params.put("CREATE_ENABLED", create);
        params.put("UPDATE_ENABLED", update);
        params.put("DELETE_ENABLED", delete);
        params.put("COMMENTS_ENABLED", comments);
        params.put("ATTACHMENTS_ENABLED", attachments);
        params.put("WORKLOGS_ENABLED", worklogs);
        params.put("HASH", hash);
        final RemoteContract checkForDuplciates = this.findByName(contract, connection);
        if (checkForDuplciates != null) {
            this.deleteContractByContractAndConnection(contract, connection);
        }
        final RemoteContract rc = (RemoteContract)this.getDao().create((Class)RemoteContract.class, (Map)params);
        return rc;
    }
    
    @Override
    public RemoteContract findByName(final String remoteName, final Integer connection) {
        final RemoteContract[] remoteContracts = (RemoteContract[])this.getDao().find((Class)RemoteContract.class, Query.select().where("CONTRACT = ? AND CONNECTION = ?", new Object[] { remoteName, connection }));
        if (remoteContracts == null || remoteContracts.length == 0) {
            this.getLogger().warn(ExtendedLoggerMessageType.CFG, "Unable to find remote contract by name: " + remoteName + " for connection id: " + connection + " Probably the contract configuration is out of sync");
            return null;
        }
        return remoteContracts[0];
    }
}
