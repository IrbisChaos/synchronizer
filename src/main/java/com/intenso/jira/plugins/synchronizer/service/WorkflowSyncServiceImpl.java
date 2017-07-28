// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import org.slf4j.LoggerFactory;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowIncomingTransitionsT;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowMappingT;
import com.intenso.jira.plugins.synchronizer.rest.model.RemoteWorkflowConfigurationT;
import com.intenso.jira.plugins.synchronizer.utils.PluginSettingsUtil;
import org.codehaus.jackson.map.ObjectMapper;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowIncomingTransitionsT;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowOutgoingTransitionsT;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowMappingT;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.action.WorkflowMapping;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.rest.model.WorkflowConfigurationT;
import org.slf4j.Logger;

public class WorkflowSyncServiceImpl implements WorkflowSyncService
{
    private static final Logger log;
    private static final String WORKFLOW_MAPPING_JSON_SETTINGS = "workflow-mapping-json";
    private static WorkflowConfigurationT cache;
    
    private synchronized WorkflowConfigurationT getCache() {
        if (WorkflowSyncServiceImpl.cache == null) {
            WorkflowSyncServiceImpl.cache = this.readWorkflowConfigurationObject();
        }
        return WorkflowSyncServiceImpl.cache;
    }
    
    @Override
    public List<WorkflowMapping> getWorkflowMappingList() {
        final List<WorkflowMapping> result = new ArrayList<WorkflowMapping>();
        final List<WorkflowMappingT> mappings = this.getCache().getConfiguration();
        if (mappings != null) {
            for (final WorkflowMappingT mapping : mappings) {
                result.add(new WorkflowMapping("" + mapping.getWorkflowMappingId(), mapping.getWorkflowMappingDisplayName(), mapping.getWorkflowName()));
            }
        }
        return result;
    }
    
    @Override
    public boolean isValidMapping(final Integer mappingId) {
        final WorkflowMappingT m = this.getWorkflowMapping(mappingId);
        return m != null;
    }
    
    @Override
    public String getOutgoingTransitionText(final Integer workflowMappingId, final Integer prevStatus, final Integer currStatus) {
        final WorkflowMappingT mapping = this.getWorkflowMapping(workflowMappingId);
        if (mapping != null && mapping.getOutgoingTransitions() != null && prevStatus != null && currStatus != null) {
            for (final WorkflowOutgoingTransitionsT t : mapping.getOutgoingTransitions()) {
                if (currStatus.equals(t.getCurrStatusId()) && prevStatus.equals(t.getPrevStatusId())) {
                    return t.getOutTransitionText();
                }
            }
            for (final WorkflowOutgoingTransitionsT t : mapping.getOutgoingTransitions()) {
                if (currStatus.equals(t.getCurrStatusId()) && t.getPrevStatusId() == -1) {
                    return t.getOutTransitionText();
                }
            }
        }
        final String msg = "workflowMappingId: " + workflowMappingId + ", prevStatus: " + prevStatus + ", currStatus: " + currStatus;
        ((QueueLogService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueLogService.class)).createQueueLog(500, msg);
        return null;
    }
    
    @Override
    public Boolean isRestApiTransition(final Integer workflowMappingId, final String transitionText) {
        final WorkflowMappingT mapping = this.getWorkflowMapping(workflowMappingId);
        if (mapping != null && mapping.getIncomingTransitions() != null && transitionText != null && !transitionText.isEmpty()) {
            for (final WorkflowIncomingTransitionsT t : mapping.getIncomingTransitions()) {
                if (transitionText.equals(t.getInTransitionText())) {
                    return t.getUseRestApi() != null && t.getUseRestApi();
                }
            }
        }
        return false;
    }
    
    @Override
    public Integer getTransitionId(final Integer workflowMappingId, final String transitionText) {
        final WorkflowMappingT mapping = this.getWorkflowMapping(workflowMappingId);
        if (mapping != null && mapping.getIncomingTransitions() != null && transitionText != null && !transitionText.isEmpty()) {
            for (final WorkflowIncomingTransitionsT t : mapping.getIncomingTransitions()) {
                if (transitionText.equals(t.getInTransitionText())) {
                    return t.getWorkflowTransitionId();
                }
            }
        }
        return null;
    }
    
