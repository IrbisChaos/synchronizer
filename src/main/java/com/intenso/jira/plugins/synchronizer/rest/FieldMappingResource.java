// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import com.intenso.jira.plugins.synchronizer.rest.model.FieldMappingRestrictedT;
import com.intenso.jira.plugins.synchronizer.entity.RemoteFieldMapping;
import com.atlassian.jira.rest.api.http.CacheControl;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import com.intenso.jira.plugins.synchronizer.entity.FieldMapping;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldMappingT;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldMappingEntryTBuilder;
import com.intenso.jira.plugins.synchronizer.rest.model.FieldMappingEntryT;
import com.intenso.jira.plugins.synchronizer.entity.FieldMappingEntry;
import java.util.ArrayList;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import com.intenso.jira.plugins.synchronizer.service.RemoteFieldMappingService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingEntryService;
import com.intenso.jira.plugins.synchronizer.service.FieldMappingService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/fieldMapping")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class FieldMappingResource
{
    private FieldMappingService fieldMappingService;
    private FieldMappingEntryService fieldMappingEntryService;
    private RemoteFieldMappingService remoteConfigurationService;
    
    public FieldMappingResource(final FieldMappingService fieldMappingService, final FieldMappingEntryService fieldMappingEntryService, final RemoteFieldMappingService remoteConfigurationService) {
        this.fieldMappingService = fieldMappingService;
        this.fieldMappingEntryService = fieldMappingEntryService;
        this.remoteConfigurationService = remoteConfigurationService;
    }
    
    @GET
    @Path("/fields")
    public Response getFieldMappingAll(@Context final HttpServletRequest request) {
        final String term = request.getParameter("q");
        final Map<String, String> fields = FieldMappingUtils.prepareFields();
        final Map<String, String> result = new HashMap<String, String>();
        for (final String key : fields.keySet()) {
            if (key.toUpperCase().contains(term.toUpperCase()) || fields.get(key).toUpperCase().contains(term.toUpperCase()) || term == null || term.isEmpty()) {
                result.put(key, fields.get(key));
            }
        }
        return Response.ok((Object)result).build();
    }
    
    @GET
    @Path("/template/{templateId}")
    public Response getFieldMappingFromTemplate(@PathParam("templateId") final Integer templateId) {
        List<FieldMappingEntry> entries = this.fieldMappingEntryService.findByMapping(templateId);
        if (entries == null) {
            entries = new ArrayList<FieldMappingEntry>();
        }
        final List<FieldMappingEntryT> entriesT = new ArrayList<FieldMappingEntryT>();
        for (final FieldMappingEntry fme : entries) {
            entriesT.add(new FieldMappingEntryTBuilder(fme).build());
        }
        return Response.ok((Object)entriesT).build();
    }
    
    @Deprecated
    @GET
    @Path("/list")
    public Response getFieldMappingForConnection() {
        final List<FieldMapping> mappingsForConnection = this.fieldMappingService.getAll();
        final Map<Integer, FieldMappingT> mappings = new HashMap<Integer, FieldMappingT>();
        final Map<Integer, List<FieldMappingEntryT>> entries = new HashMap<Integer, List<FieldMappingEntryT>>();
        for (final FieldMapping fm : mappingsForConnection) {
            mappings.put(fm.getID(), new FieldMappingT(Integer.valueOf(fm.getID()), fm.getName()));
            final List<FieldMappingEntry> fieldset = this.fieldMappingEntryService.findByMapping(fm.getID());
            final List<FieldMappingEntryT> fieldsetT = new ArrayList<FieldMappingEntryT>();
            for (final FieldMappingEntry e : fieldset) {
                fieldsetT.add(new FieldMappingEntryTBuilder(e).build());
            }
            entries.put(fm.getID(), fieldsetT);
        }
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("mappings", mappings);
        result.put("setForMapping", entries);
        return Response.ok((Object)result).build();
    }
    
    @POST
    public Response saveOrUpdateFieldMapping(final FieldMappingT mapping) {
        FieldMapping result = null;
        if (mapping.getId() != null) {
            result = this.fieldMappingService.get(mapping.getId());
        }
        if (result != null) {
            result.setName(mapping.getName());
            this.fieldMappingService.save(result);
        }
        else {
            result = this.fieldMappingService.save(mapping.getName());
        }
        return Response.ok((Object)new FieldMappingT(result)).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteFieldMapping(@PathParam("id") final Integer id) {
        if (id == null) {
            return Response.serverError().status(500).build();
        }
        final FieldMapping mapping = this.fieldMappingService.get(id);
        if (mapping == null) {
            return Response.serverError().status(404).build();
        }
        this.fieldMappingService.delete(mapping);
        final List<FieldMappingEntry> entries = this.fieldMappingEntryService.findByMapping(id);
        for (final FieldMappingEntry fe : entries) {
            this.fieldMappingEntryService.delete(fe);
        }
        return Response.ok().build();
    }
    
    @DELETE
    @Path("/entry/{id}")
    public Response deleteFieldMappingEntry(@PathParam("id") final Integer id) {
        if (id == null) {
            return Response.serverError().status(500).build();
        }
        final FieldMappingEntry fme = this.fieldMappingEntryService.get(id);
        if (fme == null) {
            return Response.serverError().status(404).build();
        }
        this.fieldMappingEntryService.delete(fme);
        final Response response = Response.ok().cacheControl(CacheControl.never()).build();
        return response;
    }
    
    @POST
    @Path("/entry")
    public Response saveOrUpdateFieldMappingEntry(final FieldMappingEntryT entry) {
        FieldMappingEntry mapping = null;
        if (entry.getId() != null) {
            mapping = this.fieldMappingEntryService.get(entry.getId());
            mapping.setLocalFieldId(entry.getLocalFieldId());
            mapping.setLocalFieldName(entry.getLocalFieldName());
            this.fieldMappingEntryService.save(mapping);
        }
        else {
            mapping = this.fieldMappingEntryService.save(entry.getFieldMappingId(), entry.getLocalFieldId(), entry.getLocalFieldName(), entry.getRemoteFieldName());
        }
        return Response.ok((Object)new FieldMappingEntryTBuilder(mapping).build()).build();
    }
    
    @GET
    @Path("/remote/{connection}")
    public Response getFieldMappingFromRemote(@PathParam("connection") final Integer connectionId, @Context final HttpServletRequest request) {
        if (connectionId == null) {
            return Response.serverError().status(404).build();
        }
        final String remoteContractName = request.getParameter("remoteContractName");
        final List<RemoteFieldMapping> config;
        synchronized (RemoteFieldMapping.class) {
            config = this.remoteConfigurationService.findByContractAndConnection(remoteContractName, connectionId);
        }
        final List<FieldMappingRestrictedT> result = new ArrayList<FieldMappingRestrictedT>();
        if (config != null) {
            final String term = request.getParameter("q");
            for (final RemoteFieldMapping rc : config) {
                if (term == null || term.isEmpty() || rc.getFieldName().contains(term)) {
                    result.add(new FieldMappingRestrictedT(rc));
                }
            }
        }
        return Response.ok((Object)result).build();
    }
}
