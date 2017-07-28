// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.ArrayList;
import java.util.Arrays;
import net.java.ao.Query;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.intenso.jira.plugins.synchronizer.entity.RemoteWorkflowMapping;

public class RemoteWorkflowMappingServiceImpl extends GenericServiceImpl<RemoteWorkflowMapping> implements RemoteWorkflowMappingService
{
    public static final String COL_CONNECTION = "CONNECTION";
    public static final String COL_WORKFLOW_MAPPING_ID = "WORKFLOW_MAPPING_ID";
    public static final String COL_WORKFLOW_MAPPING_DISPLAY_NAME = "WORKFLOW_MAPPING_DISPLAY_NAME";
    public static final String COL_IN_TRANSITION_TEXT = "IN_TRANSITION_TEXT";
    public static final String COL_RESOLUTION = "RESOLUTION";
    public static final String COL_UPDATE_DATE = "UPDATE_DATE";
    
    public RemoteWorkflowMappingServiceImpl(final ActiveObjects dao) {
        super(dao, RemoteWorkflowMapping.class);
    }
    
    @Override
    public RemoteWorkflowMapping create(final Integer connection, final Integer workflowMappingId, final String workflowMappingDisplayName, final String inTransitionText, final Integer resolution) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONNECTION", connection);
        params.put("WORKFLOW_MAPPING_ID", workflowMappingId);
        params.put("WORKFLOW_MAPPING_DISPLAY_NAME", workflowMappingDisplayName);
        params.put("IN_TRANSITION_TEXT", inTransitionText);
        params.put("RESOLUTION", resolution);
        params.put("UPDATE_DATE", new Timestamp(new Date().getTime()));
        return (RemoteWorkflowMapping)this.getDao().create((Class)RemoteWorkflowMapping.class, (Map)params);
    }
    
    @Override
    public List<RemoteWorkflowMapping> findByConnection(final Integer connectionId) {
        final RemoteWorkflowMapping[] mappings = (RemoteWorkflowMapping[])this.getDao().find((Class)RemoteWorkflowMapping.class, Query.select().where("CONNECTION = ?", new Object[] { connectionId }));
        return (mappings != null) ? Arrays.asList(mappings) : new ArrayList<RemoteWorkflowMapping>();
    }
}