    @Override
    public WorkflowMappingT getWorkflowMapping(final Integer mappingId) {
        if (mappingId != null) {
            final List<WorkflowMappingT> mappings = this.getCache().getConfiguration();
            if (mappings != null) {
                for (final WorkflowMappingT mapping : mappings) {
                    if (mappingId.equals(mapping.getWorkflowMappingId())) {
                        return mapping;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public String getMappingDisplayName(final Integer mappingId) {
        final WorkflowMappingT m = this.getWorkflowMapping(mappingId);
        if (m != null) {
            return m.getWorkflowMappingDisplayName();
        }
        return null;
    }
    
    private WorkflowConfigurationT readWorkflowConfigurationObject() {
        final String jsonString = this.getConfigurationJSONString();
        if (jsonString != null) {
            try {
                final ObjectMapper objectMapper = new ObjectMapper();
                return (WorkflowConfigurationT)objectMapper.readValue(jsonString, (Class)WorkflowConfigurationT.class);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new WorkflowConfigurationT();
    }
    
    @Override
    public String getConfigurationJSONString() {
        final String json = PluginSettingsUtil.getString("workflow-mapping-json");
        if (json != null && (json.equals("null") || json.isEmpty())) {
            return null;
        }
        return json;
    }
    
    @Override
    public String getRemoteConfigurationJSONString() {
        final RemoteWorkflowConfigurationT rwc = new RemoteWorkflowConfigurationT();
        rwc.setConfiguration(new ArrayList<RemoteWorkflowMappingT>());
        final WorkflowConfigurationT wc = this.readWorkflowConfigurationObject();
        if (wc.getConfiguration() != null) {
            for (final WorkflowMappingT wm : wc.getConfiguration()) {
                final RemoteWorkflowMappingT rwm = new RemoteWorkflowMappingT();
                rwm.setWorkflowMappingId(wm.getWorkflowMappingId());
                rwm.setWorkflowMappingDisplayName(wm.getWorkflowMappingDisplayName());
                final List<RemoteWorkflowIncomingTransitionsT> rwits = new ArrayList<RemoteWorkflowIncomingTransitionsT>();
                final List<WorkflowOutgoingTransitionsT> wots = wm.getOutgoingTransitions();
                if (wots != null) {
                    for (final WorkflowOutgoingTransitionsT wot : wots) {
                        final RemoteWorkflowIncomingTransitionsT rwit = new RemoteWorkflowIncomingTransitionsT();
                        rwit.setInTransitionText(wot.getOutTransitionText());
                        rwit.setResolution(wot.getResolution());
                        rwits.add(rwit);
                    }
                }
                rwm.setIncomingTransitions(rwits);
                rwc.getConfiguration().add(rwm);
            }
        }
        final ObjectMapper objectMapper = new ObjectMapper();
        String result = "";
        try {
            result = objectMapper.writeValueAsString((Object)rwc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public synchronized void saveConfigurationJSONString(final String json) {
        final WorkflowConfigurationT wc = this.readWorkflowConfigurationObject();
        if (wc.getConfiguration() == null) {
            wc.setConfiguration(new ArrayList<WorkflowMappingT>());
        }
        WorkflowConfigurationT newWc = null;
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            newWc = (WorkflowConfigurationT)objectMapper.readValue(json, (Class)WorkflowConfigurationT.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (newWc != null) {
            for (final WorkflowMappingT newWm : newWc.getConfiguration()) {
                final Iterator<WorkflowMappingT> i = wc.getConfiguration().iterator();
                while (i.hasNext()) {
                    final WorkflowMappingT wm = i.next();
                    if (wm.getWorkflowMappingId() == null) {
                        i.remove();
                    }
                    else {
                        if (wm.getWorkflowMappingId().equals(newWm.getWorkflowMappingId())) {
                            i.remove();
                            break;
                        }
                        continue;
                    }
                }
                wc.getConfiguration().add(newWm);
            }
            String newJson = null;
            try {
                newJson = objectMapper.writeValueAsString((Object)wc);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            if (newJson != null) {
                PluginSettingsUtil.save("workflow-mapping-json", newJson);
                WorkflowSyncServiceImpl.cache = this.readWorkflowConfigurationObject();
            }
        }
    }
    
    @Override
    public Integer getConfigurationId() {
        Integer result = 0;
        try {
            final WorkflowConfigurationT wc = this.readWorkflowConfigurationObject();
            boolean exist;
            do {
                exist = false;
                for (final WorkflowMappingT wm : wc.getConfiguration()) {
                    if (wm.getWorkflowMappingId() == result) {
                        exist = true;
                        break;
                    }
                }
                if (exist) {
                    ++result;
                }
            } while (exist);
        }
        catch (Exception e) {
            WorkflowSyncServiceImpl.log.error("Reading workflow configuration error: " + e.getMessage());
        }
        return result;
    }
    
    @Override
    public void deleteConfigurationById(final Integer id) {
        try {
            final WorkflowConfigurationT wc = this.readWorkflowConfigurationObject();
            for (final WorkflowMappingT wm : wc.getConfiguration()) {
                if (wm.getWorkflowMappingId() == id) {
                    wc.getConfiguration().remove(wm);
                    break;
                }
            }
            final ObjectMapper objectMapper = new ObjectMapper();
            final String json = objectMapper.writeValueAsString((Object)wc);
            if (json != null) {
                PluginSettingsUtil.save("workflow-mapping-json", json);
                WorkflowSyncServiceImpl.cache = this.readWorkflowConfigurationObject();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isValidMappingName(final String mappingName) {
        if (mappingName != null && !mappingName.isEmpty()) {
            final List<WorkflowMappingT> mappings = this.getCache().getConfiguration();
            if (mappings != null) {
                for (final WorkflowMappingT mapping : mappings) {
                    if (mappingName.equals(mapping.getWorkflowMappingDisplayName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean getOutgoingTransitionResolution(final Integer workflowMappingId, final Integer prevStatus, final Integer currStatus) {
        final WorkflowMappingT mapping = this.getWorkflowMapping(workflowMappingId);
        if (mapping != null && mapping.getOutgoingTransitions() != null && prevStatus != null && currStatus != null) {
            for (final WorkflowOutgoingTransitionsT t : mapping.getOutgoingTransitions()) {
                if (currStatus.equals(t.getCurrStatusId()) && prevStatus.equals(t.getPrevStatusId())) {
                    return t.getResolution();
                }
            }
            for (final WorkflowOutgoingTransitionsT t : mapping.getOutgoingTransitions()) {
                if (currStatus.equals(t.getCurrStatusId()) && t.getPrevStatusId() == -1) {
                    return t.getResolution();
                }
            }
        }
        return false;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)WorkflowSyncServiceImpl.class);
        WorkflowSyncServiceImpl.cache = null;
    }
}
